/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 */
package software.amazon.awssdk.core.exception;

import java.io.InputStream;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.SdkHttpFullResponse;

@SdkPublicApi
public final class SdkInterruptedException
extends InterruptedException {
    private static final long serialVersionUID = 8194951388566545094L;
    private final transient InputStream responseStream;

    public SdkInterruptedException() {
        this.responseStream = null;
    }

    public SdkInterruptedException(SdkHttpFullResponse response) {
        this.responseStream = Optional.ofNullable(response).flatMap(SdkHttpFullResponse::content).orElse(null);
    }

    public Optional<InputStream> getResponseStream() {
        return Optional.ofNullable(this.responseStream);
    }
}

