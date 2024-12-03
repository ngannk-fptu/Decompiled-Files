/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration
 *  software.amazon.awssdk.endpoints.EndpointProvider
 *  software.amazon.awssdk.regions.Region
 */
package software.amazon.awssdk.services.s3.internal.crossregion;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.endpoints.EndpointProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.DelegatingS3Client;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.internal.crossregion.utils.CrossRegionUtils;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Request;

@SdkInternalApi
public final class S3CrossRegionSyncClient
extends DelegatingS3Client {
    private final Map<String, Region> bucketToRegionCache = new ConcurrentHashMap<String, Region>();

    public S3CrossRegionSyncClient(S3Client s3Client) {
        super(s3Client);
    }

    private static <T extends S3Request> Optional<String> bucketNameFromRequest(T request) {
        return request.getValueForField("Bucket", String.class);
    }

    @Override
    protected <T extends S3Request, ReturnT> ReturnT invokeOperation(T request, Function<T, ReturnT> operation) {
        Optional<String> bucketRequest = S3CrossRegionSyncClient.bucketNameFromRequest(request);
        AwsRequestOverrideConfiguration overrideConfiguration = CrossRegionUtils.updateUserAgentInConfig(request);
        S3Request userAgentUpdatedRequest = (S3Request)request.toBuilder().overrideConfiguration(overrideConfiguration).build();
        if (!bucketRequest.isPresent()) {
            return operation.apply(userAgentUpdatedRequest);
        }
        String bucketName = bucketRequest.get();
        try {
            if (this.bucketToRegionCache.containsKey(bucketName)) {
                return operation.apply(CrossRegionUtils.requestWithDecoratedEndpointProvider(userAgentUpdatedRequest, () -> this.bucketToRegionCache.get(bucketName), (EndpointProvider)this.serviceClientConfiguration().endpointProvider().get()));
            }
            return operation.apply(userAgentUpdatedRequest);
        }
        catch (S3Exception exception) {
            if (CrossRegionUtils.isS3RedirectException((Throwable)((Object)exception))) {
                this.updateCacheFromRedirectException(exception, bucketName);
                return operation.apply(CrossRegionUtils.requestWithDecoratedEndpointProvider(userAgentUpdatedRequest, () -> this.bucketToRegionCache.computeIfAbsent(bucketName, this::fetchBucketRegion), (EndpointProvider)this.serviceClientConfiguration().endpointProvider().get()));
            }
            throw exception;
        }
    }

    private void updateCacheFromRedirectException(S3Exception exception, String bucketName) {
        Optional<String> regionStr = CrossRegionUtils.getBucketRegionFromException(exception);
        this.bucketToRegionCache.remove(bucketName);
        regionStr.ifPresent(region -> this.bucketToRegionCache.put(bucketName, Region.of((String)region)));
    }

    private Region fetchBucketRegion(String bucketName) {
        try {
            ((S3Client)this.delegate()).headBucket((HeadBucketRequest)((Object)HeadBucketRequest.builder().bucket(bucketName).build()));
        }
        catch (S3Exception exception) {
            if (CrossRegionUtils.isS3RedirectException((Throwable)((Object)exception))) {
                return Region.of((String)CrossRegionUtils.getBucketRegionFromException(exception).orElseThrow(() -> exception));
            }
            throw exception;
        }
        return null;
    }
}

