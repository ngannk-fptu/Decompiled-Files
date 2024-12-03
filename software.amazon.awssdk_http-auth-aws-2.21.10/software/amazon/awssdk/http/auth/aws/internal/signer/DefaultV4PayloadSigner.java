/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.ContentStreamProvider
 */
package software.amazon.awssdk.http.auth.aws.internal.signer;

import java.nio.ByteBuffer;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.auth.aws.internal.signer.V4PayloadSigner;
import software.amazon.awssdk.http.auth.aws.internal.signer.V4RequestSigningResult;

@SdkInternalApi
public class DefaultV4PayloadSigner
implements V4PayloadSigner {
    @Override
    public ContentStreamProvider sign(ContentStreamProvider payload, V4RequestSigningResult requestSigningResult) {
        return payload;
    }

    @Override
    public Publisher<ByteBuffer> signAsync(Publisher<ByteBuffer> payload, V4RequestSigningResult requestSigningResult) {
        return payload;
    }
}

