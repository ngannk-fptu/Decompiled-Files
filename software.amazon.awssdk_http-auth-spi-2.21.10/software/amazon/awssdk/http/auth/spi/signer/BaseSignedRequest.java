/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.http.SdkHttpRequest
 */
package software.amazon.awssdk.http.auth.spi.signer;

import java.util.Optional;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.http.SdkHttpRequest;

@SdkPublicApi
@Immutable
@ThreadSafe
public interface BaseSignedRequest<PayloadT> {
    public SdkHttpRequest request();

    public Optional<PayloadT> payload();

    public static interface Builder<B extends Builder<B, PayloadT>, PayloadT> {
        public B request(SdkHttpRequest var1);

        public B payload(PayloadT var1);
    }
}

