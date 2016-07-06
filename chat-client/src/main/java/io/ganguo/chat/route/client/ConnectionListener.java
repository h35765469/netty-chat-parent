package io.ganguo.chat.route.client;

import io.ganguo.chat.core.protocol.Commands;
import io.ganguo.chat.core.protocol.Handlers;
import io.ganguo.chat.core.transport.Header;
import io.ganguo.chat.core.transport.IMResponse;
import io.ganguo.chat.route.biz.bean.ClientType;
import io.ganguo.chat.route.biz.entity.User;
import io.ganguo.chat.route.server.dto.UserDTO;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;

import java.util.concurrent.TimeUnit;

/**
 * Created by user on 2016/2/5.
 */
public class ConnectionListener implements ChannelFutureListener {
    private ChatClient chatClient;
    String account;
    String password;
    Channel channel;

    public Channel getChannel(){
        return channel;
    }

    public ConnectionListener(){

    }

    public ConnectionListener(ChatClient chatClient, String account, String password){
        this.chatClient = chatClient;
       this.account = account;
        this.password = password;
    }


    public void operationComplete(ChannelFuture channelFuture) throws Exception{
            if(!channelFuture.isSuccess()){
                System.out.println("Reconnect");
                final EventLoop loop = channelFuture.channel().eventLoop();
                loop.schedule(new Runnable(){
                    public void run(){
                        chatClient.createBootstrap(new Bootstrap(),loop,account,password);
                    }
                },1L, TimeUnit.SECONDS);
            }else{
                channel = channelFuture.awaitUninterruptibly().channel();
                System.out.println("channel " + channel.isActive());
                if(channel.isActive()){
                    login(channel,account,password);
                }
            }
    }

    public void login(Channel channel,String account,String password) {
        User user = new User();
        user.setClientType(ClientType.WINDOWS.value());
        user.setAccount(account);
        user.setPassword(password);
        user.setUin(System.currentTimeMillis());

        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.USER);
        header.setCommandId(Commands.LOGIN_REQUEST);
        resp.setHeader(header);
        resp.writeEntity(new UserDTO(user));

        channel.writeAndFlush(resp).awaitUninterruptibly();
    }
}
