package io.ganguo.chat.route.client;

import io.ganguo.chat.route.biz.bean.ClientType;
import io.ganguo.chat.route.biz.entity.User;
import io.ganguo.chat.core.protocol.Handlers;
import io.ganguo.chat.core.protocol.Commands;
import io.ganguo.chat.core.transport.Header;
import io.ganguo.chat.core.transport.IMResponse;
import io.ganguo.chat.route.biz.service.impl.MessageServiceImpl;
import io.ganguo.chat.route.client.handler.Client_MessageHandler;
import io.ganguo.chat.route.client.handler.Client_UserHandler;
import io.ganguo.chat.route.server.dto.UserDTO;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


/**
 * @author Tony
 * @createAt Feb 17, 2015
 */
@Component
public class ChatClient {

    private final String host;
    private final int port;
    EventLoopGroup group;

    public ChatClient() {
        host = "192.168.43.157";
        port = 8080;
        group = new NioEventLoopGroup();
    }

    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run(String account,String password) throws Exception {
        createBootstrap(new Bootstrap(),group,account,password);
        //EventLoopGroup group = new NioEventLoopGroup();
        /*try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChatClientInitializer());

            ChannelFuture future = bootstrap.connect(host, port);
            // awaitUninterruptibly() 等待連接成功
            Channel channel = future.awaitUninterruptibly().channel();
            login(channel,account,password);


//            future.channel().closeFuture().awaitUninterruptibly();
        } finally {
           group.shutdownGracefully();
        }*/
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

    public void createBootstrap(Bootstrap bootstrap , EventLoopGroup group,String account , String password){
        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChatClientInitializer(this, account, password));


            bootstrap.remoteAddress(host, port);
            ChannelFuture future = bootstrap.connect().addListener(new ConnectionListener(this, account, password));
            // awaitUninterruptibly() 等待連接成功
            ConnectionListener listener = new ConnectionListener(this,account,password);
            //Channel channel = future.awaitUninterruptibly().channel();
            /*if(channel.isActive()){
                login(channel, account, password);
            }*/
        }catch(Exception e){

        }
    }
}
