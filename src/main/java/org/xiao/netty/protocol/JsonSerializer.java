package org.xiao.netty.protocol;

import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;

public class JsonSerializer implements Serializer{
    @Override
    public <T> T deserialize(Class<T> classz, byte[] bytes) {
        String jsonString = new String(bytes, StandardCharsets.UTF_8);
        T object = new Gson().fromJson(jsonString, classz);
        return object;
    }

    @Override
    public <T> byte[] serialize(T object) {
        String jsonString = new Gson().toJson(object);
        return jsonString.getBytes(StandardCharsets.UTF_8);
    }
}
