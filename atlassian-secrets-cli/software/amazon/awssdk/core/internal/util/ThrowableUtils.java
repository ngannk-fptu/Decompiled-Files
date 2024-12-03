/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.util;

import org.slf4j.LoggerFactory;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.exception.AbortedException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkException;

@SdkInternalApi
public final class ThrowableUtils {
    private ThrowableUtils() {
    }

    public static Throwable getRootCause(Throwable orig) {
        if (orig == null) {
            return orig;
        }
        Throwable t = orig;
        for (int i = 0; i < 1000; ++i) {
            Throwable cause = t.getCause();
            if (cause == null) {
                return t;
            }
            t = cause;
        }
        LoggerFactory.getLogger(ThrowableUtils.class).debug("Possible circular reference detected on {}: [{}]", (Object)orig.getClass(), (Object)orig);
        return orig;
    }

    public static RuntimeException failure(Throwable t) {
        if (t instanceof RuntimeException) {
            return (RuntimeException)t;
        }
        if (t instanceof Error) {
            throw (Error)t;
        }
        return t instanceof InterruptedException ? AbortedException.builder().cause(t).build() : SdkClientException.builder().cause(t).build();
    }

    public static RuntimeException failure(Throwable t, String errmsg) {
        if (t instanceof RuntimeException) {
            return (RuntimeException)t;
        }
        if (t instanceof Error) {
            throw (Error)t;
        }
        return t instanceof InterruptedException ? AbortedException.builder().message(errmsg).cause(t).build() : SdkClientException.builder().message(errmsg).cause(t).build();
    }

    public static SdkException asSdkException(Throwable t) {
        if (t instanceof SdkException) {
            return (SdkException)t;
        }
        return SdkClientException.builder().cause(t).build();
    }
}

