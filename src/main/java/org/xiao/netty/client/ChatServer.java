package org.xiao.netty.client;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import org.xiao.netty.message.LoginRequestMessage;
import org.xiao.netty.message.LoginResponseMessage;
import org.xiao.netty.protocol.MessageCodecSharable;
import org.xiao.netty.protocol.ProtocolFrameDecoder;
import org.xiao.netty.server.service.UserServiceFactory;

public class ChatServer {
    public static void main(String[] args) {
        NioEventLoopGroup boos = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler LOGGINGHANDLER = new LoggingHandler();
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boos, worker);
            serverBootstrap.childHandler(new ChannelInitializer<NioServerSocketChannel>() {
                @Override
                protected void initChannel(NioServerSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
                    ch.pipeline().addLast(LOGGINGHANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    ch.pipeline().addLast(new SimpleChannelInboundHandler<LoginRequestMessage>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, LoginRequestMessage loginRequestMessage) throws Exception {
                            String username = loginRequestMessage.getUsername();
                            String password = loginRequestMessage.getPassword();
                            boolean isLogin = UserServiceFactory.getUserService().login(username, password);
                            LoginResponseMessage message = null;
                            if (isLogin){
                                message = new LoginResponseMessage(true,"登陆成功");
                            } else {
                                message = new LoginResponseMessage(false,"用户名或密码错误");
                            }
                            channelHandlerContext.writeAndFlush(message);
                        }
                    });
                }
            });
            Channel channel = serverBootstrap
                    .bind(8080)
                    .sync()
                    .channel();
            channel.closeFuture().sync();

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            boos.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
