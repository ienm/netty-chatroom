package org.xiao.netty.protocol;

import java.io.*;

public class JdkSerializer implements Serializer{
    @Override
    public <T> T deserialize(Class<T> classz, byte[] bytes) {

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            T object  = (T) ois.readObject();
            return object;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("反序列化失败",e);
        }

    }

    @Override
    public <T> byte[] serialize(T object) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("序列化失败",e);
        }

    }
}
