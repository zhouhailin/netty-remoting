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

import java.util.Map;

/**
 * @author zhouhailin
 * @since 0.5.0
 */
public interface RemotingCommand {
    short cmdCode();

    void cmdCode(short code);

    LanguageCode language();

    void language(LanguageCode language);

    short cmdVersion();

    void cmdVersion(short version);

    int requestID();

    void requestID(int value);

    TrafficType trafficType();

    void trafficType(TrafficType value);

    SerializableType serializableType();

    void serializableType(SerializableType value);

    short opCode();

    void opCode(short value);

    String remark();

    void remark(String value);

    Map<String, String> properties();

    String property(String key);

    void property(String key, String value);

    byte[] payload();

    void payload(byte[] payload);
}