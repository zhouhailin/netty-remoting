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

package link.thingscloud.netty.remoting.impl.buffer;

import io.netty.buffer.ByteBuf;
import link.thingscloud.netty.remoting.api.buffer.RemotingBuffer;

import java.nio.ByteBuffer;

/**
 * @author zhouhailin
 * @since 0.5.0
 */
public class NettyRemotingBuffer implements RemotingBuffer {
    private final ByteBuf buffer;

    public NettyRemotingBuffer(ByteBuf buffer) {
        this.buffer = buffer;
    }

    @Override
    public void writeByte(byte data) {
        buffer.writeByte(data);
    }

    @Override
    public void writeByte(int index, byte data) {
        buffer.writeByte(data);
    }

    @Override
    public void writeBytes(byte[] data) {
        buffer.writeBytes(data);
    }

    @Override
    public void writeBytes(final ByteBuffer data) {
        buffer.writeBytes(data);
    }

    @Override
    public void writeInt(int data) {
        buffer.writeInt(data);
    }

    @Override
    public void writeShort(final short value) {
        buffer.writeShort(value);
    }

    @Override
    public void writeLong(long value) {
        buffer.writeLong(value);
    }

    @Override
    public byte readByte() {
        return buffer.readByte();
    }

    @Override
    public void readBytes(byte[] dst) {
        buffer.readBytes(dst);
    }

    @Override
    public void readBytes(final ByteBuffer dst) {
        buffer.readBytes(dst);
    }

    @Override
    public short readShort() {
        return buffer.readShort();
    }

    @Override
    public int readInt() {
        return buffer.readInt();
    }

    @Override
    public long readLong() {
        return buffer.readLong();
    }

    @Override
    public int readableBytes() {
        return buffer.readableBytes();
    }

    @Override
    public int readerIndex() {
        return buffer.readerIndex();
    }

    @Override
    public void setReaderIndex(int readerIndex) {
        buffer.setIndex(readerIndex, buffer.writerIndex());
    }

    @Override
    public int writerIndex() {
        return buffer.writerIndex();
    }

    @Override
    public void setWriterIndex(int writerIndex) {
        buffer.setIndex(buffer.readerIndex(), writerIndex);
    }

    @Override
    public void ensureCapacity(int capacity) {
        buffer.capacity(capacity);
    }
}


