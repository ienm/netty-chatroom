package org.xiao.netty.protocol;

public interface Serializer {
    <T> T deserialize(Class<T> classz, byte[] bytes);

    <T> byte[] serialize(T object);
}
