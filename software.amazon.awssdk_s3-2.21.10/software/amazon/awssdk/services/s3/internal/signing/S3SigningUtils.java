/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.arns.Arn
 *  software.amazon.awssdk.core.signer.Signer
 */
package software.amazon.awssdk.services.s3.internal.signing;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.arns.Arn;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.services.s3.internal.endpoints.S3EndpointUtils;
import software.amazon.awssdk.services.s3.internal.resource.S3ArnConverter;
import software.amazon.awssdk.services.s3.internal.resource.S3Resource;
import software.amazon.awssdk.services.s3.model.S3Request;

@SdkInternalApi
public final class S3SigningUtils {
    private S3SigningUtils() {
    }

    public static Optional<Signer> internalSignerOverride(S3Request originalRequest) {
        return originalRequest.getValueForField("Bucket", String.class).filter(S3EndpointUtils::isArn).flatMap(S3SigningUtils::getS3ResourceSigner);
    }

    private static Optional<Signer> getS3ResourceSigner(String name) {
        S3Resource resolvedS3Resource = S3ArnConverter.create().convertArn(Arn.fromString((String)name));
        return resolvedS3Resource.overrideSigner();
    }
}

