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

import io.netty.buffer.ByteBufAllocator;
import link.thingscloud.netty.remoting.BaseTest;
import link.thingscloud.netty.remoting.api.buffer.RemotingBuffer;
import link.thingscloud.netty.remoting.api.command.RemotingCommand;
import link.thingscloud.netty.remoting.api.exception.RemotingCodecException;
import link.thingscloud.netty.remoting.impl.buffer.NettyRemotingBuffer;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import static link.thingscloud.netty.remoting.impl.command.CodecHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.failBecauseExceptionWasNotThrown;
import static org.junit.Assert.assertEquals;

/**
 * @author zhouhailin
 * @since 0.5.0
 */
public class CodecHelperTest extends BaseTest {

    @Test
    public void encodeAndDecodeCommand_Success() {
        RemotingBuffer buffer = new NettyRemotingBuffer(ByteBufAllocator.DEFAULT.heapBuffer());
        RemotingCommand command = randomRemotingCommand();
        CodecHelper.encodeCommand(command, buffer);

        // Skip magic code and total length
        assertEquals(PROTOCOL_MAGIC, buffer.readByte());
        buffer.readInt();

        RemotingCommand decodedCommand = CodecHelper.decode(buffer);

        assertEquals(command, decodedCommand);
    }

    @Test
    public void encodeCommand_LenOverLimit_ExceptionThrown() {
        RemotingBuffer buffer = new NettyRemotingBuffer(ByteBufAllocator.DEFAULT.heapBuffer());
        RemotingCommand command = randomRemotingCommand();

        // Remark len exceed max limit
        command.remark(RandomStringUtils.randomAlphabetic(CodecHelper.REMARK_MAX_LEN + 1));
        try {
            CodecHelper.encodeCommand(command, buffer);
            failBecauseExceptionWasNotThrown(RemotingCodecException.class);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(RemotingCodecException.class);
        }

        command = randomRemotingCommand();
        command.property("a", RandomStringUtils.randomAlphabetic(Short.MAX_VALUE));

        try {
            CodecHelper.encodeCommand(command, buffer);
            failBecauseExceptionWasNotThrown(RemotingCodecException.class);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(RemotingCodecException.class);
        }

        command = randomRemotingCommand();
        command.property("a", RandomStringUtils.randomAlphabetic(CodecHelper.PROPERTY_MAX_LEN));

        try {
            CodecHelper.encodeCommand(command, buffer);
            failBecauseExceptionWasNotThrown(RemotingCodecException.class);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(RemotingCodecException.class);
        }

        command = randomRemotingCommand();
        command.payload(RandomStringUtils.randomAlphabetic(CodecHelper.PAYLOAD_MAX_LEN + 1).getBytes());

        try {
            CodecHelper.encodeCommand(command, buffer);
            failBecauseExceptionWasNotThrown(RemotingCodecException.class);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(RemotingCodecException.class);
        }
    }

    @Test
    public void decodeCommand_LenOverLimit_ExceptionThrown() {
        RemotingBuffer buffer = new NettyRemotingBuffer(ByteBufAllocator.DEFAULT.heapBuffer());

        buffer.writeShort((short) 0);
        buffer.writeShort((short) 0);
        buffer.writeInt(0);
        buffer.writeByte((byte) 0);
        buffer.writeShort((short) 0);

        buffer.writeShort((short) 0);
        int writerIndex = buffer.writerIndex();

        int propsSize = 1 + PROPERTY_MAX_LEN / Short.MAX_VALUE;
        buffer.writeShort((short) propsSize);

        for (int i = 0; i < propsSize; i++) {
            buffer.writeShort(Short.MAX_VALUE);
            buffer.writeBytes(new byte[Short.MAX_VALUE]);
        }

        try {
            CodecHelper.decode(buffer);
            failBecauseExceptionWasNotThrown(RemotingCodecException.class);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(RemotingCodecException.class);
        }

        buffer.setReaderIndex(0);
        buffer.setWriterIndex(writerIndex);
        buffer.writeShort((short) 0);

        buffer.writeInt(PAYLOAD_MAX_LEN + 1);

        try {
            CodecHelper.decode(buffer);
            failBecauseExceptionWasNotThrown(RemotingCodecException.class);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(RemotingCodecException.class);
        }
    }
}