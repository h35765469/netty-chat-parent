package io.ganguo.chat.route.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;

/**
 * Chat Server
 */
@Component
public class ChatServer {

    private final int PORT;

    public ChatServer() {
        PORT = 8080;
    }

    public ChatServer(int port) {
        PORT = port;
    }

    /**
     * netty
     *
     * @throws Exception
     */
    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();//接收進來的連接
        EventLoopGroup workGroup = new NioEventLoopGroup();//處理接收進來的連接

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)//使用非nio(異步處理來進行)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_RCVBUF,1048576)
                    .childHandler(new ChatServerInitializer());//添加處理器到channelpipeline

            bootstrap
                    .bind("192.168.43.157",PORT)
                    .sync().channel()
                    .closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

}
