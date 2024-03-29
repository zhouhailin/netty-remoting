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

package link.thingscloud.netty.remoting.api.interceptor;

import link.thingscloud.netty.remoting.api.RemotingEndPoint;
import link.thingscloud.netty.remoting.api.command.RemotingCommand;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author zhouhailin
 * @since 0.5.0
 */
public class RequestContext {
    protected RemotingEndPoint remotingEndPoint;
    protected String remoteAddr;
    protected RemotingCommand request;

    public RequestContext(RemotingEndPoint remotingEndPoint, String remoteAddr, RemotingCommand request) {
        super();
        this.remotingEndPoint = remotingEndPoint;
        this.remoteAddr = remoteAddr;
        this.request = request;
    }

    public RemotingEndPoint getRemotingEndPoint() {
        return remotingEndPoint;
    }

    public void setRemotingEndPoint(RemotingEndPoint remotingEndPoint) {
        this.remotingEndPoint = remotingEndPoint;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public RemotingCommand getRequest() {
        return request;
    }

    public void setRequest(RemotingCommand request) {
        this.request = request;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }
}
