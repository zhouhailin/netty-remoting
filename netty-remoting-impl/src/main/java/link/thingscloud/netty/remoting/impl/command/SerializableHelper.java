/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package link.thingscloud.netty.remoting.impl.command;

import com.alibaba.fastjson.JSON;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.fury.Fury;
import io.fury.config.Language;
import link.thingscloud.netty.remoting.api.command.SerializableType;
import link.thingscloud.netty.remoting.api.exception.RemotingSerializableException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author zhouhailin
 * @since 0.8.0
 */
public class SerializableHelper {

    private SerializableHelper() {
    }

    public static byte[] serialize(SerializableType serializableType, final Object obj) {
        if (obj == null) {
            return null;
        }
        switch (serializableType) {
            case JSON:
                return JSONSerializer.serialize(obj);
            case Kryo:
                return KryoSerializer.serialize(obj);
            case Fury:
                return FurySerializer.serialize(obj);
            case Hessian:
                return HessianSerializer.serialize(obj);
            case Jdk:
                return JdkSerializer.serialize(obj);
            default:
                return null;
        }
    }

    public static <T> T deserialize(SerializableType serializableType, byte[] data, Class<T> classOfT) {
        switch (serializableType) {
            case JSON:
                return JSONSerializer.deserialize(data, classOfT);
            case Kryo:
                return KryoSerializer.deserialize(data, classOfT);
            case Fury:
                return FurySerializer.deserialize(data, classOfT);
            case Hessian:
                return HessianSerializer.deserialize(data, classOfT);
            case Jdk:
                return JdkSerializer.deserialize(data, classOfT);
            default:
                return null;
        }
    }

    static class JSONSerializer {

        public static byte[] serialize(Object obj) {
            return JSON.toJSONBytes(obj);
        }

        public static <T> T deserialize(byte[] data, Class<T> classOfT) {
            return JSON.parseObject(data, classOfT);
        }
    }

    static class KryoSerializer {
        public static byte[] serialize(Object obj) {
            Kryo kryo = new Kryo();
            kryo.register(obj.getClass());
            byte[] buffer = new byte[1024];
            kryo.writeObject(new Output(buffer), obj);
            return buffer;
        }

        public static <T> T deserialize(byte[] data, Class<T> classOfT) {
            Kryo kryo = new Kryo();
            kryo.register(classOfT);
            return kryo.readObject(new Input(data), classOfT);
        }
    }

    static class FurySerializer {

        static final Fury FURY = Fury.builder().withLanguage(Language.JAVA).build();

        public static byte[] serialize(Object obj) {
            FURY.register(obj.getClass());
            return FURY.serialize(obj);
        }

        public static <T> T deserialize(byte[] data, Class<T> classOfT) {
            FURY.register(classOfT);
            return (T) FURY.deserialize(data);
        }
    }

    static class HessianSerializer {

        static final SerializerFactory SERIALIZER_FACTORY = new SerializerFactory();

        public static byte[] serialize(Object obj) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            Hessian2Output hout = new Hessian2Output(bout);
            hout.setSerializerFactory(SERIALIZER_FACTORY);
            try {
                hout.writeObject(obj);
                hout.close();
            } catch (IOException e) {
                throw new RemotingSerializableException("", e);
            }
            return bout.toByteArray();
        }

        public static <T> T deserialize(byte[] data, Class<T> classOfT) {
            ByteArrayInputStream bin = new ByteArrayInputStream(data, 0, data.length);
            Hessian2Input hin = new Hessian2Input(bin);
            hin.setSerializerFactory(new SerializerFactory());
            Object out;
            try {
                out = hin.readObject();
                hin.close();
            } catch (IOException e) {
                throw new RemotingSerializableException("", e);
            }
            return (T) out;
        }
    }

    static class JdkSerializer {

        public static byte[] serialize(Object obj) {
            return null;
        }

        public static <T> T deserialize(byte[] data, Class<T> classOfT) {
            return null;
        }
    }

}
