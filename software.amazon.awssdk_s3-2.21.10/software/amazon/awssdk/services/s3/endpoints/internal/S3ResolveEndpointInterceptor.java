/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.auth.signer.AwsS3V4Signer
 *  software.amazon.awssdk.auth.signer.SignerLoader
 *  software.amazon.awssdk.awscore.AwsExecutionAttribute
 *  software.amazon.awssdk.awscore.endpoints.AwsEndpointAttribute
 *  software.amazon.awssdk.awscore.endpoints.authscheme.EndpointAuthScheme
 *  software.amazon.awssdk.awscore.endpoints.authscheme.SigV4AuthScheme
 *  software.amazon.awssdk.awscore.endpoints.authscheme.SigV4aAuthScheme
 *  software.amazon.awssdk.awscore.util.SignerOverrideUtils
 *  software.amazon.awssdk.core.SdkRequest
 *  software.amazon.awssdk.core.SelectedAuthScheme
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.core.interceptor.Context$ModifyHttpRequest
 *  software.amazon.awssdk.core.interceptor.Context$ModifyRequest
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.ExecutionInterceptor
 *  software.amazon.awssdk.core.interceptor.SdkExecutionAttribute
 *  software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute
 *  software.amazon.awssdk.core.signer.Signer
 *  software.amazon.awssdk.endpoints.Endpoint
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.http.SdkHttpRequest$Builder
 *  software.amazon.awssdk.http.auth.aws.signer.AwsV4HttpSigner
 *  software.amazon.awssdk.http.auth.aws.signer.AwsV4aHttpSigner
 *  software.amazon.awssdk.http.auth.aws.signer.RegionSet
 *  software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption
 *  software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption$Builder
 *  software.amazon.awssdk.identity.spi.Identity
 *  software.amazon.awssdk.utils.AttributeMap
 *  software.amazon.awssdk.utils.HostnameValidator
 */
package software.amazon.awssdk.services.s3.endpoints.internal;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.signer.AwsS3V4Signer;
import software.amazon.awssdk.auth.signer.SignerLoader;
import software.amazon.awssdk.awscore.AwsExecutionAttribute;
import software.amazon.awssdk.awscore.endpoints.AwsEndpointAttribute;
import software.amazon.awssdk.awscore.endpoints.authscheme.EndpointAuthScheme;
import software.amazon.awssdk.awscore.endpoints.authscheme.SigV4AuthScheme;
import software.amazon.awssdk.awscore.endpoints.authscheme.SigV4aAuthScheme;
import software.amazon.awssdk.awscore.util.SignerOverrideUtils;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.SelectedAuthScheme;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.endpoints.Endpoint;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4HttpSigner;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4aHttpSigner;
import software.amazon.awssdk.http.auth.aws.signer.RegionSet;
import software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption;
import software.amazon.awssdk.identity.spi.Identity;
import software.amazon.awssdk.services.s3.endpoints.S3ClientContextParams;
import software.amazon.awssdk.services.s3.endpoints.S3EndpointParams;
import software.amazon.awssdk.services.s3.endpoints.S3EndpointProvider;
import software.amazon.awssdk.services.s3.endpoints.internal.AuthSchemeUtils;
import software.amazon.awssdk.services.s3.endpoints.internal.AwsEndpointProviderUtils;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketAnalyticsConfigurationRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketCorsRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketEncryptionRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketIntelligentTieringConfigurationRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketInventoryConfigurationRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketLifecycleRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketMetricsConfigurationRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketOwnershipControlsRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketPolicyRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketReplicationRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketTaggingRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketWebsiteRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeletePublicAccessBlockRequest;
import software.amazon.awssdk.services.s3.model.GetBucketAccelerateConfigurationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketAclRequest;
import software.amazon.awssdk.services.s3.model.GetBucketAnalyticsConfigurationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketCorsRequest;
import software.amazon.awssdk.services.s3.model.GetBucketEncryptionRequest;
import software.amazon.awssdk.services.s3.model.GetBucketIntelligentTieringConfigurationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketInventoryConfigurationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketLifecycleConfigurationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketLocationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketLoggingRequest;
import software.amazon.awssdk.services.s3.model.GetBucketMetricsConfigurationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketNotificationConfigurationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketOwnershipControlsRequest;
import software.amazon.awssdk.services.s3.model.GetBucketPolicyRequest;
import software.amazon.awssdk.services.s3.model.GetBucketPolicyStatusRequest;
import software.amazon.awssdk.services.s3.model.GetBucketReplicationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketRequestPaymentRequest;
import software.amazon.awssdk.services.s3.model.GetBucketTaggingRequest;
import software.amazon.awssdk.services.s3.model.GetBucketVersioningRequest;
import software.amazon.awssdk.services.s3.model.GetBucketWebsiteRequest;
import software.amazon.awssdk.services.s3.model.GetObjectAclRequest;
import software.amazon.awssdk.services.s3.model.GetObjectAttributesRequest;
import software.amazon.awssdk.services.s3.model.GetObjectLegalHoldRequest;
import software.amazon.awssdk.services.s3.model.GetObjectLockConfigurationRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRetentionRequest;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.GetObjectTorrentRequest;
import software.amazon.awssdk.services.s3.model.GetPublicAccessBlockRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.ListBucketAnalyticsConfigurationsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketIntelligentTieringConfigurationsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketInventoryConfigurationsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketMetricsConfigurationsRequest;
import software.amazon.awssdk.services.s3.model.ListMultipartUploadsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListPartsRequest;
import software.amazon.awssdk.services.s3.model.PutBucketAccelerateConfigurationRequest;
import software.amazon.awssdk.services.s3.model.PutBucketAclRequest;
import software.amazon.awssdk.services.s3.model.PutBucketAnalyticsConfigurationRequest;
import software.amazon.awssdk.services.s3.model.PutBucketCorsRequest;
import software.amazon.awssdk.services.s3.model.PutBucketEncryptionRequest;
import software.amazon.awssdk.services.s3.model.PutBucketIntelligentTieringConfigurationRequest;
import software.amazon.awssdk.services.s3.model.PutBucketInventoryConfigurationRequest;
import software.amazon.awssdk.services.s3.model.PutBucketLifecycleConfigurationRequest;
import software.amazon.awssdk.services.s3.model.PutBucketLoggingRequest;
import software.amazon.awssdk.services.s3.model.PutBucketMetricsConfigurationRequest;
import software.amazon.awssdk.services.s3.model.PutBucketNotificationConfigurationRequest;
import software.amazon.awssdk.services.s3.model.PutBucketOwnershipControlsRequest;
import software.amazon.awssdk.services.s3.model.PutBucketPolicyRequest;
import software.amazon.awssdk.services.s3.model.PutBucketReplicationRequest;
import software.amazon.awssdk.services.s3.model.PutBucketRequestPaymentRequest;
import software.amazon.awssdk.services.s3.model.PutBucketTaggingRequest;
import software.amazon.awssdk.services.s3.model.PutBucketVersioningRequest;
import software.amazon.awssdk.services.s3.model.PutBucketWebsiteRequest;
import software.amazon.awssdk.services.s3.model.PutObjectAclRequest;
import software.amazon.awssdk.services.s3.model.PutObjectLegalHoldRequest;
import software.amazon.awssdk.services.s3.model.PutObjectLockConfigurationRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRetentionRequest;
import software.amazon.awssdk.services.s3.model.PutObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.PutPublicAccessBlockRequest;
import software.amazon.awssdk.services.s3.model.RestoreObjectRequest;
import software.amazon.awssdk.services.s3.model.SelectObjectContentRequest;
import software.amazon.awssdk.services.s3.model.UploadPartCopyRequest;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.utils.AttributeMap;
import software.amazon.awssdk.utils.HostnameValidator;

@SdkInternalApi
public final class S3ResolveEndpointInterceptor
implements ExecutionInterceptor {
    public SdkRequest modifyRequest(Context.ModifyRequest context, ExecutionAttributes executionAttributes) {
        SdkRequest result = context.request();
        if (AwsEndpointProviderUtils.endpointIsDiscovered(executionAttributes)) {
            return result;
        }
        S3EndpointProvider provider = (S3EndpointProvider)executionAttributes.getAttribute(SdkInternalExecutionAttribute.ENDPOINT_PROVIDER);
        try {
            Optional<String> hostPrefix;
            Endpoint endpoint = provider.resolveEndpoint(S3ResolveEndpointInterceptor.ruleParams(result, executionAttributes)).join();
            if (!AwsEndpointProviderUtils.disableHostPrefixInjection(executionAttributes) && (hostPrefix = S3ResolveEndpointInterceptor.hostPrefix((String)executionAttributes.getAttribute(SdkExecutionAttribute.OPERATION_NAME), result)).isPresent()) {
                endpoint = AwsEndpointProviderUtils.addHostPrefix(endpoint, hostPrefix.get());
            }
            List endpointAuthSchemes = (List)endpoint.attribute(AwsEndpointAttribute.AUTH_SCHEMES);
            SelectedAuthScheme selectedAuthScheme = (SelectedAuthScheme)executionAttributes.getAttribute(SdkInternalExecutionAttribute.SELECTED_AUTH_SCHEME);
            if (endpointAuthSchemes != null && selectedAuthScheme != null) {
                selectedAuthScheme = this.authSchemeWithEndpointSignerProperties(endpointAuthSchemes, selectedAuthScheme);
                executionAttributes.putAttribute(SdkInternalExecutionAttribute.SELECTED_AUTH_SCHEME, selectedAuthScheme);
            }
            if (endpointAuthSchemes != null) {
                EndpointAuthScheme chosenAuthScheme = AuthSchemeUtils.chooseAuthScheme(endpointAuthSchemes);
                Supplier<Signer> signerProvider = this.signerProvider(chosenAuthScheme);
                result = SignerOverrideUtils.overrideSignerIfNotOverridden((SdkRequest)result, (ExecutionAttributes)executionAttributes, signerProvider);
            }
            executionAttributes.putAttribute(SdkInternalExecutionAttribute.RESOLVED_ENDPOINT, (Object)endpoint);
            return result;
        }
        catch (CompletionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof SdkClientException) {
                throw (SdkClientException)cause;
            }
            throw SdkClientException.create((String)"Endpoint resolution failed", (Throwable)cause);
        }
    }

    public SdkHttpRequest modifyHttpRequest(Context.ModifyHttpRequest context, ExecutionAttributes executionAttributes) {
        Endpoint resolvedEndpoint = (Endpoint)executionAttributes.getAttribute(SdkInternalExecutionAttribute.RESOLVED_ENDPOINT);
        if (resolvedEndpoint.headers().isEmpty()) {
            return context.httpRequest();
        }
        SdkHttpRequest.Builder httpRequestBuilder = (SdkHttpRequest.Builder)context.httpRequest().toBuilder();
        resolvedEndpoint.headers().forEach((name, values) -> values.forEach(v -> httpRequestBuilder.appendHeader(name, v)));
        return (SdkHttpRequest)httpRequestBuilder.build();
    }

    public static S3EndpointParams ruleParams(SdkRequest request, ExecutionAttributes executionAttributes) {
        S3EndpointParams.Builder builder = S3EndpointParams.builder();
        builder.region(AwsEndpointProviderUtils.regionBuiltIn(executionAttributes));
        builder.useFips(AwsEndpointProviderUtils.fipsEnabledBuiltIn(executionAttributes));
        builder.useDualStack(AwsEndpointProviderUtils.dualStackEnabledBuiltIn(executionAttributes));
        builder.endpoint(AwsEndpointProviderUtils.endpointBuiltIn(executionAttributes));
        builder.useGlobalEndpoint(AwsEndpointProviderUtils.useGlobalEndpointBuiltIn(executionAttributes));
        S3ResolveEndpointInterceptor.setClientContextParams(builder, executionAttributes);
        S3ResolveEndpointInterceptor.setContextParams(builder, (String)executionAttributes.getAttribute(AwsExecutionAttribute.OPERATION_NAME), request);
        S3ResolveEndpointInterceptor.setStaticContextParams(builder, (String)executionAttributes.getAttribute(AwsExecutionAttribute.OPERATION_NAME));
        return builder.build();
    }

    private static void setContextParams(S3EndpointParams.Builder params, String operationName, SdkRequest request) {
        switch (operationName) {
            case "AbortMultipartUpload": {
                S3ResolveEndpointInterceptor.setContextParams(params, (AbortMultipartUploadRequest)request);
                break;
            }
            case "CompleteMultipartUpload": {
                S3ResolveEndpointInterceptor.setContextParams(params, (CompleteMultipartUploadRequest)request);
                break;
            }
            case "CopyObject": {
                S3ResolveEndpointInterceptor.setContextParams(params, (CopyObjectRequest)request);
                break;
            }
            case "CreateBucket": {
                S3ResolveEndpointInterceptor.setContextParams(params, (CreateBucketRequest)request);
                break;
            }
            case "CreateMultipartUpload": {
                S3ResolveEndpointInterceptor.setContextParams(params, (CreateMultipartUploadRequest)request);
                break;
            }
            case "DeleteBucket": {
                S3ResolveEndpointInterceptor.setContextParams(params, (DeleteBucketRequest)request);
                break;
            }
            case "DeleteBucketAnalyticsConfiguration": {
                S3ResolveEndpointInterceptor.setContextParams(params, (DeleteBucketAnalyticsConfigurationRequest)request);
                break;
            }
            case "DeleteBucketCors": {
                S3ResolveEndpointInterceptor.setContextParams(params, (DeleteBucketCorsRequest)request);
                break;
            }
            case "DeleteBucketEncryption": {
                S3ResolveEndpointInterceptor.setContextParams(params, (DeleteBucketEncryptionRequest)request);
                break;
            }
            case "DeleteBucketIntelligentTieringConfiguration": {
                S3ResolveEndpointInterceptor.setContextParams(params, (DeleteBucketIntelligentTieringConfigurationRequest)request);
                break;
            }
            case "DeleteBucketInventoryConfiguration": {
                S3ResolveEndpointInterceptor.setContextParams(params, (DeleteBucketInventoryConfigurationRequest)request);
                break;
            }
            case "DeleteBucketLifecycle": {
                S3ResolveEndpointInterceptor.setContextParams(params, (DeleteBucketLifecycleRequest)request);
                break;
            }
            case "DeleteBucketMetricsConfiguration": {
                S3ResolveEndpointInterceptor.setContextParams(params, (DeleteBucketMetricsConfigurationRequest)request);
                break;
            }
            case "DeleteBucketOwnershipControls": {
                S3ResolveEndpointInterceptor.setContextParams(params, (DeleteBucketOwnershipControlsRequest)request);
                break;
            }
            case "DeleteBucketPolicy": {
                S3ResolveEndpointInterceptor.setContextParams(params, (DeleteBucketPolicyRequest)request);
                break;
            }
            case "DeleteBucketReplication": {
                S3ResolveEndpointInterceptor.setContextParams(params, (DeleteBucketReplicationRequest)request);
                break;
            }
            case "DeleteBucketTagging": {
                S3ResolveEndpointInterceptor.setContextParams(params, (DeleteBucketTaggingRequest)request);
                break;
            }
            case "DeleteBucketWebsite": {
                S3ResolveEndpointInterceptor.setContextParams(params, (DeleteBucketWebsiteRequest)request);
                break;
            }
            case "DeleteObject": {
                S3ResolveEndpointInterceptor.setContextParams(params, (DeleteObjectRequest)request);
                break;
            }
            case "DeleteObjectTagging": {
                S3ResolveEndpointInterceptor.setContextParams(params, (DeleteObjectTaggingRequest)request);
                break;
            }
            case "DeleteObjects": {
                S3ResolveEndpointInterceptor.setContextParams(params, (DeleteObjectsRequest)request);
                break;
            }
            case "DeletePublicAccessBlock": {
                S3ResolveEndpointInterceptor.setContextParams(params, (DeletePublicAccessBlockRequest)request);
                break;
            }
            case "GetBucketAccelerateConfiguration": {
                S3ResolveEndpointInterceptor.setContextParams(params, (GetBucketAccelerateConfigurationRequest)request);
                break;
            }
            case "GetBucketAcl": {
                S3ResolveEndpointInterceptor.setContextParams(params, (GetBucketAclRequest)request);
                break;
            }
            case "GetBucketAnalyticsConfiguration": {
                S3ResolveEndpointInterceptor.setContextParams(params, (GetBucketAnalyticsConfigurationRequest)request);
                break;
            }
            case "GetBucketCors": {
                S3ResolveEndpointInterceptor.setContextParams(params, (GetBucketCorsRequest)request);
                break;
            }
            case "GetBucketEncryption": {
                S3ResolveEndpointInterceptor.setContextParams(params, (GetBucketEncryptionRequest)request);
                break;
            }
            case "GetBucketIntelligentTieringConfiguration": {
                S3ResolveEndpointInterceptor.setContextParams(params, (GetBucketIntelligentTieringConfigurationRequest)request);
                break;
            }
            case "GetBucketInventoryConfiguration": {
                S3ResolveEndpointInterceptor.setContextParams(params, (GetBucketInventoryConfigurationRequest)request);
                break;
            }
            case "GetBucketLifecycleConfiguration": {
                S3ResolveEndpointInterceptor.setContextParams(params, (GetBucketLifecycleConfigurationRequest)request);
                break;
            }
            case "GetBucketLocation": {
                S3ResolveEndpointInterceptor.setContextParams(params, (GetBucketLocationRequest)request);
                break;
            }
            case "GetBucketLogging": {
                S3ResolveEndpointInterceptor.setContextParams(params, (GetBucketLoggingRequest)request);
                break;
            }
            case "GetBucketMetricsConfiguration": {
                S3ResolveEndpointInterceptor.setContextParams(params, (GetBucketMetricsConfigurationRequest)request);
                break;
            }
            case "GetBucketNotificationConfiguration": {
                S3ResolveEndpointInterceptor.setContextParams(params, (GetBucketNotificationConfigurationRequest)request);
                break;
            }
            case "GetBucketOwnershipControls": {
                S3ResolveEndpointInterceptor.setContextParams(params, (GetBucketOwnershipControlsRequest)request);
                break;
            }
            case "GetBucketPolicy": {
                S3ResolveEndpointInterceptor.setContextParams(params, (GetBucketPolicyRequest)request);
                break;
            }
            case "GetBucketPolicyStatus": {
                S3ResolveEndpointInterceptor.setContextParams(params, (GetBucketPolicyStatusRequest)request);
                break;
            }
            case "GetBucketReplication": {
                S3ResolveEndpointInterceptor.setContextParams(params, (GetBucketReplicationRequest)request);
                break;
            }
            case "GetBucketRequestPayment": {
                S3ResolveEndpointInterceptor.setContextParams(params, (GetBucketRequestPaymentRequest)request);
                break;
            }
            case "GetBucketTagging": {
                S3ResolveEndpointInterceptor.setContextParams(params, (GetBucketTaggingRequest)request);
                break;
            }
            case "GetBucketVersioning": {
                S3ResolveEndpointInterceptor.setContextParams(params, (GetBucketVersioningRequest)request);
                break;
            }
            case "GetBucketWebsite": {
                S3ResolveEndpointInterceptor.setContextParams(params, (GetBucketWebsiteRequest)request);
                break;
            }
            case "GetObject": {
                S3ResolveEndpointInterceptor.setContextParams(params, (GetObjectRequest)request);
                break;
            }
            case "GetObjectAcl": {
                S3ResolveEndpointInterceptor.setContextParams(params, (GetObjectAclRequest)request);
                break;
            }
            case "GetObjectAttributes": {
                S3ResolveEndpointInterceptor.setContextParams(params, (GetObjectAttributesRequest)request);
                break;
            }
            case "GetObjectLegalHold": {
                S3ResolveEndpointInterceptor.setContextParams(params, (GetObjectLegalHoldRequest)request);
                break;
            }
            case "GetObjectLockConfiguration": {
                S3ResolveEndpointInterceptor.setContextParams(params, (GetObjectLockConfigurationRequest)request);
                break;
            }
            case "GetObjectRetention": {
                S3ResolveEndpointInterceptor.setContextParams(params, (GetObjectRetentionRequest)request);
                break;
            }
            case "GetObjectTagging": {
                S3ResolveEndpointInterceptor.setContextParams(params, (GetObjectTaggingRequest)request);
                break;
            }
            case "GetObjectTorrent": {
                S3ResolveEndpointInterceptor.setContextParams(params, (GetObjectTorrentRequest)request);
                break;
            }
            case "GetPublicAccessBlock": {
                S3ResolveEndpointInterceptor.setContextParams(params, (GetPublicAccessBlockRequest)request);
                break;
            }
            case "HeadBucket": {
                S3ResolveEndpointInterceptor.setContextParams(params, (HeadBucketRequest)request);
                break;
            }
            case "HeadObject": {
                S3ResolveEndpointInterceptor.setContextParams(params, (HeadObjectRequest)request);
                break;
            }
            case "ListBucketAnalyticsConfigurations": {
                S3ResolveEndpointInterceptor.setContextParams(params, (ListBucketAnalyticsConfigurationsRequest)request);
                break;
            }
            case "ListBucketIntelligentTieringConfigurations": {
                S3ResolveEndpointInterceptor.setContextParams(params, (ListBucketIntelligentTieringConfigurationsRequest)request);
                break;
            }
            case "ListBucketInventoryConfigurations": {
                S3ResolveEndpointInterceptor.setContextParams(params, (ListBucketInventoryConfigurationsRequest)request);
                break;
            }
            case "ListBucketMetricsConfigurations": {
                S3ResolveEndpointInterceptor.setContextParams(params, (ListBucketMetricsConfigurationsRequest)request);
                break;
            }
            case "ListMultipartUploads": {
                S3ResolveEndpointInterceptor.setContextParams(params, (ListMultipartUploadsRequest)request);
                break;
            }
            case "ListObjectVersions": {
                S3ResolveEndpointInterceptor.setContextParams(params, (ListObjectVersionsRequest)request);
                break;
            }
            case "ListObjects": {
                S3ResolveEndpointInterceptor.setContextParams(params, (ListObjectsRequest)request);
                break;
            }
            case "ListObjectsV2": {
                S3ResolveEndpointInterceptor.setContextParams(params, (ListObjectsV2Request)request);
                break;
            }
            case "ListParts": {
                S3ResolveEndpointInterceptor.setContextParams(params, (ListPartsRequest)request);
                break;
            }
            case "PutBucketAccelerateConfiguration": {
                S3ResolveEndpointInterceptor.setContextParams(params, (PutBucketAccelerateConfigurationRequest)request);
                break;
            }
            case "PutBucketAcl": {
                S3ResolveEndpointInterceptor.setContextParams(params, (PutBucketAclRequest)request);
                break;
            }
            case "PutBucketAnalyticsConfiguration": {
                S3ResolveEndpointInterceptor.setContextParams(params, (PutBucketAnalyticsConfigurationRequest)request);
                break;
            }
            case "PutBucketCors": {
                S3ResolveEndpointInterceptor.setContextParams(params, (PutBucketCorsRequest)request);
                break;
            }
            case "PutBucketEncryption": {
                S3ResolveEndpointInterceptor.setContextParams(params, (PutBucketEncryptionRequest)request);
                break;
            }
            case "PutBucketIntelligentTieringConfiguration": {
                S3ResolveEndpointInterceptor.setContextParams(params, (PutBucketIntelligentTieringConfigurationRequest)request);
                break;
            }
            case "PutBucketInventoryConfiguration": {
                S3ResolveEndpointInterceptor.setContextParams(params, (PutBucketInventoryConfigurationRequest)request);
                break;
            }
            case "PutBucketLifecycleConfiguration": {
                S3ResolveEndpointInterceptor.setContextParams(params, (PutBucketLifecycleConfigurationRequest)request);
                break;
            }
            case "PutBucketLogging": {
                S3ResolveEndpointInterceptor.setContextParams(params, (PutBucketLoggingRequest)request);
                break;
            }
            case "PutBucketMetricsConfiguration": {
                S3ResolveEndpointInterceptor.setContextParams(params, (PutBucketMetricsConfigurationRequest)request);
                break;
            }
            case "PutBucketNotificationConfiguration": {
                S3ResolveEndpointInterceptor.setContextParams(params, (PutBucketNotificationConfigurationRequest)request);
                break;
            }
            case "PutBucketOwnershipControls": {
                S3ResolveEndpointInterceptor.setContextParams(params, (PutBucketOwnershipControlsRequest)request);
                break;
            }
            case "PutBucketPolicy": {
                S3ResolveEndpointInterceptor.setContextParams(params, (PutBucketPolicyRequest)request);
                break;
            }
            case "PutBucketReplication": {
                S3ResolveEndpointInterceptor.setContextParams(params, (PutBucketReplicationRequest)request);
                break;
            }
            case "PutBucketRequestPayment": {
                S3ResolveEndpointInterceptor.setContextParams(params, (PutBucketRequestPaymentRequest)request);
                break;
            }
            case "PutBucketTagging": {
                S3ResolveEndpointInterceptor.setContextParams(params, (PutBucketTaggingRequest)request);
                break;
            }
            case "PutBucketVersioning": {
                S3ResolveEndpointInterceptor.setContextParams(params, (PutBucketVersioningRequest)request);
                break;
            }
            case "PutBucketWebsite": {
                S3ResolveEndpointInterceptor.setContextParams(params, (PutBucketWebsiteRequest)request);
                break;
            }
            case "PutObject": {
                S3ResolveEndpointInterceptor.setContextParams(params, (PutObjectRequest)request);
                break;
            }
            case "PutObjectAcl": {
                S3ResolveEndpointInterceptor.setContextParams(params, (PutObjectAclRequest)request);
                break;
            }
            case "PutObjectLegalHold": {
                S3ResolveEndpointInterceptor.setContextParams(params, (PutObjectLegalHoldRequest)request);
                break;
            }
            case "PutObjectLockConfiguration": {
                S3ResolveEndpointInterceptor.setContextParams(params, (PutObjectLockConfigurationRequest)request);
                break;
            }
            case "PutObjectRetention": {
                S3ResolveEndpointInterceptor.setContextParams(params, (PutObjectRetentionRequest)request);
                break;
            }
            case "PutObjectTagging": {
                S3ResolveEndpointInterceptor.setContextParams(params, (PutObjectTaggingRequest)request);
                break;
            }
            case "PutPublicAccessBlock": {
                S3ResolveEndpointInterceptor.setContextParams(params, (PutPublicAccessBlockRequest)request);
                break;
            }
            case "RestoreObject": {
                S3ResolveEndpointInterceptor.setContextParams(params, (RestoreObjectRequest)request);
                break;
            }
            case "SelectObjectContent": {
                S3ResolveEndpointInterceptor.setContextParams(params, (SelectObjectContentRequest)request);
                break;
            }
            case "UploadPart": {
                S3ResolveEndpointInterceptor.setContextParams(params, (UploadPartRequest)request);
                break;
            }
            case "UploadPartCopy": {
                S3ResolveEndpointInterceptor.setContextParams(params, (UploadPartCopyRequest)request);
                break;
            }
        }
    }

    private static void setContextParams(S3EndpointParams.Builder params, AbortMultipartUploadRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, CompleteMultipartUploadRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, CopyObjectRequest request) {
        params.bucket(request.destinationBucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, CreateBucketRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, CreateMultipartUploadRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, DeleteBucketRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, DeleteBucketAnalyticsConfigurationRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, DeleteBucketCorsRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, DeleteBucketEncryptionRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, DeleteBucketIntelligentTieringConfigurationRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, DeleteBucketInventoryConfigurationRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, DeleteBucketLifecycleRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, DeleteBucketMetricsConfigurationRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, DeleteBucketOwnershipControlsRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, DeleteBucketPolicyRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, DeleteBucketReplicationRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, DeleteBucketTaggingRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, DeleteBucketWebsiteRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, DeleteObjectRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, DeleteObjectTaggingRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, DeleteObjectsRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, DeletePublicAccessBlockRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, GetBucketAccelerateConfigurationRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, GetBucketAclRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, GetBucketAnalyticsConfigurationRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, GetBucketCorsRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, GetBucketEncryptionRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, GetBucketIntelligentTieringConfigurationRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, GetBucketInventoryConfigurationRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, GetBucketLifecycleConfigurationRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, GetBucketLocationRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, GetBucketLoggingRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, GetBucketMetricsConfigurationRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, GetBucketNotificationConfigurationRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, GetBucketOwnershipControlsRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, GetBucketPolicyRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, GetBucketPolicyStatusRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, GetBucketReplicationRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, GetBucketRequestPaymentRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, GetBucketTaggingRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, GetBucketVersioningRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, GetBucketWebsiteRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, GetObjectRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, GetObjectAclRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, GetObjectAttributesRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, GetObjectLegalHoldRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, GetObjectLockConfigurationRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, GetObjectRetentionRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, GetObjectTaggingRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, GetObjectTorrentRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, GetPublicAccessBlockRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, HeadBucketRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, HeadObjectRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, ListBucketAnalyticsConfigurationsRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, ListBucketIntelligentTieringConfigurationsRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, ListBucketInventoryConfigurationsRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, ListBucketMetricsConfigurationsRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, ListMultipartUploadsRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, ListObjectVersionsRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, ListObjectsRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, ListObjectsV2Request request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, ListPartsRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, PutBucketAccelerateConfigurationRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, PutBucketAclRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, PutBucketAnalyticsConfigurationRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, PutBucketCorsRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, PutBucketEncryptionRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, PutBucketIntelligentTieringConfigurationRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, PutBucketInventoryConfigurationRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, PutBucketLifecycleConfigurationRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, PutBucketLoggingRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, PutBucketMetricsConfigurationRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, PutBucketNotificationConfigurationRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, PutBucketOwnershipControlsRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, PutBucketPolicyRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, PutBucketReplicationRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, PutBucketRequestPaymentRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, PutBucketTaggingRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, PutBucketVersioningRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, PutBucketWebsiteRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, PutObjectRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, PutObjectAclRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, PutObjectLegalHoldRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, PutObjectLockConfigurationRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, PutObjectRetentionRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, PutObjectTaggingRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, PutPublicAccessBlockRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, RestoreObjectRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, SelectObjectContentRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, UploadPartRequest request) {
        params.bucket(request.bucket());
    }

    private static void setContextParams(S3EndpointParams.Builder params, UploadPartCopyRequest request) {
        params.bucket(request.destinationBucket());
    }

    private static void setStaticContextParams(S3EndpointParams.Builder params, String operationName) {
        switch (operationName) {
            case "CreateBucket": {
                S3ResolveEndpointInterceptor.createBucketStaticContextParams(params);
                break;
            }
            case "WriteGetObjectResponse": {
                S3ResolveEndpointInterceptor.writeGetObjectResponseStaticContextParams(params);
                break;
            }
        }
    }

    private static void createBucketStaticContextParams(S3EndpointParams.Builder params) {
        params.disableAccessPoints(true);
    }

    private static void writeGetObjectResponseStaticContextParams(S3EndpointParams.Builder params) {
        params.useObjectLambdaEndpoint(true);
    }

    private <T extends Identity> SelectedAuthScheme<T> authSchemeWithEndpointSignerProperties(List<EndpointAuthScheme> endpointAuthSchemes, SelectedAuthScheme<T> selectedAuthScheme) {
        Iterator<EndpointAuthScheme> iterator = endpointAuthSchemes.iterator();
        if (iterator.hasNext()) {
            EndpointAuthScheme endpointAuthScheme = iterator.next();
            AuthSchemeOption.Builder option = (AuthSchemeOption.Builder)selectedAuthScheme.authSchemeOption().toBuilder();
            if (endpointAuthScheme instanceof SigV4AuthScheme) {
                SigV4AuthScheme v4AuthScheme = (SigV4AuthScheme)endpointAuthScheme;
                if (v4AuthScheme.isDisableDoubleEncodingSet()) {
                    option.putSignerProperty(AwsV4HttpSigner.DOUBLE_URL_ENCODE, (Object)(!v4AuthScheme.disableDoubleEncoding() ? 1 : 0));
                }
                if (v4AuthScheme.signingRegion() != null) {
                    option.putSignerProperty(AwsV4HttpSigner.REGION_NAME, (Object)v4AuthScheme.signingRegion());
                }
                if (v4AuthScheme.signingName() != null) {
                    option.putSignerProperty(AwsV4HttpSigner.SERVICE_SIGNING_NAME, (Object)v4AuthScheme.signingName());
                }
                return new SelectedAuthScheme(selectedAuthScheme.identity(), selectedAuthScheme.signer(), (AuthSchemeOption)option.build());
            }
            if (endpointAuthScheme instanceof SigV4aAuthScheme) {
                SigV4aAuthScheme v4aAuthScheme = (SigV4aAuthScheme)endpointAuthScheme;
                if (v4aAuthScheme.isDisableDoubleEncodingSet()) {
                    option.putSignerProperty(AwsV4aHttpSigner.DOUBLE_URL_ENCODE, (Object)(!v4aAuthScheme.disableDoubleEncoding() ? 1 : 0));
                }
                if (v4aAuthScheme.signingRegionSet() != null) {
                    RegionSet regionSet = RegionSet.create((Collection)v4aAuthScheme.signingRegionSet());
                    option.putSignerProperty(AwsV4aHttpSigner.REGION_SET, (Object)regionSet);
                }
                if (v4aAuthScheme.signingName() != null) {
                    option.putSignerProperty(AwsV4aHttpSigner.SERVICE_SIGNING_NAME, (Object)v4aAuthScheme.signingName());
                }
                return new SelectedAuthScheme(selectedAuthScheme.identity(), selectedAuthScheme.signer(), (AuthSchemeOption)option.build());
            }
            throw new IllegalArgumentException("Endpoint auth scheme '" + endpointAuthScheme.name() + "' cannot be mapped to the SDK auth scheme. Was it declared in the service's model?");
        }
        return selectedAuthScheme;
    }

    private static void setClientContextParams(S3EndpointParams.Builder params, ExecutionAttributes executionAttributes) {
        AttributeMap clientContextParams = (AttributeMap)executionAttributes.getAttribute(SdkInternalExecutionAttribute.CLIENT_CONTEXT_PARAMS);
        Optional.ofNullable(clientContextParams.get(S3ClientContextParams.ACCELERATE)).ifPresent(params::accelerate);
        Optional.ofNullable(clientContextParams.get(S3ClientContextParams.DISABLE_MULTI_REGION_ACCESS_POINTS)).ifPresent(params::disableMultiRegionAccessPoints);
        Optional.ofNullable(clientContextParams.get(S3ClientContextParams.FORCE_PATH_STYLE)).ifPresent(params::forcePathStyle);
        Optional.ofNullable(clientContextParams.get(S3ClientContextParams.USE_ARN_REGION)).ifPresent(params::useArnRegion);
    }

    private static Optional<String> hostPrefix(String operationName, SdkRequest request) {
        switch (operationName) {
            case "WriteGetObjectResponse": {
                HostnameValidator.validateHostnameCompliant((String)request.getValueForField("RequestRoute", String.class).orElse(null), (String)"RequestRoute", (String)"writeGetObjectResponseRequest");
                return Optional.of(String.format("%s.", request.getValueForField("RequestRoute", String.class).get()));
            }
        }
        return Optional.empty();
    }

    private Supplier<Signer> signerProvider(EndpointAuthScheme authScheme) {
        switch (authScheme.name()) {
            case "sigv4": {
                return AwsS3V4Signer::create;
            }
            case "sigv4a": {
                return SignerLoader::getS3SigV4aSigner;
            }
        }
        throw SdkClientException.create((String)("Don't know how to create signer for auth scheme: " + authScheme.name()));
    }
}

