package org.xiao.netty.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.xiao.netty.message.Message;

import java.util.List;


@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {

    private Serializer serializer;

    public MessageCodecSharable(){
        // 默认序列化
        serializer = new JdkSerializer();
    }

    public MessageCodecSharable(Serializer serializer){
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, List<Object> list) throws Exception {
        ByteBuf byteBuf = channelHandlerContext.alloc().buffer();
        //1. 4 字节魔数
        byteBuf.writeBytes(new byte[]{1,2,3,4});
        //2. 1 字节的版本
        byteBuf.writeByte(1);
        //3. 1 字节序列化方式 0-jdk 1-json
        int version = 0;
        if (serializer instanceof JdkSerializer){
            version = 0;
        } else if (serializer instanceof JsonSerializer){
            version = 1;
        }
        byteBuf.writeByte(version);
        //4. 1 字节指令类型
        byteBuf.writeByte(message.getMessageType());
        //5. 4个字节 请求序号
        byteBuf.writeInt(message.getSequenceId());
        byteBuf.writeByte(0xff);//对齐填充
        //6. 消息长度

        byte[] bytes = serializer.serialize(message);
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        ObjectOutputStream oos = new ObjectOutputStream(bos);
//        oos.writeObject(message);
//        byte[] bytes = bos.toByteArray();
        byteBuf.writeInt(bytes.length);
        //7. 消息正文
        byteBuf.writeBytes(bytes);

        list.add(byteBuf);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int magicNum = byteBuf.readInt();
        byte version = byteBuf.readByte();
        byte serializerType = byteBuf.readByte();
        byte messageType = byteBuf.readByte();
        int sequenceId = byteBuf.readInt();
        byteBuf.readByte();
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes,0,length);
        Message message = null;


        if (serializerType == 0){
//            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
//            message = (Message) ois.readObject();
            message = (Message) serializer.deserialize(Message.getMessageClass(messageType), bytes);
        }

        list.add(message);
    }
}
