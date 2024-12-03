/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.core.RequestOverrideConfiguration
 *  software.amazon.awssdk.core.SdkRequest
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.SdkExecutionAttribute
 *  software.amazon.awssdk.core.signer.Signer
 */
package software.amazon.awssdk.awscore.util;

import java.util.Optional;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.awscore.AwsRequest;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.core.RequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.core.signer.Signer;

@SdkProtectedApi
public final class SignerOverrideUtils {
    private SignerOverrideUtils() {
    }

    @Deprecated
    public static SdkRequest overrideSignerIfNotOverridden(SdkRequest request, ExecutionAttributes executionAttributes, Signer signer) {
        return SignerOverrideUtils.overrideSignerIfNotOverridden(request, executionAttributes, () -> signer);
    }

    @Deprecated
    public static SdkRequest overrideSignerIfNotOverridden(SdkRequest request, ExecutionAttributes executionAttributes, Supplier<Signer> signer) {
        if (SignerOverrideUtils.isSignerOverridden(request, executionAttributes)) {
            return request;
        }
        return SignerOverrideUtils.overrideSigner(request, signer.get());
    }

    public static boolean isSignerOverridden(SdkRequest request, ExecutionAttributes executionAttributes) {
        boolean isClientSignerOverridden = Boolean.TRUE.equals(executionAttributes.getAttribute(SdkExecutionAttribute.SIGNER_OVERRIDDEN));
        Optional requestSigner = request.overrideConfiguration().flatMap(RequestOverrideConfiguration::signer);
        return isClientSignerOverridden || requestSigner.isPresent();
    }

    @Deprecated
    private static SdkRequest overrideSigner(SdkRequest request, Signer signer) {
        return request.overrideConfiguration().flatMap(config -> config.signer().map(existingOverrideSigner -> request)).orElseGet(() -> SignerOverrideUtils.createNewRequest(request, signer));
    }

    @Deprecated
    private static SdkRequest createNewRequest(SdkRequest request, Signer signer) {
        AwsRequest awsRequest = (AwsRequest)request;
        AwsRequestOverrideConfiguration modifiedOverride = ((AwsRequestOverrideConfiguration.Builder)awsRequest.overrideConfiguration().map(AwsRequestOverrideConfiguration::toBuilder).orElseGet(AwsRequestOverrideConfiguration::builder).signer(signer)).build();
        return awsRequest.toBuilder().overrideConfiguration(modifiedOverride).build();
    }
}

