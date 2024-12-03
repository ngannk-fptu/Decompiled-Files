/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.auth.signer.AwsSignerExecutionAttribute
 *  software.amazon.awssdk.core.ClientType
 *  software.amazon.awssdk.core.SdkRequest
 *  software.amazon.awssdk.core.checksums.ChecksumSpecs
 *  software.amazon.awssdk.core.checksums.SdkChecksum
 *  software.amazon.awssdk.core.exception.RetryableException
 *  software.amazon.awssdk.core.interceptor.ExecutionAttribute
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.SdkExecutionAttribute
 *  software.amazon.awssdk.http.SdkHttpHeaders
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.http.SdkHttpResponse
 *  software.amazon.awssdk.utils.BinaryUtils
 *  software.amazon.awssdk.utils.StringUtils
 *  software.amazon.awssdk.utils.internal.Base16Lower
 */
package software.amazon.awssdk.services.s3.checksums;

import java.util.Arrays;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.signer.AwsSignerExecutionAttribute;
import software.amazon.awssdk.core.ClientType;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.checksums.ChecksumSpecs;
import software.amazon.awssdk.core.checksums.SdkChecksum;
import software.amazon.awssdk.core.exception.RetryableException;
import software.amazon.awssdk.core.interceptor.ExecutionAttribute;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.http.SdkHttpHeaders;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.internal.Base16Lower;

@SdkInternalApi
public final class ChecksumsEnabledValidator {
    public static final ExecutionAttribute<SdkChecksum> CHECKSUM = new ExecutionAttribute("checksum");

    private ChecksumsEnabledValidator() {
    }

    public static boolean getObjectChecksumEnabledPerRequest(SdkRequest request, ExecutionAttributes executionAttributes) {
        return request instanceof GetObjectRequest && ChecksumsEnabledValidator.checksumEnabledPerConfig(executionAttributes);
    }

    public static boolean getObjectChecksumEnabledPerResponse(SdkRequest request, SdkHttpHeaders responseHeaders) {
        return request instanceof GetObjectRequest && ChecksumsEnabledValidator.checksumEnabledPerResponse(responseHeaders);
    }

    public static boolean shouldRecordChecksum(SdkRequest sdkRequest, ClientType expectedClientType, ExecutionAttributes executionAttributes, SdkHttpRequest httpRequest) {
        if (!(sdkRequest instanceof PutObjectRequest)) {
            return false;
        }
        ClientType actualClientType = (ClientType)executionAttributes.getAttribute(SdkExecutionAttribute.CLIENT_TYPE);
        if (expectedClientType != actualClientType) {
            return false;
        }
        if (ChecksumsEnabledValidator.hasServerSideEncryptionHeader((SdkHttpHeaders)httpRequest)) {
            return false;
        }
        if (ChecksumsEnabledValidator.isHttpCheckSumValidationEnabled(executionAttributes)) {
            return false;
        }
        return ChecksumsEnabledValidator.checksumEnabledPerConfig(executionAttributes);
    }

    private static boolean isHttpCheckSumValidationEnabled(ExecutionAttributes executionAttributes) {
        Optional resolvedChecksum = executionAttributes.getOptionalAttribute(SdkExecutionAttribute.RESOLVED_CHECKSUM_SPECS);
        if (resolvedChecksum.isPresent()) {
            ChecksumSpecs checksumSpecs = (ChecksumSpecs)resolvedChecksum.get();
            return checksumSpecs.algorithm() != null;
        }
        return false;
    }

    public static boolean responseChecksumIsValid(SdkHttpResponse httpResponse) {
        return !ChecksumsEnabledValidator.hasServerSideEncryptionHeader((SdkHttpHeaders)httpResponse);
    }

    private static boolean hasServerSideEncryptionHeader(SdkHttpHeaders httpRequest) {
        if (httpRequest.firstMatchingHeader("x-amz-server-side-encryption-customer-algorithm").isPresent()) {
            return true;
        }
        return httpRequest.firstMatchingHeader("x-amz-server-side-encryption").filter(h -> h.contains(ServerSideEncryption.AWS_KMS.toString())).isPresent();
    }

    public static void validatePutObjectChecksum(PutObjectResponse response, ExecutionAttributes executionAttributes) {
        byte[] ssHash;
        String contentMd5;
        byte[] digest;
        SdkChecksum checksum = (SdkChecksum)executionAttributes.getAttribute(CHECKSUM);
        if (response.eTag() != null && !Arrays.equals(digest = BinaryUtils.fromBase64((String)(contentMd5 = BinaryUtils.toBase64((byte[])checksum.getChecksumBytes()))), ssHash = Base16Lower.decode((String)StringUtils.replace((String)response.eTag(), (String)"\"", (String)"")))) {
            throw RetryableException.create((String)String.format("Data read has a different checksum than expected. Was 0x%s, but expected 0x%s. This commonly means that the data was corrupted between the client and service. Note: Despite this error, the upload still completed and was persisted in S3.", BinaryUtils.toHex((byte[])digest), BinaryUtils.toHex((byte[])ssHash)));
        }
    }

    private static boolean checksumEnabledPerResponse(SdkHttpHeaders responseHeaders) {
        return responseHeaders.firstMatchingHeader("x-amz-transfer-encoding").filter(b -> b.equals("append-md5")).isPresent();
    }

    private static boolean checksumEnabledPerConfig(ExecutionAttributes executionAttributes) {
        S3Configuration serviceConfiguration = (S3Configuration)executionAttributes.getAttribute(AwsSignerExecutionAttribute.SERVICE_CONFIG);
        return serviceConfiguration == null || serviceConfiguration.checksumValidationEnabled();
    }
}

