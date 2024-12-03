/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.annotations.SdkTestInternalApi
 *  software.amazon.awssdk.auth.signer.AwsSignerExecutionAttribute
 *  software.amazon.awssdk.awscore.AwsRequest
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration$Builder
 *  software.amazon.awssdk.core.SdkRequest
 *  software.amazon.awssdk.core.async.AsyncRequestBody
 *  software.amazon.awssdk.core.checksums.ChecksumValidation
 *  software.amazon.awssdk.core.client.config.ClientOverrideConfiguration
 *  software.amazon.awssdk.core.client.config.ClientOverrideConfiguration$Builder
 *  software.amazon.awssdk.core.client.config.SdkAdvancedClientOption
 *  software.amazon.awssdk.core.interceptor.Context$AfterMarshalling
 *  software.amazon.awssdk.core.interceptor.Context$BeforeExecution
 *  software.amazon.awssdk.core.interceptor.ExecutionAttribute
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.ExecutionInterceptor
 *  software.amazon.awssdk.core.interceptor.SdkExecutionAttribute
 *  software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute
 *  software.amazon.awssdk.core.internal.util.ClassLoaderHelper
 *  software.amazon.awssdk.core.retry.RetryPolicy
 *  software.amazon.awssdk.core.signer.NoOpSigner
 *  software.amazon.awssdk.crt.io.ExponentialBackoffRetryOptions
 *  software.amazon.awssdk.crt.io.StandardRetryOptions
 *  software.amazon.awssdk.http.SdkHttpExecutionAttributes
 *  software.amazon.awssdk.http.SdkHttpExecutionAttributes$Builder
 *  software.amazon.awssdk.identity.spi.AwsCredentialsIdentity
 *  software.amazon.awssdk.identity.spi.IdentityProvider
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.utils.CollectionUtils
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.services.s3.internal.crt;

import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.auth.signer.AwsSignerExecutionAttribute;
import software.amazon.awssdk.awscore.AwsRequest;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.checksums.ChecksumValidation;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttribute;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.core.internal.util.ClassLoaderHelper;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.core.signer.NoOpSigner;
import software.amazon.awssdk.crt.io.ExponentialBackoffRetryOptions;
import software.amazon.awssdk.crt.io.StandardRetryOptions;
import software.amazon.awssdk.http.SdkHttpExecutionAttributes;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.DelegatingS3AsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.S3CrtAsyncClientBuilder;
import software.amazon.awssdk.services.s3.crt.S3CrtHttpConfiguration;
import software.amazon.awssdk.services.s3.crt.S3CrtRetryConfiguration;
import software.amazon.awssdk.services.s3.internal.crt.CrtContentLengthOnlyAsyncFileRequestBody;
import software.amazon.awssdk.services.s3.internal.crt.S3CrtAsyncClient;
import software.amazon.awssdk.services.s3.internal.crt.S3CrtAsyncHttpClient;
import software.amazon.awssdk.services.s3.internal.crt.S3InternalSdkHttpExecutionAttribute;
import software.amazon.awssdk.services.s3.internal.crt.S3NativeClientConfiguration;
import software.amazon.awssdk.services.s3.internal.multipart.CopyObjectHelper;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CopyObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.utils.CollectionUtils;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class DefaultS3CrtAsyncClient
extends DelegatingS3AsyncClient
implements S3CrtAsyncClient {
    public static final ExecutionAttribute<Path> OBJECT_FILE_PATH = new ExecutionAttribute("objectFilePath");
    private static final String CRT_CLIENT_CLASSPATH = "software.amazon.awssdk.crt.s3.S3Client";
    private final CopyObjectHelper copyObjectHelper;

    private DefaultS3CrtAsyncClient(DefaultS3CrtClientBuilder builder) {
        super(DefaultS3CrtAsyncClient.initializeS3AsyncClient(builder));
        long partSizeInBytes = builder.minimalPartSizeInBytes == null ? 0x800000L : builder.minimalPartSizeInBytes;
        long thresholdInBytes = builder.thresholdInBytes == null ? partSizeInBytes : builder.thresholdInBytes;
        this.copyObjectHelper = new CopyObjectHelper((S3AsyncClient)this.delegate(), partSizeInBytes, thresholdInBytes);
    }

    @Override
    public CompletableFuture<PutObjectResponse> putObject(PutObjectRequest putObjectRequest, Path sourcePath) {
        AwsRequestOverrideConfiguration overrideConfig = putObjectRequest.overrideConfiguration().map(config -> (AwsRequestOverrideConfiguration.Builder)config.toBuilder().putExecutionAttribute(OBJECT_FILE_PATH, (Object)sourcePath)).orElseGet(() -> (AwsRequestOverrideConfiguration.Builder)AwsRequestOverrideConfiguration.builder().putExecutionAttribute(OBJECT_FILE_PATH, (Object)sourcePath)).build();
        return this.putObject((PutObjectRequest)((Object)putObjectRequest.toBuilder().overrideConfiguration(overrideConfig).build()), (AsyncRequestBody)new CrtContentLengthOnlyAsyncFileRequestBody(sourcePath));
    }

    @Override
    public CompletableFuture<CopyObjectResponse> copyObject(CopyObjectRequest copyObjectRequest) {
        return this.copyObjectHelper.copyObject(copyObjectRequest);
    }

    private static S3AsyncClient initializeS3AsyncClient(DefaultS3CrtClientBuilder builder) {
        ClientOverrideConfiguration.Builder overrideConfigurationBuilder = ClientOverrideConfiguration.builder().putAdvancedOption(SdkAdvancedClientOption.SIGNER, (Object)new NoOpSigner()).putExecutionAttribute(SdkExecutionAttribute.HTTP_RESPONSE_CHECKSUM_VALIDATION, (Object)ChecksumValidation.FORCE_SKIP).retryPolicy(RetryPolicy.none()).addExecutionInterceptor((ExecutionInterceptor)new ValidateRequestInterceptor()).addExecutionInterceptor((ExecutionInterceptor)new AttachHttpAttributesExecutionInterceptor());
        if (builder.executionInterceptors != null) {
            builder.executionInterceptors.forEach(arg_0 -> ((ClientOverrideConfiguration.Builder)overrideConfigurationBuilder).addExecutionInterceptor(arg_0));
        }
        return (S3AsyncClient)((S3AsyncClientBuilder)((S3AsyncClientBuilder)((S3AsyncClientBuilder)((S3AsyncClientBuilder)((S3AsyncClientBuilder)((S3AsyncClientBuilder)((S3AsyncClientBuilder)((S3AsyncClientBuilder)((S3AsyncClientBuilder)S3AsyncClient.builder().serviceConfiguration((S3Configuration)S3Configuration.builder().checksumValidationEnabled(false).build())).region(builder.region)).endpointOverride(builder.endpointOverride)).credentialsProvider(builder.credentialsProvider)).overrideConfiguration((ClientOverrideConfiguration)overrideConfigurationBuilder.build())).accelerate(builder.accelerate)).forcePathStyle(builder.forcePathStyle)).crossRegionAccessEnabled(builder.crossRegionAccessEnabled)).httpClientBuilder(DefaultS3CrtAsyncClient.initializeS3CrtAsyncHttpClient(builder))).build();
    }

    private static S3CrtAsyncHttpClient.Builder initializeS3CrtAsyncHttpClient(DefaultS3CrtClientBuilder builder) {
        DefaultS3CrtAsyncClient.validateCrtInClassPath();
        Validate.isPositiveOrNull((Long)builder.readBufferSizeInBytes, (String)"initialReadBufferSizeInBytes");
        Validate.isPositiveOrNull((Integer)builder.maxConcurrency, (String)"maxConcurrency");
        Validate.isPositiveOrNull((Double)builder.targetThroughputInGbps, (String)"targetThroughputInGbps");
        Validate.isPositiveOrNull((Long)builder.minimalPartSizeInBytes, (String)"minimalPartSizeInBytes");
        Validate.isPositiveOrNull((Long)builder.thresholdInBytes, (String)"thresholdInBytes");
        S3NativeClientConfiguration.Builder nativeClientBuilder = S3NativeClientConfiguration.builder().checksumValidationEnabled(builder.checksumValidationEnabled).targetThroughputInGbps(builder.targetThroughputInGbps).partSizeInBytes(builder.minimalPartSizeInBytes).maxConcurrency(builder.maxConcurrency).signingRegion(builder.region == null ? null : builder.region.id()).endpointOverride(builder.endpointOverride).credentialsProvider((IdentityProvider<? extends AwsCredentialsIdentity>)builder.credentialsProvider).readBufferSizeInBytes(builder.readBufferSizeInBytes).httpConfiguration(builder.httpConfiguration).thresholdInBytes(builder.thresholdInBytes);
        if (builder.retryConfiguration != null) {
            nativeClientBuilder.standardRetryOptions(new StandardRetryOptions().withBackoffRetryOptions(new ExponentialBackoffRetryOptions().withMaxRetries((long)builder.retryConfiguration.numRetries().intValue())));
        }
        return S3CrtAsyncHttpClient.builder().s3ClientConfiguration(nativeClientBuilder.build());
    }

    private static void validateCrtInClassPath() {
        try {
            ClassLoaderHelper.loadClass((String)CRT_CLIENT_CLASSPATH, (boolean)false, (Class[])new Class[0]);
        }
        catch (ClassNotFoundException e) {
            throw new IllegalStateException("Could not load classes from AWS Common Runtime (CRT) library.software.amazon.awssdk.crt:crt is a required dependency; make sure you have it on the classpath.", e);
        }
    }

    private static final class ValidateRequestInterceptor
    implements ExecutionInterceptor {
        private ValidateRequestInterceptor() {
        }

        public void beforeExecution(Context.BeforeExecution context, ExecutionAttributes executionAttributes) {
            ValidateRequestInterceptor.validateOverrideConfiguration(context.request());
        }

        private static void validateOverrideConfiguration(SdkRequest request) {
            if (!(request instanceof AwsRequest)) {
                return;
            }
            if (request.overrideConfiguration().isPresent()) {
                AwsRequestOverrideConfiguration overrideConfiguration = (AwsRequestOverrideConfiguration)request.overrideConfiguration().get();
                if (overrideConfiguration.signer().isPresent()) {
                    throw new UnsupportedOperationException("Request-level signer override is not supported");
                }
                if (overrideConfiguration.credentialsIdentityProvider().isPresent()) {
                    throw new UnsupportedOperationException("Request-level credentials override is not supported");
                }
                if (!CollectionUtils.isNullOrEmpty((Collection)overrideConfiguration.metricPublishers())) {
                    throw new UnsupportedOperationException("Request-level Metric Publishers override is not supported");
                }
                if (overrideConfiguration.apiCallAttemptTimeout().isPresent()) {
                    throw new UnsupportedOperationException("Request-level apiCallAttemptTimeout override is not supported");
                }
            }
        }
    }

    private static final class AttachHttpAttributesExecutionInterceptor
    implements ExecutionInterceptor {
        private AttachHttpAttributesExecutionInterceptor() {
        }

        public void beforeExecution(Context.BeforeExecution context, ExecutionAttributes executionAttributes) {
            executionAttributes.putAttribute(SdkInternalExecutionAttribute.AUTH_SCHEMES, null);
        }

        public void afterMarshalling(Context.AfterMarshalling context, ExecutionAttributes executionAttributes) {
            SdkHttpExecutionAttributes existingHttpAttributes = (SdkHttpExecutionAttributes)executionAttributes.getAttribute(SdkInternalExecutionAttribute.SDK_HTTP_EXECUTION_ATTRIBUTES);
            SdkHttpExecutionAttributes.Builder builder = existingHttpAttributes != null ? existingHttpAttributes.toBuilder() : SdkHttpExecutionAttributes.builder();
            SdkHttpExecutionAttributes attributes = builder.put(S3InternalSdkHttpExecutionAttribute.OPERATION_NAME, executionAttributes.getAttribute(SdkExecutionAttribute.OPERATION_NAME)).put(S3InternalSdkHttpExecutionAttribute.HTTP_CHECKSUM, executionAttributes.getAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM)).put(S3InternalSdkHttpExecutionAttribute.SIGNING_REGION, executionAttributes.getAttribute(AwsSignerExecutionAttribute.SIGNING_REGION)).put(S3InternalSdkHttpExecutionAttribute.OBJECT_FILE_PATH, executionAttributes.getAttribute(OBJECT_FILE_PATH)).build();
            AttachHttpAttributesExecutionInterceptor.disableChecksumForPutAndGet(context, executionAttributes);
            executionAttributes.putAttribute(SdkInternalExecutionAttribute.SDK_HTTP_EXECUTION_ATTRIBUTES, (Object)attributes);
        }

        private static void disableChecksumForPutAndGet(Context.AfterMarshalling context, ExecutionAttributes executionAttributes) {
            if (context.request() instanceof PutObjectRequest || context.request() instanceof GetObjectRequest) {
                executionAttributes.putAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, null);
                executionAttributes.putAttribute(SdkInternalExecutionAttribute.RESOLVED_CHECKSUM_SPECS, null);
            }
        }
    }

    public static final class DefaultS3CrtClientBuilder
    implements S3CrtAsyncClientBuilder {
        private Long readBufferSizeInBytes;
        private IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider;
        private Region region;
        private Long minimalPartSizeInBytes;
        private Double targetThroughputInGbps;
        private Integer maxConcurrency;
        private URI endpointOverride;
        private Boolean checksumValidationEnabled;
        private S3CrtHttpConfiguration httpConfiguration;
        private Boolean accelerate;
        private Boolean forcePathStyle;
        private List<ExecutionInterceptor> executionInterceptors;
        private S3CrtRetryConfiguration retryConfiguration;
        private boolean crossRegionAccessEnabled;
        private Long thresholdInBytes;

        @Override
        public S3CrtAsyncClientBuilder credentialsProvider(IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider) {
            this.credentialsProvider = credentialsProvider;
            return this;
        }

        @Override
        public S3CrtAsyncClientBuilder region(Region region) {
            this.region = region;
            return this;
        }

        @Override
        public S3CrtAsyncClientBuilder minimumPartSizeInBytes(Long partSizeBytes) {
            this.minimalPartSizeInBytes = partSizeBytes;
            return this;
        }

        @Override
        public S3CrtAsyncClientBuilder targetThroughputInGbps(Double targetThroughputInGbps) {
            this.targetThroughputInGbps = targetThroughputInGbps;
            return this;
        }

        @Override
        public S3CrtAsyncClientBuilder maxConcurrency(Integer maxConcurrency) {
            this.maxConcurrency = maxConcurrency;
            return this;
        }

        @Override
        public S3CrtAsyncClientBuilder endpointOverride(URI endpointOverride) {
            this.endpointOverride = endpointOverride;
            return this;
        }

        @Override
        public S3CrtAsyncClientBuilder checksumValidationEnabled(Boolean checksumValidationEnabled) {
            this.checksumValidationEnabled = checksumValidationEnabled;
            return this;
        }

        @Override
        public S3CrtAsyncClientBuilder initialReadBufferSizeInBytes(Long readBufferSizeInBytes) {
            this.readBufferSizeInBytes = readBufferSizeInBytes;
            return this;
        }

        @Override
        public S3CrtAsyncClientBuilder httpConfiguration(S3CrtHttpConfiguration configuration) {
            this.httpConfiguration = configuration;
            return this;
        }

        @Override
        public S3CrtAsyncClientBuilder accelerate(Boolean accelerate) {
            this.accelerate = accelerate;
            return this;
        }

        @Override
        public S3CrtAsyncClientBuilder forcePathStyle(Boolean forcePathStyle) {
            this.forcePathStyle = forcePathStyle;
            return this;
        }

        @SdkTestInternalApi
        S3CrtAsyncClientBuilder addExecutionInterceptor(ExecutionInterceptor executionInterceptor) {
            if (this.executionInterceptors == null) {
                this.executionInterceptors = new ArrayList<ExecutionInterceptor>();
            }
            this.executionInterceptors.add(executionInterceptor);
            return this;
        }

        @Override
        public S3CrtAsyncClientBuilder retryConfiguration(S3CrtRetryConfiguration retryConfiguration) {
            this.retryConfiguration = retryConfiguration;
            return this;
        }

        @Override
        public S3CrtAsyncClientBuilder crossRegionAccessEnabled(Boolean crossRegionAccessEnabled) {
            this.crossRegionAccessEnabled = crossRegionAccessEnabled;
            return this;
        }

        @Override
        public S3CrtAsyncClientBuilder thresholdInBytes(Long thresholdInBytes) {
            this.thresholdInBytes = thresholdInBytes;
            return this;
        }

        @Override
        public S3CrtAsyncClient build() {
            return new DefaultS3CrtAsyncClient(this);
        }
    }
}

