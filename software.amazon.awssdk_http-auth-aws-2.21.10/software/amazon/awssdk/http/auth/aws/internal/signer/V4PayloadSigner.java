/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.ContentStreamProvider
 *  software.amazon.awssdk.http.SdkHttpRequest$Builder
 */
package software.amazon.awssdk.http.auth.aws.internal.signer;

import java.nio.ByteBuffer;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.auth.aws.internal.signer.DefaultV4PayloadSigner;
import software.amazon.awssdk.http.auth.aws.internal.signer.V4RequestSigningResult;

@SdkInternalApi
public interface V4PayloadSigner {
    public static V4PayloadSigner create() {
        return new DefaultV4PayloadSigner();
    }

    public ContentStreamProvider sign(ContentStreamProvider var1, V4RequestSigningResult var2);

    public Publisher<ByteBuffer> signAsync(Publisher<ByteBuffer> var1, V4RequestSigningResult var2);

    default public void beforeSigning(SdkHttpRequest.Builder request, ContentStreamProvider payload) {
    }
}

