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

package link.thingscloud.netty.remoting.common;

import link.thingscloud.netty.remoting.api.AsyncHandler;
import link.thingscloud.netty.remoting.api.command.RemotingCommand;
import link.thingscloud.netty.remoting.api.exception.RemotingRuntimeException;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhouhailin
 * @since 0.5.0
 */
public class ResponseFuture {
    private final long beginTimestamp = System.currentTimeMillis();

    @ToStringExclude
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    @ToStringExclude
    private final AtomicBoolean asyncHandlerExecuted = new AtomicBoolean(false);

    private int requestId;
    private long timeoutMillis;

    @ToStringExclude
    private AsyncHandler asyncHandler;

    private volatile RemotingCommand responseCommand;
    private volatile boolean sendRequestOK = true;
    private volatile RemotingRuntimeException cause;

    @ToStringExclude
    private SemaphoreReleaseOnlyOnce once;

    private RemotingCommand requestCommand;
    private String remoteAddr;

    public ResponseFuture(int requestId, long timeoutMillis, AsyncHandler asyncHandler,
                          @Nullable SemaphoreReleaseOnlyOnce once) {
        this.requestId = requestId;
        this.timeoutMillis = timeoutMillis;
        this.asyncHandler = asyncHandler;
        this.once = once;
    }

    public ResponseFuture(int requestId, long timeoutMillis) {
        this.requestId = requestId;
        this.timeoutMillis = timeoutMillis;
    }

    public void executeAsyncHandler() {
        if (asyncHandler != null) {
            if (this.asyncHandlerExecuted.compareAndSet(false, true)) {
                if (cause != null) {
                    asyncHandler.onFailure(requestCommand, cause);
                } else {
                    assert responseCommand != null;
                    asyncHandler.onSuccess(responseCommand);
                }
            }
        }
    }

    public void release() {
        if (this.once != null) {
            this.once.release();
        }
    }

    public RemotingCommand waitResponse(final long timeoutMillis) {
        try {
            this.countDownLatch.await(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignore) {
        }
        return this.responseCommand;
    }

    public void putResponse(final RemotingCommand responseCommand) {
        this.responseCommand = responseCommand;
        this.countDownLatch.countDown();
    }

    public long getBeginTimestamp() {
        return beginTimestamp;
    }

    public boolean isSendRequestOK() {
        return sendRequestOK;
    }

    public void setSendRequestOK(boolean sendRequestOK) {
        this.sendRequestOK = sendRequestOK;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public AsyncHandler getAsyncHandler() {
        return asyncHandler;
    }

    public RemotingRuntimeException getCause() {
        return cause;
    }

    public void setCause(RemotingRuntimeException cause) {
        this.cause = cause;
    }

    public RemotingCommand getResponseCommand() {
        return responseCommand;
    }

    public void setResponseCommand(RemotingCommand responseCommand) {
        this.responseCommand = responseCommand;
    }

    public int getRequestId() {
        return requestId;
    }

    public RemotingCommand getRequestCommand() {
        return requestCommand;
    }

    public void setRequestCommand(RemotingCommand requestCommand) {
        this.requestCommand = requestCommand;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }
}
