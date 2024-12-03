/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.reactor.ssl;

import java.nio.ByteBuffer;
import org.apache.http.nio.reactor.ssl.SSLBuffer;
import org.apache.http.nio.reactor.ssl.SSLBufferManagementStrategy;
import org.apache.http.util.Args;

public class ReleasableSSLBufferManagementStrategy
implements SSLBufferManagementStrategy {
    @Override
    public SSLBuffer constructBuffer(int size) {
        return new InternalBuffer(size);
    }

    private static final class InternalBuffer
    implements SSLBuffer {
        private ByteBuffer wrapped;
        private final int length;

        public InternalBuffer(int size) {
            Args.positive(size, "size");
            this.length = size;
        }

        @Override
        public ByteBuffer acquire() {
            if (this.wrapped != null) {
                return this.wrapped;
            }
            this.wrapped = ByteBuffer.allocate(this.length);
            return this.wrapped;
        }

        @Override
        public void release() {
            this.wrapped = null;
        }

        @Override
        public boolean isAcquired() {
            return this.wrapped != null;
        }

        @Override
        public boolean hasData() {
            return this.wrapped != null && this.wrapped.position() > 0;
        }
    }
}

