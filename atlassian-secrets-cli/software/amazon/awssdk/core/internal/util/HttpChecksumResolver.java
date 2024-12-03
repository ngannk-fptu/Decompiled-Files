/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.util;

import java.util.ArrayList;
import java.util.List;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.checksums.Algorithm;
import software.amazon.awssdk.core.checksums.ChecksumSpecs;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.core.interceptor.trait.HttpChecksum;
import software.amazon.awssdk.core.internal.util.HttpChecksumUtils;

@SdkInternalApi
public final class HttpChecksumResolver {
    private HttpChecksumResolver() {
    }

    public static ChecksumSpecs getResolvedChecksumSpecs(ExecutionAttributes executionAttributes) {
        ChecksumSpecs checksumSpecs = executionAttributes.getAttribute(SdkExecutionAttribute.RESOLVED_CHECKSUM_SPECS);
        if (checksumSpecs != null) {
            return checksumSpecs;
        }
        return HttpChecksumResolver.resolveChecksumSpecs(executionAttributes);
    }

    public static ChecksumSpecs resolveChecksumSpecs(ExecutionAttributes executionAttributes) {
        HttpChecksum httpChecksumTraitInOperation = executionAttributes.getAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM);
        if (httpChecksumTraitInOperation == null) {
            return null;
        }
        boolean hasRequestValidation = HttpChecksumResolver.hasRequestValidationMode(httpChecksumTraitInOperation);
        String requestAlgorithm = httpChecksumTraitInOperation.requestAlgorithm();
        String checksumHeaderName = requestAlgorithm != null ? HttpChecksumUtils.httpChecksumHeader(requestAlgorithm) : null;
        List<Algorithm> responseValidationAlgorithms = HttpChecksumResolver.getResponseValidationAlgorithms(httpChecksumTraitInOperation);
        return ChecksumSpecs.builder().algorithm(Algorithm.fromValue(httpChecksumTraitInOperation.requestAlgorithm())).headerName(checksumHeaderName).responseValidationAlgorithms(responseValidationAlgorithms).isValidationEnabled(hasRequestValidation).isRequestChecksumRequired(httpChecksumTraitInOperation.isRequestChecksumRequired()).isRequestStreaming(httpChecksumTraitInOperation.isRequestStreaming()).build();
    }

    private static boolean hasRequestValidationMode(HttpChecksum httpChecksum) {
        return httpChecksum.requestValidationMode() != null;
    }

    private static List<Algorithm> getResponseValidationAlgorithms(HttpChecksum httpChecksumTraitInOperation) {
        List<String> responseAlgorithms = httpChecksumTraitInOperation.responseAlgorithms();
        if (responseAlgorithms != null && !responseAlgorithms.isEmpty()) {
            ArrayList<Algorithm> responseValidationAlgorithms = new ArrayList<Algorithm>(responseAlgorithms.size());
            for (String algorithmName : responseAlgorithms) {
                responseValidationAlgorithms.add(Algorithm.fromValue(algorithmName));
            }
            return responseValidationAlgorithms;
        }
        return null;
    }
}

