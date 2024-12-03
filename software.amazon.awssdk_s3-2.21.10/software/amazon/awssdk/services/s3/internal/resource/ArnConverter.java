/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.arns.Arn
 */
package software.amazon.awssdk.services.s3.internal.resource;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.arns.Arn;
import software.amazon.awssdk.services.s3.internal.resource.AwsResource;

@FunctionalInterface
@SdkInternalApi
public interface ArnConverter<T extends AwsResource> {
    public T convertArn(Arn var1);
}

