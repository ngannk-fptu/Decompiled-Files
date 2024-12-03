/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.waiters;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.internal.waiters.ResponseOrException;

@SdkPublicApi
public interface WaiterResponse<T> {
    public ResponseOrException<T> matched();

    public int attemptsExecuted();
}

