/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkResponse
 *  software.amazon.awssdk.core.checksums.ChecksumSpecs
 *  software.amazon.awssdk.core.checksums.ChecksumValidation
 *  software.amazon.awssdk.core.interceptor.Context$AfterTransmission
 *  software.amazon.awssdk.core.interceptor.Context$ModifyResponse
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.ExecutionInterceptor
 *  software.amazon.awssdk.core.interceptor.SdkExecutionAttribute
 *  software.amazon.awssdk.core.internal.util.HttpChecksumResolver
 *  software.amazon.awssdk.core.internal.util.HttpChecksumUtils
 *  software.amazon.awssdk.http.SdkHttpResponse
 *  software.amazon.awssdk.utils.Pair
 */
package software.amazon.awssdk.services.s3.internal.handlers;

import java.util.Optional;
import java.util.regex.Pattern;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.checksums.ChecksumSpecs;
import software.amazon.awssdk.core.checksums.ChecksumValidation;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.core.internal.util.HttpChecksumResolver;
import software.amazon.awssdk.core.internal.util.HttpChecksumUtils;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.utils.Pair;

@SdkInternalApi
public class GetObjectInterceptor
implements ExecutionInterceptor {
    public static final Pattern MULTIPART_CHECKSUM_PATTERN = Pattern.compile(".*-\\d+$");

    public void afterTransmission(Context.AfterTransmission context, ExecutionAttributes executionAttributes) {
        Pair algorithmChecksumValuePair;
        if (!(context.request() instanceof GetObjectRequest)) {
            return;
        }
        ChecksumSpecs resolvedChecksumSpecs = HttpChecksumResolver.getResolvedChecksumSpecs((ExecutionAttributes)executionAttributes);
        if (HttpChecksumUtils.isHttpChecksumValidationEnabled((ChecksumSpecs)resolvedChecksumSpecs) && (algorithmChecksumValuePair = HttpChecksumUtils.getAlgorithmChecksumValuePair((SdkHttpResponse)context.httpResponse(), (ChecksumSpecs)resolvedChecksumSpecs)) != null && algorithmChecksumValuePair.right() != null && MULTIPART_CHECKSUM_PATTERN.matcher((CharSequence)algorithmChecksumValuePair.right()).matches()) {
            executionAttributes.putAttribute(SdkExecutionAttribute.HTTP_RESPONSE_CHECKSUM_VALIDATION, (Object)ChecksumValidation.FORCE_SKIP);
        }
    }

    public SdkResponse modifyResponse(Context.ModifyResponse context, ExecutionAttributes executionAttributes) {
        SdkResponse response = context.response();
        if (!(response instanceof GetObjectResponse)) {
            return response;
        }
        return this.fixContentRange(response, context.httpResponse());
    }

    private SdkResponse fixContentRange(SdkResponse sdkResponse, SdkHttpResponse httpResponse) {
        GetObjectResponse getObjectResponse = (GetObjectResponse)sdkResponse;
        if (getObjectResponse.contentRange() != null) {
            return getObjectResponse;
        }
        Optional xAmzContentRange = httpResponse.firstMatchingHeader("x-amz-content-range");
        if (!xAmzContentRange.isPresent()) {
            return getObjectResponse;
        }
        return (SdkResponse)getObjectResponse.copy(r -> r.contentRange((String)xAmzContentRange.get()));
    }
}

