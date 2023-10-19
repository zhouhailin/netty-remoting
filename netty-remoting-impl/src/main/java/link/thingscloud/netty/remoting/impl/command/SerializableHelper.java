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

import com.alibaba.fastjson2.JSON;
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
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhouhailin
 * @since 0.8.0
 */
public class SerializableHelper {

    private static final List<Class<?>> DEFAULT_REGISTER_CLASS = new ArrayList<>();

    static {
        DEFAULT_REGISTER_CLASS.add(String.class);
        DEFAULT_REGISTER_CLASS.add(java.time.LocalDate.class);
        DEFAULT_REGISTER_CLASS.add(java.time.LocalTime.class);
        DEFAULT_REGISTER_CLASS.add(java.time.LocalDateTime.class);
        DEFAULT_REGISTER_CLASS.add(java.util.Date.class);
        DEFAULT_REGISTER_CLASS.add(java.sql.Timestamp.class);
        DEFAULT_REGISTER_CLASS.add(java.util.HashMap.class);
        DEFAULT_REGISTER_CLASS.add(java.util.ArrayList.class);
        DEFAULT_REGISTER_CLASS.add(java.util.HashSet.class);
        DEFAULT_REGISTER_CLASS.add(java.util.LinkedHashMap.class);
        DEFAULT_REGISTER_CLASS.add(java.util.LinkedHashSet.class);
        DEFAULT_REGISTER_CLASS.add(java.util.LinkedList.class);
        DEFAULT_REGISTER_CLASS.add(java.util.TreeMap.class);
        DEFAULT_REGISTER_CLASS.add(java.util.TreeSet.class);
    }

    private SerializableHelper() {
    }

    public static void register(Class<?> clazz) {
        if (DEFAULT_REGISTER_CLASS.contains(clazz)) {
            return;
        }
        DEFAULT_REGISTER_CLASS.add(clazz);
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

    /**
     * <a href="https://github.com/alibaba/fastjson2">Wiki</a>
     */
    static class JSONSerializer {

        public static byte[] serialize(Object obj) {
            return JSON.toJSONBytes(obj);
        }

        public static <T> T deserialize(byte[] data, Class<T> classOfT) {
            return JSON.parseObject(data, classOfT);
        }
    }


    /**
     * <a href="https://github.com/EsotericSoftware/kryo">Wiki</a>
     */
    static class KryoSerializer {

        static final Kryo KRYO = new Kryo();
        static final List<Class<?>> KRYO_REGISTER = new ArrayList<>();

        private static void tryRegister(Class<?> clazz) {
            try {
                if (KRYO_REGISTER.contains(clazz)) {
                    return;
                }
                KRYO_REGISTER.add(clazz);
                KRYO.register(clazz);

                for (Class<?> clazz0 : DEFAULT_REGISTER_CLASS) {
                    tryRegister(clazz0);
                }
                for (Field field : FieldUtils.getAllFields(clazz)) {
                    Class<?> type = field.getType();
                    if (type.isPrimitive() && DEFAULT_REGISTER_CLASS.contains(type)) {
                        continue;
                    }
                    tryRegister(type);
                }
            } catch (Exception e) {
                throw new RemotingSerializableException("Kryo register failed", e);
            }
        }

        public static byte[] serialize(Object obj) {
//            Kryo kryo = new Kryo();
            tryRegister(obj.getClass());
            byte[] buffer = new byte[1024];
            KRYO.writeClassAndObject(new Output(buffer), obj);
            return buffer;
        }

        public static <T> T deserialize(byte[] data, Class<T> classOfT) {
//            Kryo kryo = new Kryo();
            tryRegister(classOfT);
            return (T) KRYO.readClassAndObject(new Input(data));
        }
    }

    /**
     * <a href="https://github.com/alipay/fury">Wiki</a>
     */
    static class FurySerializer {

        static final Fury FURY = Fury.builder().withLanguage(Language.JAVA).build();
        static final List<Class<?>> FURY_REGISTER = new ArrayList<>();

        private static void tryRegister(Class<?> clazz) {
            try {
                if (FURY_REGISTER.contains(clazz)) {
                    return;
                }
                FURY_REGISTER.add(clazz);
                FURY.register(clazz);
                for (Class<?> clazz0 : DEFAULT_REGISTER_CLASS) {
                    tryRegister(clazz0);
                }
                for (Field field : FieldUtils.getAllFields(clazz)) {
                    Class<?> type = field.getType();
                    if (type.isPrimitive() && DEFAULT_REGISTER_CLASS.contains(type)) {
                        continue;
                    }
                    tryRegister(type);
                }
            } catch (Exception e) {
                throw new RemotingSerializableException("Fury register failed", e);
            }
        }

        public static byte[] serialize(Object obj) {
            tryRegister(obj.getClass());
            return FURY.serialize(obj);
        }

        public static <T> T deserialize(byte[] data, Class<T> classOfT) {
            tryRegister(classOfT);
            return (T) FURY.deserialize(data);
        }
    }

    /**
     * <a href="https://github.com/sofastack/sofa-hessian/wiki/UserGuide">Wiki</a>
     */
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
                throw new RemotingSerializableException("Hessian Serializer failed", e);
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
                throw new RemotingSerializableException("Hessian Deserializer failed", e);
            }
            return (T) out;
        }
    }

    static class JdkSerializer {

        public static byte[] serialize(Object obj) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            try {
                ObjectOutputStream out = new ObjectOutputStream(bout);
                out.writeObject(obj);
                out.close();
            } catch (IOException e) {
                throw new RemotingSerializableException("Jdk Serializer failed", e);
            }
            return bout.toByteArray();
        }

        public static <T> T deserialize(byte[] data, Class<T> classOfT) {
            ByteArrayInputStream bin = new ByteArrayInputStream(data, 0, data.length);
            Object out;
            try {
                ObjectInputStream in = new ObjectInputStream(bin);
                out = in.readObject();
                in.close();
            } catch (IOException | ClassNotFoundException e) {
                throw new RemotingSerializableException("Jdk Deserializer failed", e);
            }
            return (T) out;
        }
    }

}
