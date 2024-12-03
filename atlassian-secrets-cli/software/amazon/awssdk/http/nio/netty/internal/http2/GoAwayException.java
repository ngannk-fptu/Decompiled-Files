/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal.http2;

import java.io.IOException;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public class GoAwayException
extends IOException {
    private final String message;

    GoAwayException(long errorCode, String debugData) {
        this.message = String.format("GOAWAY received from service, requesting this stream be closed. Error Code = %d, Debug Data = %s", errorCode, debugData);
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}

