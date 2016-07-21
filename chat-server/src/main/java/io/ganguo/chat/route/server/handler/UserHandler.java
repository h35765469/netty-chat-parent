package io.ganguo.chat.route.server.handler;

import io.ganguo.chat.route.biz.bean.ClientType;
import io.ganguo.chat.route.biz.entity.Friend;
import io.ganguo.chat.route.biz.entity.Login;
import io.ganguo.chat.route.biz.entity.OfflineMessage;
import io.ganguo.chat.route.biz.entity.User;
import io.ganguo.chat.route.biz.service.impl.FriendServiceImpl;
import io.ganguo.chat.route.biz.service.impl.UserServiceImpl;
import io.ganguo.chat.core.connetion.IMConnection;
import io.ganguo.chat.core.handler.IMHandler;
import io.ganguo.chat.core.protocol.Commands;
import io.ganguo.chat.core.protocol.Handlers;
import io.ganguo.chat.core.transport.Header;
import io.ganguo.chat.core.transport.IMRequest;
import io.ganguo.chat.core.transport.IMResponse;
import io.ganguo.chat.route.server.dto.FriendDTO;
import io.ganguo.chat.route.server.dto.LoginDTO;
import io.ganguo.chat.route.server.dto.OfflineMessageDTO;
import io.ganguo.chat.route.server.dto.UserDTO;
import io.ganguo.chat.route.server.session.ClientSession;
import io.ganguo.chat.route.server.session.ClientSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Tony
 * @createAt Feb 17, 2015
 */
@Component
public class UserHandler extends IMHandler<IMRequest> {
    private Logger logger = LoggerFactory.getLogger(UserHandler.class);

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private FriendServiceImpl friendService;




    @Override
    public short getId() {
        return Handlers.USER;
    }

    @Override
    public void dispatch(IMConnection connection, IMRequest request) {
        Header header = request.getHeader();
        switch (header.getCommandId()) {
            case Commands.LOGIN_REQUEST: {
                login(connection, request);
                break;
            }
            case Commands.LOGIN_CHANNEL_REQUEST:
                loginChannel(connection, request);
                break;
            case Commands.FRIEND_REQUEST:
                receiveFriend(connection, request);
            default:
                connection.close();
                break;
        }
    }

    private void loginChannel(IMConnection connection, IMRequest request) {
        LoginDTO loginDTO = request.readEntity(LoginDTO.class);
        Login login = loginDTO.getLogin();

        boolean isSuccess = userService.authenticate(login.getUin(), login.getAuthToken());
        IMResponse resp = new IMResponse();
        Header header = request.getHeader();
        if (isSuccess) {
            User user = userService.findByUin(login.getUin());

            header.setHandlerId(getId());
            header.setCommandId(Commands.LOGIN_CHANNEL_SUCCESS);
            resp.setHeader(header);
            resp.writeEntity(new UserDTO(user));
            connection.sendResponse(resp);

            // 是否已經登入，踢下线
            //ClientSession old = ClientSessionManager.getInstance().get(login.getUin());
            ClientSession old = ClientSessionManager.getInstance().get(login.getAccount());

            if (old != null && old.getConnection() != connection) {
                kick(old.getConnection(), request);
            }
            // 绑定用户UIN到connection中
            ClientSession session = new ClientSession(login, connection);
            ClientSessionManager.getInstance().add(session);
        } else {
            header.setHandlerId(getId());
            header.setCommandId(Commands.LOGIN_CHANNEL_FAIL);
            resp.setHeader(header);
            connection.sendResponse(resp);
            connection.close();
        }
    }

    private void login(IMConnection connection, IMRequest request) {
        UserDTO userDTO = request.readEntity(UserDTO.class);
        String account = userDTO.getUser().getAccount();
        //String password = userDTO.getUser().getPassword();
        long uin = userDTO.getUser().getUin();
        //userService.register(account,password , uin);
        userService.register(account , uin);
        //Login login = userService.login(account, password);
        Login login = userService.login(account);

        IMResponse resp = new IMResponse();
        Header header = request.getHeader();
        if (login != null) {
            header.setHandlerId(getId());
            header.setCommandId(Commands.LOGIN_SUCCESS);
            resp.setHeader(header);
            resp.writeEntity(new LoginDTO(login));
            connection.sendResponse(resp);

            // 是否已經登入(相同帳號)，踢下線
            //ClientSession old = ClientSessionManager.getInstance().get(login.getUin());
            ClientSession old = ClientSessionManager.getInstance().get(login.getAccount());
            if (old != null && old.getConnection() != connection) {
                kick(old.getConnection(), request);
            }
            // 绑定用户UIN到connection中
            ClientSessionManager.getInstance().add(new ClientSession(login, connection));
        } else {
            header.setHandlerId(getId());
            header.setCommandId(Commands.LOGIN_FAIL);
            resp.setHeader(header);
            connection.sendResponse(resp);
            connection.close();
        }
    }


    /**
     * 被踢下線
     *
     * @param connection
     */
    private void kick(IMConnection connection, IMRequest request) {
        // send 离线信息，并kill
        IMResponse resp = new IMResponse();
        Header header = request.getHeader();
        header.setHandlerId(getId());
        header.setCommandId(Commands.LOGIN_CHANNEL_KICKED);
        resp.setHeader(header);
        connection.sendResponse(resp);
        connection.close();
    }

    private void receiveFriend(IMConnection connection, IMRequest request){
        FriendDTO friendDTO = request.readEntity(FriendDTO.class);
        Friend friend = friendDTO.getFriend();
        List<Friend>friendList = friendService.getFriend(friend.getUserName());
        if(friendList !=null){
            String[] friendArray = new String[friendList.size()];
            for(int i = 0 ; i < friendList.size() ; i++){
                friendArray[i] = friendList.get(i).getFriendUserName();
            }
            friend.setFriendArray(friendArray);
        }else{
            String[] friendArray = new String[0];
            friend.setFriendArray(friendArray);
        }

        ClientSession session = ClientSessionManager.getInstance().get(friend.getUserName());
        if(session != null){
            IMResponse resp = new IMResponse();
            Header header = request.getHeader();
            header.setCommandId(Commands.FRIEND_SUCCESS);
            resp.setHeader(header);
            resp.writeEntity(new FriendDTO(friend));
            session.getConnection().sendResponse(resp);
        }
    }

}
