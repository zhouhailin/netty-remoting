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

package link.thingscloud.netty.remoting.api.command;

/**
 * @author zhouhailin
 * @since 0.8.0
 */
public enum SerializableType {
    JSON,
    Kryo,
    Fury,
    Hessian,
    Jdk;

    public static SerializableType parse(int index) {
        switch (index) {
            case 0:
                return JSON;
            case 1:
                return Kryo;
            case 2:
                return Fury;
            case 3:
                return Hessian;
            case 4:
                return Jdk;
            default:
                throw new IllegalArgumentException("SerializableType " + index + " is not supported");
        }
    }

}
