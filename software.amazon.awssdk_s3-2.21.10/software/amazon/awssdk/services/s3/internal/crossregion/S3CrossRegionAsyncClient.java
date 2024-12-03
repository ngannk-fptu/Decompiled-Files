/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration
 *  software.amazon.awssdk.endpoints.EndpointProvider
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.utils.CompletableFutureUtils
 */
package software.amazon.awssdk.services.s3.internal.crossregion;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.endpoints.EndpointProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.DelegatingS3AsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.internal.crossregion.utils.CrossRegionUtils;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Request;
import software.amazon.awssdk.utils.CompletableFutureUtils;

@SdkInternalApi
public final class S3CrossRegionAsyncClient
extends DelegatingS3AsyncClient {
    private final Map<String, Region> bucketToRegionCache = new ConcurrentHashMap<String, Region>();

    public S3CrossRegionAsyncClient(S3AsyncClient s3Client) {
        super(s3Client);
    }

    @Override
    protected <T extends S3Request, ReturnT> CompletableFuture<ReturnT> invokeOperation(T request, Function<T, CompletableFuture<ReturnT>> operation) {
        Optional bucket = request.getValueForField("Bucket", String.class);
        AwsRequestOverrideConfiguration overrideConfiguration = CrossRegionUtils.updateUserAgentInConfig(request);
        S3Request userAgentUpdatedRequest = (S3Request)request.toBuilder().overrideConfiguration(overrideConfiguration).build();
        if (!bucket.isPresent()) {
            return operation.apply(userAgentUpdatedRequest);
        }
        String bucketName = (String)bucket.get();
        CompletableFuture returnFuture = new CompletableFuture();
        CompletableFuture<ReturnT> apiOperationFuture = this.bucketToRegionCache.containsKey(bucketName) ? operation.apply(CrossRegionUtils.requestWithDecoratedEndpointProvider(userAgentUpdatedRequest, () -> this.bucketToRegionCache.get(bucketName), (EndpointProvider)this.serviceClientConfiguration().endpointProvider().get())) : operation.apply(userAgentUpdatedRequest);
        apiOperationFuture.whenComplete((BiConsumer)this.redirectToCrossRegionIfRedirectException(operation, userAgentUpdatedRequest, bucketName, returnFuture));
        return returnFuture;
    }

    private <T extends S3Request, ReturnT> BiConsumer<ReturnT, Throwable> redirectToCrossRegionIfRedirectException(Function<T, CompletableFuture<ReturnT>> operation, T userAgentUpdatedRequest, String bucketName, CompletableFuture<ReturnT> returnFuture) {
        return (response, throwable) -> {
            if (throwable != null) {
                if (CrossRegionUtils.isS3RedirectException(throwable)) {
                    this.bucketToRegionCache.remove(bucketName);
                    this.requestWithCrossRegion(userAgentUpdatedRequest, operation, bucketName, returnFuture, (Throwable)throwable);
                } else {
                    returnFuture.completeExceptionally((Throwable)throwable);
                }
            } else {
                returnFuture.complete(response);
            }
        };
    }

    private <T extends S3Request, ReturnT> void requestWithCrossRegion(T request, Function<T, CompletableFuture<ReturnT>> operation, String bucketName, CompletableFuture<ReturnT> returnFuture, Throwable throwable) {
        Optional<String> bucketRegionFromException = CrossRegionUtils.getBucketRegionFromException((S3Exception)((Object)throwable.getCause()));
        if (bucketRegionFromException.isPresent()) {
            this.sendRequestWithRightRegion(request, operation, bucketName, returnFuture, bucketRegionFromException.get());
        } else {
            this.fetchRegionAndSendRequest(request, operation, bucketName, returnFuture);
        }
    }

    private <T extends S3Request, ReturnT> void fetchRegionAndSendRequest(T request, Function<T, CompletableFuture<ReturnT>> operation, String bucketName, CompletableFuture<ReturnT> returnFuture) {
        ((S3AsyncClient)this.delegate()).headBucket((HeadBucketRequest.Builder b) -> b.bucket(bucketName)).whenComplete((response, throwable) -> {
            if (throwable != null) {
                if (CrossRegionUtils.isS3RedirectException(throwable)) {
                    this.bucketToRegionCache.remove(bucketName);
                    Optional<String> bucketRegion = CrossRegionUtils.getBucketRegionFromException((S3Exception)((Object)((Object)throwable.getCause())));
                    if (bucketRegion.isPresent()) {
                        this.sendRequestWithRightRegion(request, operation, bucketName, returnFuture, bucketRegion.get());
                    } else {
                        returnFuture.completeExceptionally((Throwable)throwable);
                    }
                } else {
                    returnFuture.completeExceptionally((Throwable)throwable);
                }
            }
        });
    }

    private <T extends S3Request, ReturnT> void sendRequestWithRightRegion(T request, Function<T, CompletableFuture<ReturnT>> operation, String bucketName, CompletableFuture<ReturnT> returnFuture, String region) {
        this.bucketToRegionCache.put(bucketName, Region.of((String)region));
        CompletableFuture<ReturnT> newFuture = operation.apply(CrossRegionUtils.requestWithDecoratedEndpointProvider(request, () -> Region.of((String)region), (EndpointProvider)this.serviceClientConfiguration().endpointProvider().get()));
        CompletableFutureUtils.forwardResultTo(newFuture, returnFuture);
        CompletableFutureUtils.forwardExceptionTo(returnFuture, newFuture);
    }
}

