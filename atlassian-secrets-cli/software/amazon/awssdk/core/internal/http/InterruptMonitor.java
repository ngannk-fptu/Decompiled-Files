/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.exception.SdkInterruptedException;
import software.amazon.awssdk.http.SdkHttpFullResponse;

@SdkInternalApi
public final class InterruptMonitor {
    private InterruptMonitor() {
    }

    public static void checkInterrupted() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new SdkInterruptedException();
        }
    }

    public static void checkInterrupted(SdkHttpFullResponse response) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new SdkInterruptedException(response);
        }
    }
}

