/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.interceptor;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.Predicate;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.ClientType;
import software.amazon.awssdk.core.checksums.Algorithm;
import software.amazon.awssdk.core.checksums.ChecksumSpecs;
import software.amazon.awssdk.core.checksums.ChecksumValidation;
import software.amazon.awssdk.core.checksums.SdkChecksum;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.core.internal.async.ChecksumValidatingPublisher;
import software.amazon.awssdk.core.internal.io.ChecksumValidatingInputStream;
import software.amazon.awssdk.core.internal.util.HttpChecksumResolver;
import software.amazon.awssdk.core.internal.util.HttpChecksumUtils;
import software.amazon.awssdk.utils.Pair;

@SdkInternalApi
public final class HttpChecksumValidationInterceptor
implements ExecutionInterceptor {
    private static final Predicate<ExecutionAttributes> IS_FORCE_SKIPPED_VALIDATION = ex -> ChecksumValidation.FORCE_SKIP.equals(ex.getOptionalAttribute(SdkExecutionAttribute.HTTP_RESPONSE_CHECKSUM_VALIDATION).orElse(null));

    @Override
    public Optional<InputStream> modifyHttpResponseContent(Context.ModifyHttpResponse context, ExecutionAttributes executionAttributes) {
        ChecksumSpecs resolvedChecksumSpecs = HttpChecksumResolver.getResolvedChecksumSpecs(executionAttributes);
        if (resolvedChecksumSpecs != null && this.isFlexibleChecksumValidationForResponse(executionAttributes, resolvedChecksumSpecs, ClientType.SYNC)) {
            Pair<Algorithm, String> algorithmChecksumPair = HttpChecksumUtils.getAlgorithmChecksumValuePair(context.httpResponse(), resolvedChecksumSpecs);
            this.updateContextWithChecksumValidationStatus(executionAttributes, algorithmChecksumPair);
            if (algorithmChecksumPair != null && context.responseBody().isPresent()) {
                return Optional.of(new ChecksumValidatingInputStream(context.responseBody().get(), SdkChecksum.forAlgorithm(algorithmChecksumPair.left()), algorithmChecksumPair.right()));
            }
        }
        return context.responseBody();
    }

    @Override
    public Optional<Publisher<ByteBuffer>> modifyAsyncHttpResponseContent(Context.ModifyHttpResponse context, ExecutionAttributes executionAttributes) {
        ChecksumSpecs resolvedChecksumSpecs = HttpChecksumResolver.getResolvedChecksumSpecs(executionAttributes);
        if (resolvedChecksumSpecs != null && this.isFlexibleChecksumValidationForResponse(executionAttributes, resolvedChecksumSpecs, ClientType.ASYNC)) {
            Pair<Algorithm, String> algorithmChecksumPair = HttpChecksumUtils.getAlgorithmChecksumValuePair(context.httpResponse(), resolvedChecksumSpecs);
            this.updateContextWithChecksumValidationStatus(executionAttributes, algorithmChecksumPair);
            if (algorithmChecksumPair != null && context.responsePublisher().isPresent()) {
                return Optional.of(new ChecksumValidatingPublisher(context.responsePublisher().get(), SdkChecksum.forAlgorithm(algorithmChecksumPair.left()), algorithmChecksumPair.right()));
            }
        }
        return context.responsePublisher();
    }

    private void updateContextWithChecksumValidationStatus(ExecutionAttributes executionAttributes, Pair<Algorithm, String> algorithmChecksumPair) {
        if (algorithmChecksumPair == null || algorithmChecksumPair.left() == null) {
            executionAttributes.putAttribute(SdkExecutionAttribute.HTTP_RESPONSE_CHECKSUM_VALIDATION, ChecksumValidation.CHECKSUM_ALGORITHM_NOT_FOUND);
        } else {
            executionAttributes.putAttribute(SdkExecutionAttribute.HTTP_RESPONSE_CHECKSUM_VALIDATION, ChecksumValidation.VALIDATED);
            executionAttributes.putAttribute(SdkExecutionAttribute.HTTP_CHECKSUM_VALIDATION_ALGORITHM, algorithmChecksumPair.left());
        }
    }

    private boolean isFlexibleChecksumValidationForResponse(ExecutionAttributes executionAttributes, ChecksumSpecs checksumSpecs, ClientType clientType) {
        return HttpChecksumUtils.isHttpChecksumValidationEnabled(checksumSpecs) && executionAttributes.getAttribute(SdkExecutionAttribute.CLIENT_TYPE).equals((Object)clientType) && !IS_FORCE_SKIPPED_VALIDATION.test(executionAttributes);
    }
}

