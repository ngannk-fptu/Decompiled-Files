/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
final class FutureCancelledException
extends RuntimeException {
    private final long executionId;

    FutureCancelledException(long executionId, Throwable cause) {
        super(cause);
        this.executionId = executionId;
    }

    long getExecutionId() {
        return this.executionId;
    }
}

