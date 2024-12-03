/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.spdy;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.ThrowableUtil;

public class SpdyProtocolException
extends Exception {
    private static final long serialVersionUID = 7870000537743847264L;

    public SpdyProtocolException() {
    }

    public SpdyProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpdyProtocolException(String message) {
        super(message);
    }

    public SpdyProtocolException(Throwable cause) {
        super(cause);
    }

    static SpdyProtocolException newStatic(String message, Class<?> clazz, String method) {
        StacklessSpdyProtocolException exception = PlatformDependent.javaVersion() >= 7 ? new StacklessSpdyProtocolException(message, true) : new StacklessSpdyProtocolException(message);
        return ThrowableUtil.unknownStackTrace(exception, clazz, method);
    }

    @SuppressJava6Requirement(reason="uses Java 7+ Exception.<init>(String, Throwable, boolean, boolean) but is guarded by version checks")
    private SpdyProtocolException(String message, boolean shared) {
        super(message, null, false, true);
        assert (shared);
    }

    private static final class StacklessSpdyProtocolException
    extends SpdyProtocolException {
        private static final long serialVersionUID = -6302754207557485099L;

        StacklessSpdyProtocolException(String message) {
            super(message);
        }

        StacklessSpdyProtocolException(String message, boolean shared) {
            super(message, shared);
        }

        @Override
        public Throwable fillInStackTrace() {
            return this;
        }
    }
}

