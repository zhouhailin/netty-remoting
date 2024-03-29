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

import link.thingscloud.netty.remoting.api.command.RemotingCommand;
import link.thingscloud.netty.remoting.api.command.RemotingCommandFactory;
import link.thingscloud.netty.remoting.api.command.TrafficType;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author zhouhailin
 * @since 0.5.0
 */
public class RemotingCommandFactoryImplTest {
    private final RemotingCommandFactory factory = new RemotingCommandFactoryImpl();

    @Test
    public void createRequest_Success() {
        RemotingCommand request = factory.createRequest();

        assertEquals(request.cmdCode(), 0);
        assertEquals(request.cmdVersion(), 0);
        assertEquals(request.opCode(), RemotingSysResponseCode.SUCCESS);
        assertNull(request.payload());
        assertTrue(request.properties().isEmpty());
        assertNotEquals(request.requestID(), 0);
        assertEquals(request.remark(), "");
        assertEquals(request.trafficType(), TrafficType.REQUEST_SYNC);
    }

    @Test
    public void createResponse_Success() {
        RemotingCommand request = factory.createRequest();
        request.cmdVersion((short) 123);
        request.cmdCode((short) 100);
        RemotingCommand response = factory.createResponse(request);

        assertEquals(response.cmdVersion(), request.cmdVersion());
        assertEquals(response.cmdCode(), request.cmdCode());
        assertEquals(response.trafficType(), TrafficType.RESPONSE);
        assertEquals(response.requestID(), request.requestID());
    }
}