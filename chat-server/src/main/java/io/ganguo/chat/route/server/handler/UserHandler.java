package io.ganguo.chat.route.server.handler;

import io.ganguo.chat.route.biz.bean.ClientType;
import io.ganguo.chat.route.biz.entity.Friend;
import io.ganguo.chat.route.biz.entity.Login;
import io.ganguo.chat.route.biz.entity.OfflineMessage;
import io.ganguo.chat.route.biz.entity.User;
import io.ganguo.chat.route.biz.service.impl.FriendServiceImpl;
import io.ganguo.chat.route.biz.service.impl.MessageServiceImpl;
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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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

    @Autowired
    private MessageServiceImpl messageService;




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
                break;
            case Commands.FRIEND_ADD_REQUEST:
                requestFriend(connection, request);
                break;
            case Commands.FRIEND_ADD_SUCCESS:
                addFriend(connection,request);
                break;
            case Commands.FRIEND_REJECT_REQUEST:
                rejectFriend(connection, request);
                break;
            case Commands.FRIEND_SEARCH_REQUEST:
                searchFriend(connection,request);
                break;
            case Commands.FRIEND_REMOVE_REQUEST:
                removeFriend(connection,request);
                break;
            case Commands.FRIEND_FAVORITE_REQUEST:
                favoriteFriend(connection,request);
                break;
            case Commands.FRIEND_BLOCK_REQUEST:
                blockFriend(connection,request);
                break;
            case Commands.FRIEND_VIEWER_REQUEST:
                viewFriend(connection,request);
                break;
            case Commands.FRIEND_EDITNAME_REQUEST:
                editFriendName(connection,request);
                break;
            case Commands.FRIEND_NEWFRIENDCHECK_REQUEST:
                checkNewFriend(connection, request);
                break;
            case Commands.USER_AVATAR_REQUEST:
                updateAvatar(connection, request);
                break;
            case Commands.USER_NAME_REQUEST:
                updateNickName(connection, request);
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
        long uin = userDTO.getUser().getUin();
        String nickName = userDTO.getUser().getNickName();
        userService.register(account, uin, nickName);
        System.out.println("nickName " + nickName);
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
        List<Friend>friendList = friendService.getFriend(friend.getId());
        String userName = userService.getUserName(friend.getId());

        if(friendList !=null){
            String[] friendIdArray = new String[friendList.size()];
            String[] friendArray = new String[friendList.size()];
            String[] friendNameArray = new String[friendList.size()];
            String[] friendAvatarUriArray = new String[friendList.size()];
            int[] favoriteArray = new int[friendList.size()];
            int[] viewArray = new int[friendList.size()];
            int[] statusArray = new int[friendList.size()];


            for(int i = 0 ; i < friendList.size() ; i++){
                friendIdArray[i] = friendList.get(i).getFriendObject().getId();
                friendArray[i] = friendList.get(i).getFriendObject().getAccount();
                if(friendList.get(i).getFriendName().equals("")){
                    friendNameArray[i] = friendList.get(i).getFriendObject().getNickName();
                }else{
                    friendNameArray[i] = friendList.get(i).getFriendName();
                }
                friendAvatarUriArray[i] =   friendList.get(i).getFriendObject().getAvatarUrl();
                favoriteArray[i] = friendList.get(i).getIsFavorite();
                viewArray[i] = friendList.get(i).getViewer();
                statusArray[i] = friendList.get(i).getStatus();
            }
            friend.setFriendIdArray(friendIdArray);
            friend.setFriendArray(friendArray);
            friend.setFriendNameArray(friendNameArray);
            friend.setFavoriteArray(favoriteArray);
            friend.setViewerArray(viewArray);
            friend.setStatusArray(statusArray);
            friend.setFriendAvatarUriArray(friendAvatarUriArray);
        }else{
            String[] friendArray = new String[0];
            friend.setFriendArray(friendArray);
        }

        ClientSession session = ClientSessionManager.getInstance().get(userName);
        if(session != null){
            IMResponse resp = new IMResponse();
            Header header = request.getHeader();
            header.setCommandId(Commands.FRIEND_SUCCESS);
            resp.setHeader(header);
            resp.writeEntity(new FriendDTO(friend));
            session.getConnection().sendResponse(resp);
        }
    }

    private void requestFriend(IMConnection connection , IMRequest request){
        FriendDTO friendDTO = request.readEntity(FriendDTO.class);
        Friend friend = friendDTO.getFriend();
        User userData = userService.getUserData(friend.getId());//獲取朋友資料(發出邀請朋友者)
        User friendData = userService.getUserData(friend.getFriendId());//接收朋友邀請者;

        friendService.saveFriend(userData.getId(), friendData.getId(), 0);//先將邀請人狀態存入mongodb中
        ClientSession session = ClientSessionManager.getInstance().get(friendData.getAccount());
        if(session != null){
            IMResponse resp = new IMResponse();
            Header header = request.getHeader();
            resp.setHeader(header);
            Friend friendReverse = new Friend();//傳過去後代表傳送者變成friend
            friendReverse.setFriendId(userData.getId());
            friendReverse.setFriendUserName(userData.getAccount());
            friendReverse.setFriendName(userData.getNickName());
            friendReverse.setFriendAvatarUri(userData.getAvatarUrl());
            friendDTO.setFriend(friendReverse);
            resp.writeEntity(friendDTO);
            System.out.println("FuckDto " + friendDTO.getFriend().getFriendAvatarUri());
            session.getConnection().sendResponse(resp);
        }
    }

    private void addFriend(IMConnection connection , IMRequest request){
        FriendDTO friendDTO = request.readEntity(FriendDTO.class);
        Friend friend = friendDTO.getFriend();
        User userData = userService.getUserData(friend.getId());
        User friendData = userService.getUserData(friend.getFriendId());
        friendService.saveFriend(userData.getId(), friendData.getId(),1);//受邀人答應後再將他加入mongodb中
        friendService.updateFriendStatus(userData.getId(), friendData.getId(), 1);//更改邀請者的status為1
        ClientSession session = ClientSessionManager.getInstance().get(friendData.getAccount());
        if(session != null){
            IMResponse resp = new IMResponse();
            Header header = request.getHeader();
            resp.setHeader(header);
            Friend friendReverse = new Friend();//受邀人傳過去後轉變成朋友
            friendReverse.setFriendId(userData.getId());
            friendReverse.setFriendName(userData.getNickName());
            friendReverse.setFriendAvatarUri(userData.getAvatarUrl());
            friendDTO.setFriend(friendReverse);
            resp.writeEntity(friendDTO);
            session.getConnection().sendResponse(resp);
        }else{
            messageService.SaveOfflineMessage(userData.getId(), friendData.getId(), "開心成為你朋友", System.currentTimeMillis(), (byte)3, -3);
        }
    }

    //拒絕朋友邀請
    private void rejectFriend(IMConnection connection, IMRequest request){
        FriendDTO friendDTO = request.readEntity(FriendDTO.class);
        Friend friend = friendDTO.getFriend();
        ClientSession session = ClientSessionManager.getInstance().get(friend.getFriendUserName());
        friendService.removeFriend(friend.getFriendId(), friend.getId());

        if(session != null){
            IMResponse resp = new IMResponse();
            Header header = request.getHeader();
            header.setCommandId(Commands.FRIEND_REJECT_REQUEST);
            resp.setHeader(header);
            resp.writeEntity(new FriendDTO(friend));
            session.getConnection().sendResponse(resp);
        }else {
            messageService.SaveOfflineMessage(friend.getId(), friend.getFriendId(), "拒絕成為你朋友", System.currentTimeMillis(), (byte)4, -3);
        }
    }

    private void searchFriend(IMConnection connection , IMRequest request){
        FriendDTO friendDTO = request.readEntity(FriendDTO.class);
        Friend friend = friendDTO.getFriend();
        List<User>searchList = friendService.searchFriendName(friend.getFriendName());
        if(!searchList.isEmpty()) {
            String[] searchFriendIdArray = new String[searchList.size()];
            String[] searchFriendArray = new String[searchList.size()];
            String[] searchFriendNameArray = new String[searchList.size()];
            String[] searchFriendAvatarArray = new String[searchList.size()];
            for (int i = 0; i < searchFriendArray.length; i++) {
                searchFriendIdArray[i] = searchList.get(i).getId();
                searchFriendArray[i] = searchList.get(i).getAccount();
                searchFriendNameArray[i] = searchList.get(i).getNickName();
                searchFriendAvatarArray[i] = searchList.get(i).getAvatarUrl();
            }
            friend.setFriendIdArray(searchFriendIdArray);
            friend.setFriendArray(searchFriendArray);
            friend.setFriendNameArray(searchFriendNameArray);
            friend.setFriendAvatarUriArray(searchFriendAvatarArray);
            String username = userService.getUserName(friend.getId());
            ClientSession session = ClientSessionManager.getInstance().get(username);
            if (session != null) {
                IMResponse resp = new IMResponse();
                Header header = request.getHeader();
                header.setCommandId(Commands.FRIEND_SEARCH_REQUEST);
                resp.setHeader(header);
                resp.writeEntity(new FriendDTO(friend));
                session.getConnection().sendResponse(resp);
            }
        }
    }

    //刪除好友
    private void removeFriend(IMConnection connection , IMRequest request){
        FriendDTO friendDTO  = request.readEntity(FriendDTO.class);
        Friend friend = friendDTO.getFriend();
        friendService.removeFriend(friend.getId(), friend.getFriendId());
    }

    //最愛好友
    private void favoriteFriend(IMConnection connection , IMRequest request){
        FriendDTO friendDTO = request.readEntity(FriendDTO.class);
        Friend friend = friendDTO.getFriend();
        friendService.favoriteFriend(friend.getId(), friend.getFriendId(), friend.getIsFavorite());
    }

    //封解鎖好友
    private void blockFriend(IMConnection connection , IMRequest request){
        FriendDTO friendDTO = request.readEntity(FriendDTO.class);
        Friend friend = friendDTO.getFriend();
        friendService.blockFriend(friend.getId(), friend.getFriendId(), friend.getIsBlock());
    }

    //開關觀察者
    private void viewFriend(IMConnection connection , IMRequest request){
        FriendDTO friendDTO = request.readEntity(FriendDTO.class);
        Friend friend = friendDTO.getFriend();
        friendService.viewFriend(friend.getId(), friend.getFriendId(), friend.getViewer());
    }

    private void editFriendName(IMConnection connection , IMRequest request){
        FriendDTO friendDTO = request.readEntity(FriendDTO.class);
        Friend friend = friendDTO.getFriend();
        friendService.editFriendName(friend.getId(), friend.getFriendId(), friend.getFriendName());
    }

    //確認是否有人邀請我為好友
    private void checkNewFriend(IMConnection connection, IMRequest request){
        FriendDTO friendDTO = request.readEntity(FriendDTO.class);
        Friend friend = friendDTO.getFriend();
        List<Friend>pendingFriendList = friendService.findPendingFriend(friend.getId());
        if(!pendingFriendList.isEmpty()) {
            String[] friendIdArray = new String[pendingFriendList.size()];
            String[] friendArray = new String[pendingFriendList.size()];
            String[] friendNameArray = new String[pendingFriendList.size()];
            String[] friendAvatarArray = new String[pendingFriendList.size()];
            for (int i = 0; i < friendArray.length; i++) {
                //我是被邀請對象(friendObject)，但我的朋友是邀請對象(userObject)
                friendIdArray[i] = pendingFriendList.get(i).getUserObject().getId();
                friendArray[i] = pendingFriendList.get(i).getUserObject().getAccount();
                friendNameArray[i] = pendingFriendList.get(i).getUserObject().getNickName();
                friendAvatarArray[i] = pendingFriendList.get(i).getUserObject().getAvatarUrl();
            }
            friend.setFriendIdArray(friendIdArray);
            friend.setFriendArray(friendArray);
            friend.setFriendNameArray(friendNameArray);
            friend.setFriendAvatarUriArray(friendAvatarArray);
            ClientSession session = ClientSessionManager.getInstance().get(pendingFriendList.get(0).getFriendObject().getAccount());
            if (session != null) {
                IMResponse resp = new IMResponse();
                Header header = request.getHeader();
                header.setCommandId(Commands.FRIEND_NEWFRIENDCHECK_REQUEST);
                resp.setHeader(header);
                resp.writeEntity(new FriendDTO(friend));
                session.getConnection().sendResponse(resp);
            }
        }
    }



    //更新使用者大頭貼的資料
    private void updateAvatar(IMConnection connection, IMRequest request){
        UserDTO userDTO = request.readEntity(UserDTO.class);
        userService.updateAvatarUrl(userDTO.getUser().getId(), userDTO.getUser().getAvatarUrl());
    }

    //更新使用者名字
    private void updateNickName(IMConnection connection, IMRequest request){
        UserDTO userDTO = request.readEntity(UserDTO.class);
        userService.updateNickName(userDTO.getUser().getId(), userDTO.getUser().getNickName());
    }



}
