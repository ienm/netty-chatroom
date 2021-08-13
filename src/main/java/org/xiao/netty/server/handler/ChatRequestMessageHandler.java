package org.xiao.netty.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.xiao.netty.message.ChatRequestMessage;
import org.xiao.netty.message.ChatResponseMessage;
import org.xiao.netty.server.session.SessionFactory;

@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ChatRequestMessage chatRequestMessage) throws Exception {
        String to = chatRequestMessage.getTo();
        String from = chatRequestMessage.getFrom();
        String content = chatRequestMessage.getContent();
        Channel chatChannel = SessionFactory.getSession().getChannel(to);
        if (chatChannel != null){
            chatChannel.writeAndFlush(new ChatResponseMessage(from,content));
        }
        //不在线
        else {
            channelHandlerContext.writeAndFlush(new ChatResponseMessage(false,"对方用户不在线"));
        }
    }
}
