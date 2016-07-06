package io.ganguo.chat.route.client;

import io.ganguo.chat.core.connetion.IMConnection;
import io.ganguo.chat.core.handler.IMHandler;
import io.ganguo.chat.core.handler.IMHandlerManager;
import io.ganguo.chat.core.transport.Header;
import io.ganguo.chat.core.transport.IMRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ChatClientHandler extends SimpleChannelInboundHandler<IMRequest> {
    private Logger logger = LoggerFactory.getLogger(ChatClientHandler.class);

    private IMConnection mConnection = null;

    private ChatClient chatClient;
    private String account,password;

    public ChatClientHandler(ChatClient chatClient,String account,String password){
        this.chatClient = chatClient;
        this.account = account;
        this.password = password;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        logger.info("handlerAdded");

        if (mConnection != null) {
            mConnection.close();
            mConnection = null;
        }
        mConnection = new IMConnection(0L, ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        logger.info("handlerRemoved");
        mConnection = null;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception{
        final EventLoop eventLoop = ctx.channel().eventLoop();
        eventLoop.schedule(new Runnable(){

            public void run(){
                chatClient.createBootstrap(new Bootstrap(),eventLoop,account,password);
            }
        },1L, TimeUnit.SECONDS);
        super.channelInactive(ctx);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, IMRequest request) throws Exception {
        logger.info("messageReceived");

        Header header = request.getHeader();
        IMHandler handler = IMHandlerManager.getInstance().find(header.getHandlerId());
        if (handler != null) {
            handler.dispatch(mConnection, request);
        } else {
            logger.warn("Not found handlerId: " + header.getHandlerId());
        }
    }

}
