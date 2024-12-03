/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.awscore.AwsExecutionAttribute
 *  software.amazon.awssdk.core.SdkRequest
 *  software.amazon.awssdk.core.interceptor.Context$ModifyRequest
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.ExecutionInterceptor
 *  software.amazon.awssdk.regions.Region
 */
package software.amazon.awssdk.services.s3.internal.handlers;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.AwsExecutionAttribute;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.internal.BucketUtils;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

@SdkInternalApi
public final class CreateBucketInterceptor
implements ExecutionInterceptor {
    public SdkRequest modifyRequest(Context.ModifyRequest context, ExecutionAttributes executionAttributes) {
        SdkRequest sdkRequest = context.request();
        if (sdkRequest instanceof CreateBucketRequest) {
            CreateBucketRequest request = (CreateBucketRequest)sdkRequest;
            this.validateBucketNameIsS3Compatible(request.bucket());
            if (request.createBucketConfiguration() == null || request.createBucketConfiguration().locationConstraint() == null) {
                Region region = (Region)executionAttributes.getAttribute(AwsExecutionAttribute.AWS_REGION);
                sdkRequest = (SdkRequest)request.toBuilder().createBucketConfiguration(this.toLocationConstraint(region)).build();
            }
        }
        return sdkRequest;
    }

    private CreateBucketConfiguration toLocationConstraint(Region region) {
        if (region.equals(Region.US_EAST_1)) {
            return null;
        }
        return (CreateBucketConfiguration)CreateBucketConfiguration.builder().locationConstraint(region.id()).build();
    }

    private void validateBucketNameIsS3Compatible(String bucketName) {
        BucketUtils.isValidDnsBucketName(bucketName, true);
    }
}

