/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.reactor.ssl;

import java.nio.ByteBuffer;
import org.apache.http.nio.reactor.ssl.SSLBuffer;
import org.apache.http.nio.reactor.ssl.SSLBufferManagementStrategy;
import org.apache.http.util.Args;

public class PermanentSSLBufferManagementStrategy
implements SSLBufferManagementStrategy {
    @Override
    public SSLBuffer constructBuffer(int size) {
        return new InternalBuffer(size);
    }

    private static final class InternalBuffer
    implements SSLBuffer {
        private final ByteBuffer buffer;

        public InternalBuffer(int size) {
            Args.positive(size, "size");
            this.buffer = ByteBuffer.allocate(size);
        }

        @Override
        public ByteBuffer acquire() {
            return this.buffer;
        }

        @Override
        public void release() {
        }

        @Override
        public boolean isAcquired() {
            return true;
        }

        @Override
        public boolean hasData() {
            return this.buffer.position() > 0;
        }
    }
}

