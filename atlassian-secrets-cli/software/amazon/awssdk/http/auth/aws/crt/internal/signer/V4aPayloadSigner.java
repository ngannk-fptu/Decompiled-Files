/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.aws.crt.internal.signer;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.auth.aws.crt.internal.signer.DefaultV4aPayloadSigner;
import software.amazon.awssdk.http.auth.aws.crt.internal.signer.V4aRequestSigningResult;

@SdkInternalApi
public interface V4aPayloadSigner {
    public static V4aPayloadSigner create() {
        return new DefaultV4aPayloadSigner();
    }

    public ContentStreamProvider sign(ContentStreamProvider var1, V4aRequestSigningResult var2);

    default public void beforeSigning(SdkHttpRequest.Builder request, ContentStreamProvider payload, String checksum) {
    }
}

