/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.core.signer.Signer
 */
package software.amazon.awssdk.services.s3.internal.resource;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.services.s3.internal.resource.AwsResource;

@SdkProtectedApi
public interface S3Resource
extends AwsResource {
    public String type();

    default public Optional<S3Resource> parentS3Resource() {
        return Optional.empty();
    }

    default public Optional<Signer> overrideSigner() {
        return Optional.empty();
    }
}

