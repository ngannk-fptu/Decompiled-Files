/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.io;

import java.net.SocketTimeoutException;
import java.util.Objects;
import org.apache.hc.core5.util.Timeout;

public final class SocketTimeoutExceptionFactory {
    public static SocketTimeoutException create(Timeout timeout) {
        return new SocketTimeoutException(Objects.toString(timeout));
    }
}

