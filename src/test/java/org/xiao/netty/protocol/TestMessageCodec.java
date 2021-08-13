package org.xiao.netty.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import org.xiao.netty.message.LoginRequestMessage;
import org.xiao.netty.protocol.MessageCodec;
//@ChannelHandler.Sharable 拥有sharable注解标记的类都能够在并发环境下使用

public class TestMessageCodec {
    public static void main(String[] args) throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LoggingHandler(),
                new LengthFieldBasedFrameDecoder(1024,12,4,0,0),
                new MessageCodec());

        LoginRequestMessage requestMessage = new LoginRequestMessage("zhangshan","123","张山");
//        channel.writeOutbound(requestMessage);

        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null,requestMessage,buf);

        channel.writeInbound(buf);
    }
}
