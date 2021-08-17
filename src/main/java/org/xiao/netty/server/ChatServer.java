package org.xiao.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.xiao.netty.server.handler.ChatRequestMessageHandler;
import org.xiao.netty.server.handler.LoginRequestMessageHandler;
import org.xiao.netty.protocol.MessageCodecSharable;
import org.xiao.netty.protocol.ProtocolFrameDecoder;
import org.xiao.netty.server.handler.QuiteHandler;
import org.xiao.netty.server.handler.group.*;

public class ChatServer {
    public static void main(String[] args) {
        NioEventLoopGroup boos = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler LOGGINGHANDLER = new LoggingHandler();
        LoginRequestMessageHandler LOGIN_HANDLER = new LoginRequestMessageHandler();
        ChatRequestMessageHandler CHAT_HANDLER = new ChatRequestMessageHandler();
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        GroupCreateRequestMessageHandler GROUP_CREATE_HANDLER = new GroupCreateRequestMessageHandler();
        GroupJoinRequestMessageHandler GROUP_JOIN_HANDLER = new GroupJoinRequestMessageHandler();
        GroupMembersRequestMessageHandler GROUP_MEMBER_HANDLER = new GroupMembersRequestMessageHandler();
        GroupQuitRequestMessageHandler GROUP_QUIT_HANDLER = new GroupQuitRequestMessageHandler();
        GroupChatRequestMessageHandler GROUP_CHAT_HANDLER = new GroupChatRequestMessageHandler();
        QuiteHandler QUITE_HANDLER = new QuiteHandler();

        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boos, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
//                    ch.pipeline().addLast(LOGGINGHANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    ch.pipeline().addLast(LOGIN_HANDLER);

                    //心跳检测
                    // 5秒内没有 收到 channel 数据 触发yige IdleState#READER—IDLE事件
                    ch.pipeline().addLast(new IdleStateHandler(5,0,0));
                    ch.pipeline().addLast(new ChannelDuplexHandler() {
                        // 触发特殊事件
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//                            super.userEventTriggered(ctx, evt);
                            IdleStateEvent even = (IdleStateEvent) evt;

                            if (even.state() == IdleState.READER_IDLE){
                                System.out.println("5s 未收到数据");
                            }
                        }
                    });

                    ch.pipeline().addLast(QUITE_HANDLER);

                    //单聊功能
                    ch.pipeline().addLast(CHAT_HANDLER);
                    //群聊功能
                    ch.pipeline().addLast(GROUP_CREATE_HANDLER);
                    ch.pipeline().addLast(GROUP_JOIN_HANDLER);
                    ch.pipeline().addLast(GROUP_CHAT_HANDLER);
                    ch.pipeline().addLast(GROUP_MEMBER_HANDLER);
                    ch.pipeline().addLast(GROUP_QUIT_HANDLER);
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
