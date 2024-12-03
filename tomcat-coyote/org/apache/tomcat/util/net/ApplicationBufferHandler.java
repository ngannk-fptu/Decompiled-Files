/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.net;

import java.nio.ByteBuffer;

public interface ApplicationBufferHandler {
    public static final ByteBuffer EMPTY_BUFFER = ByteBuffer.allocate(0);
    public static final ApplicationBufferHandler EMPTY = new ApplicationBufferHandler(){

        @Override
        public void expand(int newSize) {
        }

        @Override
        public void setByteBuffer(ByteBuffer buffer) {
        }

        @Override
        public ByteBuffer getByteBuffer() {
            return EMPTY_BUFFER;
        }
    };

    public void setByteBuffer(ByteBuffer var1);

    public ByteBuffer getByteBuffer();

    public void expand(int var1);
}

