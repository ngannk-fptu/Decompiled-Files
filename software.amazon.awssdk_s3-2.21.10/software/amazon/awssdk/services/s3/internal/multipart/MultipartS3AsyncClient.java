/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration$Builder
 *  software.amazon.awssdk.core.ApiName
 *  software.amazon.awssdk.core.async.AsyncRequestBody
 *  software.amazon.awssdk.core.async.AsyncResponseTransformer
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.services.s3.internal.multipart;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.core.ApiName;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.DelegatingS3AsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.internal.UserAgentUtils;
import software.amazon.awssdk.services.s3.internal.multipart.CopyObjectHelper;
import software.amazon.awssdk.services.s3.internal.multipart.MultipartConfigurationResolver;
import software.amazon.awssdk.services.s3.internal.multipart.UploadObjectHelper;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CopyObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Request;
import software.amazon.awssdk.services.s3.multipart.MultipartConfiguration;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class MultipartS3AsyncClient
extends DelegatingS3AsyncClient {
    private static final ApiName USER_AGENT_API_NAME = ApiName.builder().name("hll").version("s3Multipart").build();
    private final UploadObjectHelper mpuHelper;
    private final CopyObjectHelper copyObjectHelper;

    private MultipartS3AsyncClient(S3AsyncClient delegate, MultipartConfiguration multipartConfiguration) {
        super(delegate);
        MultipartConfiguration validConfiguration = (MultipartConfiguration)Validate.getOrDefault((Object)multipartConfiguration, () -> ((MultipartConfiguration.Builder)MultipartConfiguration.builder()).build());
        MultipartConfigurationResolver resolver = new MultipartConfigurationResolver(validConfiguration);
        long minPartSizeInBytes = resolver.minimalPartSizeInBytes();
        long threshold = resolver.thresholdInBytes();
        this.mpuHelper = new UploadObjectHelper(delegate, resolver);
        this.copyObjectHelper = new CopyObjectHelper(delegate, minPartSizeInBytes, threshold);
    }

    @Override
    public CompletableFuture<PutObjectResponse> putObject(PutObjectRequest putObjectRequest, AsyncRequestBody requestBody) {
        return this.mpuHelper.uploadObject(putObjectRequest, requestBody);
    }

    @Override
    public CompletableFuture<CopyObjectResponse> copyObject(CopyObjectRequest copyObjectRequest) {
        return this.copyObjectHelper.copyObject(copyObjectRequest);
    }

    @Override
    public <ReturnT> CompletableFuture<ReturnT> getObject(GetObjectRequest getObjectRequest, AsyncResponseTransformer<GetObjectResponse, ReturnT> asyncResponseTransformer) {
        throw new UnsupportedOperationException("Multipart download is not yet supported. Instead use the CRT based S3 client for multipart download.");
    }

    @Override
    public void close() {
        this.delegate().close();
    }

    public static MultipartS3AsyncClient create(S3AsyncClient client, MultipartConfiguration multipartConfiguration) {
        DelegatingS3AsyncClient clientWithUserAgent = new DelegatingS3AsyncClient(client){

            @Override
            protected <T extends S3Request, ReturnT> CompletableFuture<ReturnT> invokeOperation(T request, Function<T, CompletableFuture<ReturnT>> operation) {
                T requestWithUserAgent = UserAgentUtils.applyUserAgentInfo(request, c -> {
                    AwsRequestOverrideConfiguration.Builder cfr_ignored_0 = (AwsRequestOverrideConfiguration.Builder)c.addApiName(USER_AGENT_API_NAME);
                });
                return operation.apply(requestWithUserAgent);
            }
        };
        return new MultipartS3AsyncClient(clientWithUserAgent, multipartConfiguration);
    }
}

