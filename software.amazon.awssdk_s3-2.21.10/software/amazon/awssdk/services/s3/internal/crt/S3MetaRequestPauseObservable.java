/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.crt.s3.ResumeToken
 *  software.amazon.awssdk.crt.s3.S3MetaRequest
 */
package software.amazon.awssdk.services.s3.internal.crt;

import java.util.function.Function;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.crt.s3.ResumeToken;
import software.amazon.awssdk.crt.s3.S3MetaRequest;

@SdkInternalApi
public class S3MetaRequestPauseObservable {
    private final Function<S3MetaRequest, ResumeToken> pause = S3MetaRequest::pause;
    private volatile S3MetaRequest request;

    public void subscribe(S3MetaRequest request) {
        this.request = request;
    }

    public ResumeToken pause() {
        return this.pause.apply(this.request);
    }
}

