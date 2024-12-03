/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration$Builder
 *  software.amazon.awssdk.awscore.exception.AwsErrorDetails
 *  software.amazon.awssdk.core.ApiName
 *  software.amazon.awssdk.endpoints.EndpointProvider
 *  software.amazon.awssdk.regions.Region
 */
package software.amazon.awssdk.services.s3.internal.crossregion.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.core.ApiName;
import software.amazon.awssdk.endpoints.EndpointProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.endpoints.S3EndpointProvider;
import software.amazon.awssdk.services.s3.internal.crossregion.endpointprovider.BucketEndpointProvider;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Request;

@SdkInternalApi
public final class CrossRegionUtils {
    public static final int REDIRECT_STATUS_CODE = 301;
    public static final int TEMPORARY_REDIRECT_STATUS_CODE = 307;
    public static final String AMZ_BUCKET_REGION_HEADER = "x-amz-bucket-region";
    private static final List<Integer> REDIRECT_STATUS_CODES = Arrays.asList(301, 307);
    private static final List<String> REDIRECT_ERROR_CODES = Collections.singletonList("AuthorizationHeaderMalformed");
    private static final ApiName API_NAME = ApiName.builder().version("cross-region").name("hll").build();
    private static final Consumer<AwsRequestOverrideConfiguration.Builder> USER_AGENT_APPLIER = b -> {
        AwsRequestOverrideConfiguration.Builder cfr_ignored_0 = (AwsRequestOverrideConfiguration.Builder)b.addApiName(API_NAME);
    };

    private CrossRegionUtils() {
    }

    public static Optional<String> getBucketRegionFromException(S3Exception exception) {
        return exception.awsErrorDetails().sdkHttpResponse().firstMatchingHeader(AMZ_BUCKET_REGION_HEADER);
    }

    public static boolean isS3RedirectException(Throwable exception) {
        Throwable exceptionToBeChecked = exception instanceof CompletionException ? exception.getCause() : exception;
        return exceptionToBeChecked instanceof S3Exception && CrossRegionUtils.isRedirectError((S3Exception)((Object)exceptionToBeChecked));
    }

    private static boolean isRedirectError(S3Exception exceptionToBeChecked) {
        if (REDIRECT_STATUS_CODES.stream().anyMatch(status -> status.equals(exceptionToBeChecked.statusCode()))) {
            return true;
        }
        if (CrossRegionUtils.getBucketRegionFromException(exceptionToBeChecked).isPresent()) {
            return true;
        }
        AwsErrorDetails awsErrorDetails = exceptionToBeChecked.awsErrorDetails();
        return awsErrorDetails != null && REDIRECT_ERROR_CODES.stream().anyMatch(code -> code.equals(awsErrorDetails.errorCode()));
    }

    public static <T extends S3Request> T requestWithDecoratedEndpointProvider(T request, Supplier<Region> regionSupplier, EndpointProvider clientEndpointProvider) {
        AwsRequestOverrideConfiguration requestOverrideConfig = request.overrideConfiguration().orElseGet(() -> AwsRequestOverrideConfiguration.builder().build());
        S3EndpointProvider delegateEndpointProvider = (S3EndpointProvider)requestOverrideConfig.endpointProvider().orElse(clientEndpointProvider);
        return (T)((Object)((S3Request)request.toBuilder().overrideConfiguration(((AwsRequestOverrideConfiguration.Builder)requestOverrideConfig.toBuilder().endpointProvider((EndpointProvider)BucketEndpointProvider.create(delegateEndpointProvider, regionSupplier))).build()).build()));
    }

    public static <T extends S3Request> AwsRequestOverrideConfiguration updateUserAgentInConfig(T request) {
        return request.overrideConfiguration().map(c -> ((AwsRequestOverrideConfiguration.Builder)c.toBuilder().applyMutation(USER_AGENT_APPLIER)).build()).orElseGet(() -> ((AwsRequestOverrideConfiguration.Builder)AwsRequestOverrideConfiguration.builder().applyMutation(USER_AGENT_APPLIER)).build());
    }
}

