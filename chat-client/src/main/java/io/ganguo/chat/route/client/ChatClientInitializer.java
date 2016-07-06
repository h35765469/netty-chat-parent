package io.ganguo.chat.route.client;

import io.ganguo.chat.core.handler.IMHandler;
import io.ganguo.chat.route.client.handler.Client_MessageHandler;
import io.ganguo.chat.route.client.handler.Client_UserHandler;
import io.ganguo.chat.core.codec.PacketDecoder;
import io.ganguo.chat.core.codec.PacketEncoder;
import io.ganguo.chat.core.handler.IMHandlerManager;
import io.ganguo.chat.route.server.ChatContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.util.Map;

public class ChatClientInitializer extends ChannelInitializer<SocketChannel> {

    ChatClient chatClient;
    String account , password;

    public ChatClientInitializer(ChatClient chatClient,String account,String password){
        this.chatClient = chatClient;
        this.account = account;
        this.password = password;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast("decoder", new PacketDecoder(Integer.MAX_VALUE, 0, 4));
        pipeline.addLast("encoder", new PacketEncoder());

        pipeline.addLast("handler", new ChatClientHandler(chatClient,account,password));

        initIMHandler();
    }

    private void initIMHandler() {
        Map<String, IMHandler> handlers = ChatContext.getBeansOfType(IMHandler.class);
        for (String key : handlers.keySet()) {
            IMHandler handler = handlers.get(key);
            if(!key.equals("messageHandler")){
                if(!key.equals("userHandler")) {
                    IMHandlerManager.getInstance().register(handler);
                }
            }
        }
    }
}
