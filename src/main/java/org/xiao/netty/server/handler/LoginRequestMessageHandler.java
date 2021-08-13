package org.xiao.netty.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.xiao.netty.message.LoginRequestMessage;
import org.xiao.netty.message.LoginResponseMessage;
import org.xiao.netty.server.service.UserServiceFactory;
import org.xiao.netty.server.session.SessionFactory;

@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LoginRequestMessage loginRequestMessage) throws Exception {
        String username = loginRequestMessage.getUsername();
        String password = loginRequestMessage.getPassword();
        boolean isLogin = UserServiceFactory.getUserService().login(username, password);
        LoginResponseMessage message = null;
        if (isLogin) {
            SessionFactory.getSession().bind(channelHandlerContext.channel(), username);
            message = new LoginResponseMessage(true, "登陆成功");
        } else {
            message = new LoginResponseMessage(false, "用户名或密码错误");
        }
        channelHandlerContext.writeAndFlush(message);
    }
}
