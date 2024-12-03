/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal.http2;

import java.io.IOException;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public final class PingFailedException
extends IOException {
    PingFailedException(String msg) {
        super(msg);
    }

    PingFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}

