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

import junit.framework.TestCase;
import link.thingscloud.netty.remoting.api.command.SerializableType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouhailin
 * @since 0.8.0
 */
public class SerializableHelperTest extends TestCase {


    @Data
    @Accessors(chain = true)
    static class Person implements java.io.Serializable{
        private String name;
        private int int1;
        private short age;
        private long timeMillis;
        private boolean sex;
        private LocalDate localDate;
        private LocalTime localTime;
        private LocalDateTime localDateTime;
        private Date date1;
        private Timestamp timestamp1;
        private Map<String, Object> map1;
        private Child child;
    }

    @Data
    @Accessors(chain = true)
    static class Child implements java.io.Serializable {
        private String name;
        private int int1;
        private short age;
        private long timeMillis;
        private boolean sex;
        private LocalDate localDate;
        private LocalTime localTime;
        private LocalDateTime localDateTime;
        private Date date1;
        private Timestamp timestamp1;
        private Map<String, Object> map1;
    }

    public Object newObject() {
        Map<String, Object> map1 = new HashMap<>();
        map1.put("hello", "child");
        Child child = new Child().setName("hello").setInt1(100).setAge((short)10).setTimeMillis(System.currentTimeMillis()).setSex(true)
                .setLocalDate(LocalDate.now()).setLocalTime(LocalTime.now()).setLocalDateTime(LocalDateTime.now())
                .setDate1(new Date()).setTimestamp1(new Timestamp(System.currentTimeMillis())).setMap1(map1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("hello", "world");

        return new Person().setName("hello").setInt1(100).setAge((short) 10).setTimeMillis(System.currentTimeMillis()).setSex(true)
                .setLocalDate(LocalDate.now()).setLocalTime(LocalTime.now()).setLocalDateTime(LocalDateTime.now())
                .setDate1(new Date()).setTimestamp1(new Timestamp(System.currentTimeMillis())).setMap1(map2)
                .setChild(child);
    }

    public void testSerializeJSON() {
        Object obj = newObject();
        byte[] serialize = SerializableHelper.serialize(SerializableType.JSON, obj);
        // System.out.println(new String(serialize));
        Person deserialize = SerializableHelper.deserialize(SerializableType.JSON, serialize, Person.class);
        // System.out.println(deserialize);
        assertNotNull(deserialize);
        assertEquals(obj.toString(), deserialize.toString());
    }

    public void testSerializeKryo() {
        Object obj = newObject();
        // System.out.println(obj);
        byte[] serialize = SerializableHelper.serialize(SerializableType.Kryo, obj);
        Person deserialize = SerializableHelper.deserialize(SerializableType.Kryo, serialize, Person.class);
        // System.out.println(deserialize);
        assertNotNull(deserialize);
        assertEquals(obj.toString(), deserialize.toString());
    }

    public void testSerializeFury() {
        Object obj = newObject();
        // System.out.println(obj);
        byte[] serialize = SerializableHelper.serialize(SerializableType.Fury, obj);
        Person deserialize = SerializableHelper.deserialize(SerializableType.Fury, serialize, Person.class);
        // System.out.println(deserialize);
        assertNotNull(deserialize);
        assertEquals(obj.toString(), deserialize.toString());
    }

    public void testSerializeHessian() {
        Object obj = newObject();
        // System.out.println(obj);
        byte[] serialize = SerializableHelper.serialize(SerializableType.Hessian, obj);
        Person deserialize = SerializableHelper.deserialize(SerializableType.Hessian, serialize, Person.class);
        // System.out.println(deserialize);
        assertNotNull(deserialize);
        assertEquals(obj.toString(), deserialize.toString());
    }

    public void testSerializeJdk() {
        Object obj = newObject();
        // System.out.println(obj);
        byte[] serialize = SerializableHelper.serialize(SerializableType.Jdk, obj);
        Person deserialize = SerializableHelper.deserialize(SerializableType.Jdk, serialize, Person.class);
        // System.out.println(deserialize);
        assertNotNull(deserialize);
        assertEquals(obj.toString(), deserialize.toString());
    }

    public void testBenchmark() {
        int times = 10000;

        long start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            testSerializeJSON();
        }
        // testSerializeJSON cost 2539 ms
        System.out.println("testSerializeJSON cost " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            testSerializeKryo();
        }
        // testSerializeKryo cost 19598 ms
        // testSerializeKryo cost 763 ms
        System.out.println("testSerializeKryo cost " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            testSerializeFury();
        }
        // testSerializeFury cost 1370 ms
        // testSerializeFury cost 2263 ms
        System.out.println("testSerializeFury cost " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            testSerializeHessian();
        }
        // testSerializeHessian cost 2676 ms
        // testSerializeHessian cost 2629 ms
        System.out.println("testSerializeHessian cost " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            testSerializeJdk();
        }
        // testSerializeJdk cost 1989 ms
        // testSerializeJdk cost 1968 ms
        System.out.println("testSerializeJdk cost " + (System.currentTimeMillis() - start) + " ms");

    }

}