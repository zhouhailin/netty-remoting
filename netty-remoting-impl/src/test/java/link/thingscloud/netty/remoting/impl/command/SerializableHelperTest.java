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

/**
 * @author zhouhailin
 * @since 0.8.0
 */
public class SerializableHelperTest extends TestCase {


    @Data
    @Accessors(chain = true)
    class Person {
        private String name;
        private int age;
    }

    public void testSerialize() {
        Person person = new Person();
        person.setAge(10).setName("zhouhailin");
        byte[] serialize = SerializableHelper.serialize(SerializableType.JSON, person);
        System.out.println(new String(serialize));
    }

    public void testDeserialize() {
    }
}