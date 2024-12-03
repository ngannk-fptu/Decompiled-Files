/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.core.waiters.WaiterOverrideConfiguration
 *  software.amazon.awssdk.core.waiters.WaiterOverrideConfiguration$Builder
 *  software.amazon.awssdk.core.waiters.WaiterResponse
 *  software.amazon.awssdk.utils.SdkAutoCloseable
 */
package software.amazon.awssdk.services.s3.waiters;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.waiters.WaiterOverrideConfiguration;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.waiters.DefaultS3AsyncWaiter;
import software.amazon.awssdk.utils.SdkAutoCloseable;

@SdkPublicApi
public interface S3AsyncWaiter
extends SdkAutoCloseable {
    default public CompletableFuture<WaiterResponse<HeadBucketResponse>> waitUntilBucketExists(HeadBucketRequest headBucketRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<WaiterResponse<HeadBucketResponse>> waitUntilBucketExists(Consumer<HeadBucketRequest.Builder> headBucketRequest) {
        return this.waitUntilBucketExists((HeadBucketRequest)((Object)((HeadBucketRequest.Builder)HeadBucketRequest.builder().applyMutation(headBucketRequest)).build()));
    }

    default public CompletableFuture<WaiterResponse<HeadBucketResponse>> waitUntilBucketExists(HeadBucketRequest headBucketRequest, WaiterOverrideConfiguration overrideConfig) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<WaiterResponse<HeadBucketResponse>> waitUntilBucketExists(Consumer<HeadBucketRequest.Builder> headBucketRequest, Consumer<WaiterOverrideConfiguration.Builder> overrideConfig) {
        return this.waitUntilBucketExists((HeadBucketRequest)((Object)((HeadBucketRequest.Builder)HeadBucketRequest.builder().applyMutation(headBucketRequest)).build()), ((WaiterOverrideConfiguration.Builder)WaiterOverrideConfiguration.builder().applyMutation(overrideConfig)).build());
    }

    default public CompletableFuture<WaiterResponse<HeadBucketResponse>> waitUntilBucketNotExists(HeadBucketRequest headBucketRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<WaiterResponse<HeadBucketResponse>> waitUntilBucketNotExists(Consumer<HeadBucketRequest.Builder> headBucketRequest) {
        return this.waitUntilBucketNotExists((HeadBucketRequest)((Object)((HeadBucketRequest.Builder)HeadBucketRequest.builder().applyMutation(headBucketRequest)).build()));
    }

    default public CompletableFuture<WaiterResponse<HeadBucketResponse>> waitUntilBucketNotExists(HeadBucketRequest headBucketRequest, WaiterOverrideConfiguration overrideConfig) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<WaiterResponse<HeadBucketResponse>> waitUntilBucketNotExists(Consumer<HeadBucketRequest.Builder> headBucketRequest, Consumer<WaiterOverrideConfiguration.Builder> overrideConfig) {
        return this.waitUntilBucketNotExists((HeadBucketRequest)((Object)((HeadBucketRequest.Builder)HeadBucketRequest.builder().applyMutation(headBucketRequest)).build()), ((WaiterOverrideConfiguration.Builder)WaiterOverrideConfiguration.builder().applyMutation(overrideConfig)).build());
    }

    default public CompletableFuture<WaiterResponse<HeadObjectResponse>> waitUntilObjectExists(HeadObjectRequest headObjectRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<WaiterResponse<HeadObjectResponse>> waitUntilObjectExists(Consumer<HeadObjectRequest.Builder> headObjectRequest) {
        return this.waitUntilObjectExists((HeadObjectRequest)((Object)((HeadObjectRequest.Builder)HeadObjectRequest.builder().applyMutation(headObjectRequest)).build()));
    }

    default public CompletableFuture<WaiterResponse<HeadObjectResponse>> waitUntilObjectExists(HeadObjectRequest headObjectRequest, WaiterOverrideConfiguration overrideConfig) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<WaiterResponse<HeadObjectResponse>> waitUntilObjectExists(Consumer<HeadObjectRequest.Builder> headObjectRequest, Consumer<WaiterOverrideConfiguration.Builder> overrideConfig) {
        return this.waitUntilObjectExists((HeadObjectRequest)((Object)((HeadObjectRequest.Builder)HeadObjectRequest.builder().applyMutation(headObjectRequest)).build()), ((WaiterOverrideConfiguration.Builder)WaiterOverrideConfiguration.builder().applyMutation(overrideConfig)).build());
    }

    default public CompletableFuture<WaiterResponse<HeadObjectResponse>> waitUntilObjectNotExists(HeadObjectRequest headObjectRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<WaiterResponse<HeadObjectResponse>> waitUntilObjectNotExists(Consumer<HeadObjectRequest.Builder> headObjectRequest) {
        return this.waitUntilObjectNotExists((HeadObjectRequest)((Object)((HeadObjectRequest.Builder)HeadObjectRequest.builder().applyMutation(headObjectRequest)).build()));
    }

    default public CompletableFuture<WaiterResponse<HeadObjectResponse>> waitUntilObjectNotExists(HeadObjectRequest headObjectRequest, WaiterOverrideConfiguration overrideConfig) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<WaiterResponse<HeadObjectResponse>> waitUntilObjectNotExists(Consumer<HeadObjectRequest.Builder> headObjectRequest, Consumer<WaiterOverrideConfiguration.Builder> overrideConfig) {
        return this.waitUntilObjectNotExists((HeadObjectRequest)((Object)((HeadObjectRequest.Builder)HeadObjectRequest.builder().applyMutation(headObjectRequest)).build()), ((WaiterOverrideConfiguration.Builder)WaiterOverrideConfiguration.builder().applyMutation(overrideConfig)).build());
    }

    public static Builder builder() {
        return DefaultS3AsyncWaiter.builder();
    }

    public static S3AsyncWaiter create() {
        return DefaultS3AsyncWaiter.builder().build();
    }

    public static interface Builder {
        public Builder scheduledExecutorService(ScheduledExecutorService var1);

        public Builder overrideConfiguration(WaiterOverrideConfiguration var1);

        default public Builder overrideConfiguration(Consumer<WaiterOverrideConfiguration.Builder> overrideConfiguration) {
            WaiterOverrideConfiguration.Builder builder = WaiterOverrideConfiguration.builder();
            overrideConfiguration.accept(builder);
            return this.overrideConfiguration(builder.build());
        }

        public Builder client(S3AsyncClient var1);

        public S3AsyncWaiter build();
    }
}

