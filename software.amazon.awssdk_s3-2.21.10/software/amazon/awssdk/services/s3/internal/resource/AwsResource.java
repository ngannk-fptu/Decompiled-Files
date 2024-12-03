/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.s3.internal.resource;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public interface AwsResource {
    public Optional<String> partition();

    public Optional<String> region();

    public Optional<String> accountId();
}

