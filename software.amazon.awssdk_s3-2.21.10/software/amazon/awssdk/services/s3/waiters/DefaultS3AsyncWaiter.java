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
 *  software.amazon.awssdk.core.waiters.AsyncWaiter
 *  software.amazon.awssdk.core.waiters.AsyncWaiter$Builder
 *  software.amazon.awssdk.core.waiters.WaiterAcceptor
 *  software.amazon.awssdk.core.waiters.WaiterOverrideConfiguration
 *  software.amazon.awssdk.core.waiters.WaiterResponse
 *  software.amazon.awssdk.core.waiters.WaiterState
 *  software.amazon.awssdk.utils.AttributeMap
 *  software.amazon.awssdk.utils.AttributeMap$Builder
 *  software.amazon.awssdk.utils.SdkAutoCloseable
 *  software.amazon.awssdk.utils.ThreadFactoryBuilder
 */
package software.amazon.awssdk.services.s3.waiters;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ApiName;
import software.amazon.awssdk.core.internal.waiters.WaiterAttribute;
import software.amazon.awssdk.core.retry.backoff.BackoffStrategy;
import software.amazon.awssdk.core.retry.backoff.FixedDelayBackoffStrategy;
import software.amazon.awssdk.core.waiters.AsyncWaiter;
import software.amazon.awssdk.core.waiters.WaiterAcceptor;
import software.amazon.awssdk.core.waiters.WaiterOverrideConfiguration;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.core.waiters.WaiterState;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Request;
import software.amazon.awssdk.services.s3.waiters.S3AsyncWaiter;
import software.amazon.awssdk.services.s3.waiters.internal.WaitersRuntime;
import software.amazon.awssdk.utils.AttributeMap;
import software.amazon.awssdk.utils.SdkAutoCloseable;
import software.amazon.awssdk.utils.ThreadFactoryBuilder;

@SdkInternalApi
@ThreadSafe
final class DefaultS3AsyncWaiter
implements S3AsyncWaiter {
    private static final WaiterAttribute<SdkAutoCloseable> CLIENT_ATTRIBUTE = new WaiterAttribute(SdkAutoCloseable.class);
    private static final WaiterAttribute<ScheduledExecutorService> SCHEDULED_EXECUTOR_SERVICE_ATTRIBUTE = new WaiterAttribute(ScheduledExecutorService.class);
    private final S3AsyncClient client;
    private final AttributeMap managedResources;
    private final AsyncWaiter<HeadBucketResponse> bucketExistsWaiter;
    private final AsyncWaiter<HeadBucketResponse> bucketNotExistsWaiter;
    private final AsyncWaiter<HeadObjectResponse> objectExistsWaiter;
    private final AsyncWaiter<HeadObjectResponse> objectNotExistsWaiter;
    private final ScheduledExecutorService executorService;

    private DefaultS3AsyncWaiter(DefaultBuilder builder) {
        AttributeMap.Builder attributeMapBuilder = AttributeMap.builder();
        if (builder.client == null) {
            this.client = (S3AsyncClient)S3AsyncClient.builder().build();
            attributeMapBuilder.put(CLIENT_ATTRIBUTE, (Object)this.client);
        } else {
            this.client = builder.client;
        }
        if (builder.executorService == null) {
            this.executorService = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().threadNamePrefix("waiters-ScheduledExecutor").build());
            attributeMapBuilder.put(SCHEDULED_EXECUTOR_SERVICE_ATTRIBUTE, (Object)this.executorService);
        } else {
            this.executorService = builder.executorService;
        }
        this.managedResources = attributeMapBuilder.build();
        this.bucketExistsWaiter = ((AsyncWaiter.Builder)((AsyncWaiter.Builder)AsyncWaiter.builder(HeadBucketResponse.class).acceptors(DefaultS3AsyncWaiter.bucketExistsWaiterAcceptors())).overrideConfiguration(DefaultS3AsyncWaiter.bucketExistsWaiterConfig(builder.overrideConfiguration))).scheduledExecutorService(this.executorService).build();
        this.bucketNotExistsWaiter = ((AsyncWaiter.Builder)((AsyncWaiter.Builder)AsyncWaiter.builder(HeadBucketResponse.class).acceptors(DefaultS3AsyncWaiter.bucketNotExistsWaiterAcceptors())).overrideConfiguration(DefaultS3AsyncWaiter.bucketNotExistsWaiterConfig(builder.overrideConfiguration))).scheduledExecutorService(this.executorService).build();
        this.objectExistsWaiter = ((AsyncWaiter.Builder)((AsyncWaiter.Builder)AsyncWaiter.builder(HeadObjectResponse.class).acceptors(DefaultS3AsyncWaiter.objectExistsWaiterAcceptors())).overrideConfiguration(DefaultS3AsyncWaiter.objectExistsWaiterConfig(builder.overrideConfiguration))).scheduledExecutorService(this.executorService).build();
        this.objectNotExistsWaiter = ((AsyncWaiter.Builder)((AsyncWaiter.Builder)AsyncWaiter.builder(HeadObjectResponse.class).acceptors(DefaultS3AsyncWaiter.objectNotExistsWaiterAcceptors())).overrideConfiguration(DefaultS3AsyncWaiter.objectNotExistsWaiterConfig(builder.overrideConfiguration))).scheduledExecutorService(this.executorService).build();
    }

    private static String errorCode(Throwable error) {
        if (error instanceof AwsServiceException) {
            return ((AwsServiceException)error).awsErrorDetails().errorCode();
        }
        return null;
    }

    @Override
    public CompletableFuture<WaiterResponse<HeadBucketResponse>> waitUntilBucketExists(HeadBucketRequest headBucketRequest) {
        return this.bucketExistsWaiter.runAsync(() -> this.client.headBucket(this.applyWaitersUserAgent(headBucketRequest)));
    }

    @Override
    public CompletableFuture<WaiterResponse<HeadBucketResponse>> waitUntilBucketExists(HeadBucketRequest headBucketRequest, WaiterOverrideConfiguration overrideConfig) {
        return this.bucketExistsWaiter.runAsync(() -> this.client.headBucket(this.applyWaitersUserAgent(headBucketRequest)), DefaultS3AsyncWaiter.bucketExistsWaiterConfig(overrideConfig));
    }

    @Override
    public CompletableFuture<WaiterResponse<HeadBucketResponse>> waitUntilBucketNotExists(HeadBucketRequest headBucketRequest) {
        return this.bucketNotExistsWaiter.runAsync(() -> this.client.headBucket(this.applyWaitersUserAgent(headBucketRequest)));
    }

    @Override
    public CompletableFuture<WaiterResponse<HeadBucketResponse>> waitUntilBucketNotExists(HeadBucketRequest headBucketRequest, WaiterOverrideConfiguration overrideConfig) {
        return this.bucketNotExistsWaiter.runAsync(() -> this.client.headBucket(this.applyWaitersUserAgent(headBucketRequest)), DefaultS3AsyncWaiter.bucketNotExistsWaiterConfig(overrideConfig));
    }

    @Override
    public CompletableFuture<WaiterResponse<HeadObjectResponse>> waitUntilObjectExists(HeadObjectRequest headObjectRequest) {
        return this.objectExistsWaiter.runAsync(() -> this.client.headObject(this.applyWaitersUserAgent(headObjectRequest)));
    }

    @Override
    public CompletableFuture<WaiterResponse<HeadObjectResponse>> waitUntilObjectExists(HeadObjectRequest headObjectRequest, WaiterOverrideConfiguration overrideConfig) {
        return this.objectExistsWaiter.runAsync(() -> this.client.headObject(this.applyWaitersUserAgent(headObjectRequest)), DefaultS3AsyncWaiter.objectExistsWaiterConfig(overrideConfig));
    }

    @Override
    public CompletableFuture<WaiterResponse<HeadObjectResponse>> waitUntilObjectNotExists(HeadObjectRequest headObjectRequest) {
        return this.objectNotExistsWaiter.runAsync(() -> this.client.headObject(this.applyWaitersUserAgent(headObjectRequest)));
    }

    @Override
    public CompletableFuture<WaiterResponse<HeadObjectResponse>> waitUntilObjectNotExists(HeadObjectRequest headObjectRequest, WaiterOverrideConfiguration overrideConfig) {
        return this.objectNotExistsWaiter.runAsync(() -> this.client.headObject(this.applyWaitersUserAgent(headObjectRequest)), DefaultS3AsyncWaiter.objectNotExistsWaiterConfig(overrideConfig));
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

    public static S3AsyncWaiter.Builder builder() {
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
    implements S3AsyncWaiter.Builder {
        private S3AsyncClient client;
        private WaiterOverrideConfiguration overrideConfiguration;
        private ScheduledExecutorService executorService;

        private DefaultBuilder() {
        }

        @Override
        public S3AsyncWaiter.Builder scheduledExecutorService(ScheduledExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        @Override
        public S3AsyncWaiter.Builder overrideConfiguration(WaiterOverrideConfiguration overrideConfiguration) {
            this.overrideConfiguration = overrideConfiguration;
            return this;
        }

        @Override
        public S3AsyncWaiter.Builder client(S3AsyncClient client) {
            this.client = client;
            return this;
        }

        @Override
        public S3AsyncWaiter build() {
            return new DefaultS3AsyncWaiter(this);
        }
    }
}

