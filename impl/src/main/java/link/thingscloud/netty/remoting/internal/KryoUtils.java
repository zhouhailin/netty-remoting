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

package link.thingscloud.netty.remoting.internal;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * @author : zhouhailin
 * @version 0.5.0
 */
public class KryoUtils {

    public static byte[] encode(Object object) {
        if (object == null) {
            return null;
        }
        Kryo kryo = new Kryo();
        kryo.register(object.getClass());
        byte[] buffer = new byte[1024];
        kryo.writeObject(new Output(buffer), object);
        return encode(object);
    }

    public static byte[] encode(Object object, Class<?>... classes) {
        if (object == null) {
            return null;
        }
        Kryo kryo = new Kryo();
        kryo.register(object.getClass());
        for (Class<?> aClass : classes) {
            kryo.register(aClass);
        }
        byte[] buffer = new byte[1024];
        kryo.writeObject(new Output(buffer), object);
        return buffer;
    }

    public static <T> T decode(byte[] buffer, Class<T> type) {
        if (buffer == null || buffer.length == 0) {
            return null;
        }
        Kryo kryo = new Kryo();
        kryo.register(type);
        return kryo.readObject(new Input(buffer), type);
    }

    public static <T> T decode(byte[] buffer, Class<T> type, Class<?>... classes) {
        if (buffer == null || buffer.length == 0) {
            return null;
        }
        Kryo kryo = new Kryo();
        kryo.register(type);
        for (Class<?> aClass : classes) {
            kryo.register(aClass);
        }
        return kryo.readObject(new Input(buffer), type);
    }

    public static void main(String[] args) {
        Person person = new Person();
        person.setName("zhangsan");
        person.setAge(10);
        person.setCat(new Cat().setAge(1).setName("cat"));

        byte[] encode = encode(person, Cat.class);
        Person decode = decode(encode, Person.class, Cat.class);
        System.out.println(decode);
    }

    static class Person {
        private String name;
        private int age;
        private Cat cat;

        public String getName() {
            return name;
        }

        public Person setName(String name) {
            this.name = name;
            return this;
        }

        public int getAge() {
            return age;
        }

        public Person setAge(int age) {
            this.age = age;
            return this;
        }

        public Cat getCat() {
            return cat;
        }

        public Person setCat(Cat cat) {
            this.cat = cat;
            return this;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    ", cat=" + cat +
                    '}';
        }
    }

    static class Cat {
        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public Cat setName(String name) {
            this.name = name;
            return this;
        }

        public int getAge() {
            return age;
        }

        public Cat setAge(int age) {
            this.age = age;
            return this;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }
}
