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

package link.thingscloud.netty.remoting.api;

import link.thingscloud.netty.remoting.api.command.RemotingCommandFactory;
import link.thingscloud.netty.remoting.api.interceptor.Interceptor;
import link.thingscloud.netty.remoting.common.Pair;

import java.util.concurrent.ExecutorService;

/**
 * @author zhouhailin
 * @since 0.5.0
 */
public interface RemotingService extends ConnectionService, ObjectLifecycle {
    void registerInterceptor(Interceptor interceptor);

    void registerRequestProcessor(short requestCode, RequestProcessor processor, ExecutorService executor);

    void registerRequestProcessor(short requestCode, RequestProcessor processor);

    void unregisterRequestProcessor(short requestCode);

    Pair<RequestProcessor, ExecutorService> processor(short requestCode);

    RemotingCommandFactory commandFactory();
}
