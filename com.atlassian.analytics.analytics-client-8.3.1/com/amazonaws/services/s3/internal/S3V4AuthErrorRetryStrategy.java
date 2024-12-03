/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.Request;
import com.amazonaws.SdkClientException;
import com.amazonaws.annotation.Immutable;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.internal.SdkPredicate;
import com.amazonaws.regions.Regions;
import com.amazonaws.retry.internal.AuthErrorRetryStrategy;
import com.amazonaws.retry.internal.AuthRetryParameters;
import com.amazonaws.services.s3.internal.AWSS3V4Signer;
import com.amazonaws.services.s3.internal.BucketNameUtils;
import com.amazonaws.services.s3.internal.IsSigV4RetryablePredicate;
import com.amazonaws.services.s3.internal.S3RequestEndpointResolver;
import com.amazonaws.util.StringUtils;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Immutable
public class S3V4AuthErrorRetryStrategy
implements AuthErrorRetryStrategy {
    private static Log log = LogFactory.getLog(S3V4AuthErrorRetryStrategy.class);
    private static final String V4_REGION_WARNING = "please use region-specific endpoint to access buckets located in regions that require V4 signing.";
    private final S3RequestEndpointResolver endpointResolver;
    private final SdkPredicate<AmazonServiceException> sigV4RetryPredicate;

    public S3V4AuthErrorRetryStrategy(S3RequestEndpointResolver endpointResolver) {
        this(endpointResolver, new IsSigV4RetryablePredicate());
    }

    S3V4AuthErrorRetryStrategy(S3RequestEndpointResolver endpointResolver, SdkPredicate<AmazonServiceException> isSigV4Retryable) {
        this.endpointResolver = endpointResolver;
        this.sigV4RetryPredicate = isSigV4Retryable;
    }

    @Override
    public AuthRetryParameters shouldRetryWithAuthParam(Request<?> request, HttpResponse response, AmazonServiceException ase) {
        if (!this.sigV4RetryPredicate.test(ase)) {
            return null;
        }
        if (S3V4AuthErrorRetryStrategy.hasServingRegionHeader(response)) {
            return this.redirectToRegionInHeader(request, response);
        }
        if (this.canUseVirtualAddressing()) {
            return this.redirectToS3External();
        }
        throw new SdkClientException(V4_REGION_WARNING, ase);
    }

    private boolean canUseVirtualAddressing() {
        return BucketNameUtils.isDNSBucketName(this.endpointResolver.getBucketName());
    }

    private AuthRetryParameters redirectToRegionInHeader(Request<?> request, HttpResponse response) {
        String region = S3V4AuthErrorRetryStrategy.getServingRegionHeader(response);
        AWSS3V4Signer v4Signer = this.buildSigV4Signer(region);
        this.endpointResolver.resolveRequestEndpoint(request, region);
        return this.buildRetryParams(v4Signer, request.getEndpoint());
    }

    private AuthRetryParameters redirectToS3External() {
        AWSS3V4Signer v4Signer = this.buildSigV4Signer(Regions.US_EAST_1.getName());
        try {
            URI bucketEndpoint = new URI(String.format("https://%s.s3-external-1.amazonaws.com", this.endpointResolver.getBucketName()));
            return this.buildRetryParams(v4Signer, bucketEndpoint);
        }
        catch (URISyntaxException e) {
            throw new SdkClientException("Failed to re-send the request to \"s3-external-1.amazonaws.com\". please use region-specific endpoint to access buckets located in regions that require V4 signing.", e);
        }
    }

    private AWSS3V4Signer buildSigV4Signer(String region) {
        AWSS3V4Signer v4Signer = new AWSS3V4Signer();
        v4Signer.setRegionName(region);
        v4Signer.setServiceName("s3");
        return v4Signer;
    }

    private AuthRetryParameters buildRetryParams(AWSS3V4Signer signer, URI endpoint) {
        log.warn((Object)("Attempting to re-send the request to " + endpoint.getHost() + " with AWS V4 authentication. To avoid this warning in the future, " + V4_REGION_WARNING));
        return new AuthRetryParameters(signer, endpoint);
    }

    private static boolean hasServingRegionHeader(HttpResponse response) {
        return !StringUtils.isNullOrEmpty(S3V4AuthErrorRetryStrategy.getServingRegionHeader(response));
    }

    private static String getServingRegionHeader(HttpResponse response) {
        return response.getHeaders().get("x-amz-region");
    }
}

