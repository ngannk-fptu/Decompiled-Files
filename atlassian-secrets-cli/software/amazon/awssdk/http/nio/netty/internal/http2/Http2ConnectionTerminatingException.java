/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal.http2;

import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
final class Http2ConnectionTerminatingException
extends RuntimeException {
    Http2ConnectionTerminatingException(String message) {
        super(message);
    }
}

