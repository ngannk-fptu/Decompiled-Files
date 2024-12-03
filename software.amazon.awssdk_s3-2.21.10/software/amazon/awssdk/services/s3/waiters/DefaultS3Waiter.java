/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration$Builder
 *  software.amazon.awssdk.awscore.exception.AwsServiceException
 *  software.amazon.awssdk.core.ApiName
 *  software.amazon.awssdk.core.internal.waiters.WaiterAttribute
 *  software.amazon.awssdk.core.retry.backoff.BackoffStrategy
 *  software.amazon.awssdk.core.retry.backoff.FixedDelayBackoffStrategy
 *  software.amazon.awssdk.core.waiters.Waiter
 *  software.amazon.awssdk.core.waiters.Waiter$Builder
 *  software.amazon.awssdk.core.waiters.WaiterAcceptor
 *  software.amazon.awssdk.core.waiters.WaiterOverrideConfiguration
 *  software.amazon.awssdk.core.waiters.WaiterResponse
 *  software.amazon.awssdk.core.waiters.WaiterState
 *  software.amazon.awssdk.utils.AttributeMap
 *  software.amazon.awssdk.utils.AttributeMap$Builder
 *  software.amazon.awssdk.utils.SdkAutoCloseable
 */
package software.amazon.awssdk.services.s3.waiters;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ApiName;
import software.amazon.awssdk.core.internal.waiters.WaiterAttribute;
import software.amazon.awssdk.core.retry.backoff.BackoffStrategy;
import software.amazon.awssdk.core.retry.backoff.FixedDelayBackoffStrategy;
import software.amazon.awssdk.core.waiters.Waiter;
import software.amazon.awssdk.core.waiters.WaiterAcceptor;
import software.amazon.awssdk.core.waiters.WaiterOverrideConfiguration;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.core.waiters.WaiterState;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Request;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;
import software.amazon.awssdk.services.s3.waiters.internal.WaitersRuntime;
import software.amazon.awssdk.utils.AttributeMap;
import software.amazon.awssdk.utils.SdkAutoCloseable;

@SdkInternalApi
@ThreadSafe
final class DefaultS3Waiter
implements S3Waiter {
    private static final WaiterAttribute<SdkAutoCloseable> CLIENT_ATTRIBUTE = new WaiterAttribute(SdkAutoCloseable.class);
    private final S3Client client;
    private final AttributeMap managedResources;
    private final Waiter<HeadBucketResponse> bucketExistsWaiter;
    private final Waiter<HeadBucketResponse> bucketNotExistsWaiter;
    private final Waiter<HeadObjectResponse> objectExistsWaiter;
    private final Waiter<HeadObjectResponse> objectNotExistsWaiter;

    private DefaultS3Waiter(DefaultBuilder builder) {
        AttributeMap.Builder attributeMapBuilder = AttributeMap.builder();
        if (builder.client == null) {
            this.client = (S3Client)S3Client.builder().build();
            attributeMapBuilder.put(CLIENT_ATTRIBUTE, (Object)this.client);
        } else {
            this.client = builder.client;
        }
        this.managedResources = attributeMapBuilder.build();
        this.bucketExistsWaiter = ((Waiter.Builder)((Waiter.Builder)Waiter.builder(HeadBucketResponse.class).acceptors(DefaultS3Waiter.bucketExistsWaiterAcceptors())).overrideConfiguration(DefaultS3Waiter.bucketExistsWaiterConfig(builder.overrideConfiguration))).build();
        this.bucketNotExistsWaiter = ((Waiter.Builder)((Waiter.Builder)Waiter.builder(HeadBucketResponse.class).acceptors(DefaultS3Waiter.bucketNotExistsWaiterAcceptors())).overrideConfiguration(DefaultS3Waiter.bucketNotExistsWaiterConfig(builder.overrideConfiguration))).build();
        this.objectExistsWaiter = ((Waiter.Builder)((Waiter.Builder)Waiter.builder(HeadObjectResponse.class).acceptors(DefaultS3Waiter.objectExistsWaiterAcceptors())).overrideConfiguration(DefaultS3Waiter.objectExistsWaiterConfig(builder.overrideConfiguration))).build();
        this.objectNotExistsWaiter = ((Waiter.Builder)((Waiter.Builder)Waiter.builder(HeadObjectResponse.class).acceptors(DefaultS3Waiter.objectNotExistsWaiterAcceptors())).overrideConfiguration(DefaultS3Waiter.objectNotExistsWaiterConfig(builder.overrideConfiguration))).build();
    }

    private static String errorCode(Throwable error) {
        if (error instanceof AwsServiceException) {
            return ((AwsServiceException)error).awsErrorDetails().errorCode();
        }
        return null;
    }

    @Override
    public WaiterResponse<HeadBucketResponse> waitUntilBucketExists(HeadBucketRequest headBucketRequest) {
        return this.bucketExistsWaiter.run(() -> this.client.headBucket(this.applyWaitersUserAgent(headBucketRequest)));
    }

    @Override
    public WaiterResponse<HeadBucketResponse> waitUntilBucketExists(HeadBucketRequest headBucketRequest, WaiterOverrideConfiguration overrideConfig) {
        return this.bucketExistsWaiter.run(() -> this.client.headBucket(this.applyWaitersUserAgent(headBucketRequest)), DefaultS3Waiter.bucketExistsWaiterConfig(overrideConfig));
    }

    @Override
    public WaiterResponse<HeadBucketResponse> waitUntilBucketNotExists(HeadBucketRequest headBucketRequest) {
        return this.bucketNotExistsWaiter.run(() -> this.client.headBucket(this.applyWaitersUserAgent(headBucketRequest)));
    }

    @Override
    public WaiterResponse<HeadBucketResponse> waitUntilBucketNotExists(HeadBucketRequest headBucketRequest, WaiterOverrideConfiguration overrideConfig) {
        return this.bucketNotExistsWaiter.run(() -> this.client.headBucket(this.applyWaitersUserAgent(headBucketRequest)), DefaultS3Waiter.bucketNotExistsWaiterConfig(overrideConfig));
    }

    @Override
    public WaiterResponse<HeadObjectResponse> waitUntilObjectExists(HeadObjectRequest headObjectRequest) {
        return this.objectExistsWaiter.run(() -> this.client.headObject(this.applyWaitersUserAgent(headObjectRequest)));
    }

    @Override
    public WaiterResponse<HeadObjectResponse> waitUntilObjectExists(HeadObjectRequest headObjectRequest, WaiterOverrideConfiguration overrideConfig) {
        return this.objectExistsWaiter.run(() -> this.client.headObject(this.applyWaitersUserAgent(headObjectRequest)), DefaultS3Waiter.objectExistsWaiterConfig(overrideConfig));
    }

    @Override
    public WaiterResponse<HeadObjectResponse> waitUntilObjectNotExists(HeadObjectRequest headObjectRequest) {
        return this.objectNotExistsWaiter.run(() -> this.client.headObject(this.applyWaitersUserAgent(headObjectRequest)));
    }

    @Override
    public WaiterResponse<HeadObjectResponse> waitUntilObjectNotExists(HeadObjectRequest headObjectRequest, WaiterOverrideConfiguration overrideConfig) {
        return this.objectNotExistsWaiter.run(() -> this.client.headObject(this.applyWaitersUserAgent(headObjectRequest)), DefaultS3Waiter.objectNotExistsWaiterConfig(overrideConfig));
    }

    private static List<WaiterAcceptor<? super HeadBucketResponse>> bucketExistsWaiterAcceptors() {
        ArrayList<WaiterAcceptor<? super HeadBucketResponse>> result = new ArrayList<WaiterAcceptor<? super HeadBucketResponse>>();
        result.add(new WaitersRuntime.ResponseStatusAcceptor(200, WaiterState.SUCCESS));
        result.add(new WaitersRuntime.ResponseStatusAcceptor(301, WaiterState.SUCCESS));
        result.add(new WaitersRuntime.ResponseStatusAcceptor(403, WaiterState.SUCCESS));
        result.add(new WaitersRuntime.ResponseStatusAcceptor(404, WaiterState.RETRY));
        result.addAll(WaitersRuntime.DEFAULT_ACCEPTORS);
        return result;
    }

    private static List<WaiterAcceptor<? super HeadBucketResponse>> bucketNotExistsWaiterAcceptors() {
        ArrayList<WaiterAcceptor<? super HeadBucketResponse>> result = new ArrayList<WaiterAcceptor<? super HeadBucketResponse>>();
        result.add(new WaitersRuntime.ResponseStatusAcceptor(404, WaiterState.SUCCESS));
        result.addAll(WaitersRuntime.DEFAULT_ACCEPTORS);
        return result;
    }

    private static List<WaiterAcceptor<? super HeadObjectResponse>> objectExistsWaiterAcceptors() {
        ArrayList<WaiterAcceptor<? super HeadObjectResponse>> result = new ArrayList<WaiterAcceptor<? super HeadObjectResponse>>();
        result.add(new WaitersRuntime.ResponseStatusAcceptor(200, WaiterState.SUCCESS));
        result.add(new WaitersRuntime.ResponseStatusAcceptor(404, WaiterState.RETRY));
        result.addAll(WaitersRuntime.DEFAULT_ACCEPTORS);
        return result;
    }

    private static List<WaiterAcceptor<? super HeadObjectResponse>> objectNotExistsWaiterAcceptors() {
        ArrayList<WaiterAcceptor<? super HeadObjectResponse>> result = new ArrayList<WaiterAcceptor<? super HeadObjectResponse>>();
        result.add(new WaitersRuntime.ResponseStatusAcceptor(404, WaiterState.SUCCESS));
        result.addAll(WaitersRuntime.DEFAULT_ACCEPTORS);
        return result;
    }

    private static WaiterOverrideConfiguration bucketExistsWaiterConfig(WaiterOverrideConfiguration overrideConfig) {
        Optional<WaiterOverrideConfiguration> optionalOverrideConfig = Optional.ofNullable(overrideConfig);
        int maxAttempts = optionalOverrideConfig.flatMap(WaiterOverrideConfiguration::maxAttempts).orElse(20);
        BackoffStrategy backoffStrategy = (BackoffStrategy)optionalOverrideConfig.flatMap(WaiterOverrideConfiguration::backoffStrategy).orElse(FixedDelayBackoffStrategy.create((Duration)Duration.ofSeconds(5L)));
        Duration waitTimeout = optionalOverrideConfig.flatMap(WaiterOverrideConfiguration::waitTimeout).orElse(null);
        return WaiterOverrideConfiguration.builder().maxAttempts(Integer.valueOf(maxAttempts)).backoffStrategy(backoffStrategy).waitTimeout(waitTimeout).build();
    }

    private static WaiterOverrideConfiguration bucketNotExistsWaiterConfig(WaiterOverrideConfiguration overrideConfig) {
        Optional<WaiterOverrideConfiguration> optionalOverrideConfig = Optional.ofNullable(overrideConfig);
        int maxAttempts = optionalOverrideConfig.flatMap(WaiterOverrideConfiguration::maxAttempts).orElse(20);
        BackoffStrategy backoffStrategy = (BackoffStrategy)optionalOverrideConfig.flatMap(WaiterOverrideConfiguration::backoffStrategy).orElse(FixedDelayBackoffStrategy.create((Duration)Duration.ofSeconds(5L)));
        Duration waitTimeout = optionalOverrideConfig.flatMap(WaiterOverrideConfiguration::waitTimeout).orElse(null);
        return WaiterOverrideConfiguration.builder().maxAttempts(Integer.valueOf(maxAttempts)).backoffStrategy(backoffStrategy).waitTimeout(waitTimeout).build();
    }

    private static WaiterOverrideConfiguration objectExistsWaiterConfig(WaiterOverrideConfiguration overrideConfig) {
        Optional<WaiterOverrideConfiguration> optionalOverrideConfig = Optional.ofNullable(overrideConfig);
        int maxAttempts = optionalOverrideConfig.flatMap(WaiterOverrideConfiguration::maxAttempts).orElse(20);
        BackoffStrategy backoffStrategy = (BackoffStrategy)optionalOverrideConfig.flatMap(WaiterOverrideConfiguration::backoffStrategy).orElse(FixedDelayBackoffStrategy.create((Duration)Duration.ofSeconds(5L)));
        Duration waitTimeout = optionalOverrideConfig.flatMap(WaiterOverrideConfiguration::waitTimeout).orElse(null);
        return WaiterOverrideConfiguration.builder().maxAttempts(Integer.valueOf(maxAttempts)).backoffStrategy(backoffStrategy).waitTimeout(waitTimeout).build();
    }

    private static WaiterOverrideConfiguration objectNotExistsWaiterConfig(WaiterOverrideConfiguration overrideConfig) {
        Optional<WaiterOverrideConfiguration> optionalOverrideConfig = Optional.ofNullable(overrideConfig);
        int maxAttempts = optionalOverrideConfig.flatMap(WaiterOverrideConfiguration::maxAttempts).orElse(20);
        BackoffStrategy backoffStrategy = (BackoffStrategy)optionalOverrideConfig.flatMap(WaiterOverrideConfiguration::backoffStrategy).orElse(FixedDelayBackoffStrategy.create((Duration)Duration.ofSeconds(5L)));
        Duration waitTimeout = optionalOverrideConfig.flatMap(WaiterOverrideConfiguration::waitTimeout).orElse(null);
        return WaiterOverrideConfiguration.builder().maxAttempts(Integer.valueOf(maxAttempts)).backoffStrategy(backoffStrategy).waitTimeout(waitTimeout).build();
    }

    public void close() {
        this.managedResources.close();
    }

    public static S3Waiter.Builder builder() {
        return new DefaultBuilder();
    }

    private <T extends S3Request> T applyWaitersUserAgent(T request) {
        Consumer<AwsRequestOverrideConfiguration.Builder> userAgentApplier = b -> {
            AwsRequestOverrideConfiguration.Builder cfr_ignored_0 = (AwsRequestOverrideConfiguration.Builder)b.addApiName(ApiName.builder().version("waiter").name("hll").build());
        };
        AwsRequestOverrideConfiguration overrideConfiguration = request.overrideConfiguration().map(c -> ((AwsRequestOverrideConfiguration.Builder)c.toBuilder().applyMutation(userAgentApplier)).build()).orElse(((AwsRequestOverrideConfiguration.Builder)AwsRequestOverrideConfiguration.builder().applyMutation(userAgentApplier)).build());
        return (T)((Object)((S3Request)request.toBuilder().overrideConfiguration(overrideConfiguration).build()));
    }

    public static final class DefaultBuilder
    implements S3Waiter.Builder {
        private S3Client client;
        private WaiterOverrideConfiguration overrideConfiguration;

        private DefaultBuilder() {
        }

        @Override
        public S3Waiter.Builder overrideConfiguration(WaiterOverrideConfiguration overrideConfiguration) {
            this.overrideConfiguration = overrideConfiguration;
            return this;
        }

        @Override
        public S3Waiter.Builder client(S3Client client) {
            this.client = client;
            return this;
        }

        @Override
        public S3Waiter build() {
            return new DefaultS3Waiter(this);
        }
    }
}

