/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.ContentStreamProvider
 */
package software.amazon.awssdk.http.auth.aws.crt.internal.signer;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.auth.aws.crt.internal.signer.V4aPayloadSigner;
import software.amazon.awssdk.http.auth.aws.crt.internal.signer.V4aRequestSigningResult;

@SdkInternalApi
public class DefaultV4aPayloadSigner
implements V4aPayloadSigner {
    @Override
    public ContentStreamProvider sign(ContentStreamProvider payload, V4aRequestSigningResult requestSigningResult) {
        return payload;
    }
}

