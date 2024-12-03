/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.interceptor;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.checksums.DefaultChecksumAlgorithm;
import software.amazon.awssdk.checksums.spi.ChecksumAlgorithm;
import software.amazon.awssdk.core.ClientType;
import software.amazon.awssdk.core.SelectedAuthScheme;
import software.amazon.awssdk.core.ServiceConfiguration;
import software.amazon.awssdk.core.checksums.Algorithm;
import software.amazon.awssdk.core.checksums.ChecksumSpecs;
import software.amazon.awssdk.core.checksums.ChecksumValidation;
import software.amazon.awssdk.core.interceptor.ExecutionAttribute;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.http.auth.aws.internal.signer.util.ChecksumUtil;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4FamilyHttpSigner;
import software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption;
import software.amazon.awssdk.http.auth.spi.signer.AsyncSignRequest;
import software.amazon.awssdk.http.auth.spi.signer.AsyncSignedRequest;
import software.amazon.awssdk.http.auth.spi.signer.HttpSigner;
import software.amazon.awssdk.http.auth.spi.signer.SignRequest;
import software.amazon.awssdk.http.auth.spi.signer.SignedRequest;
import software.amazon.awssdk.identity.spi.Identity;
import software.amazon.awssdk.metrics.MetricCollector;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.utils.CompletableFutureUtils;
import software.amazon.awssdk.utils.ImmutableMap;

@SdkPublicApi
public class SdkExecutionAttribute {
    public static final ExecutionAttribute<ServiceConfiguration> SERVICE_CONFIG = new ExecutionAttribute("ServiceConfig");
    public static final ExecutionAttribute<String> SERVICE_NAME = new ExecutionAttribute("ServiceName");
    public static final ExecutionAttribute<Integer> TIME_OFFSET = new ExecutionAttribute("TimeOffset");
    public static final ExecutionAttribute<ClientType> CLIENT_TYPE = new ExecutionAttribute("ClientType");
    public static final ExecutionAttribute<String> OPERATION_NAME = new ExecutionAttribute("OperationName");
    public static final ExecutionAttribute<MetricCollector> API_CALL_METRIC_COLLECTOR = new ExecutionAttribute("ApiCallMetricCollector");
    public static final ExecutionAttribute<MetricCollector> API_CALL_ATTEMPT_METRIC_COLLECTOR = new ExecutionAttribute("ApiCallAttemptMetricCollector");
    public static final ExecutionAttribute<Boolean> ENDPOINT_OVERRIDDEN = new ExecutionAttribute("EndpointOverridden");
    public static final ExecutionAttribute<URI> CLIENT_ENDPOINT = new ExecutionAttribute("EndpointOverride");
    public static final ExecutionAttribute<Boolean> SIGNER_OVERRIDDEN = new ExecutionAttribute("SignerOverridden");
    @Deprecated
    public static final ExecutionAttribute<ProfileFile> PROFILE_FILE = new ExecutionAttribute("ProfileFile");
    public static final ExecutionAttribute<Supplier<ProfileFile>> PROFILE_FILE_SUPPLIER = new ExecutionAttribute("ProfileFileSupplier");
    public static final ExecutionAttribute<String> PROFILE_NAME = new ExecutionAttribute("ProfileName");
    public static final ExecutionAttribute<ChecksumSpecs> RESOLVED_CHECKSUM_SPECS = ExecutionAttribute.mappedBuilder("ResolvedChecksumSpecs", () -> SdkInternalExecutionAttribute.INTERNAL_RESOLVED_CHECKSUM_SPECS, () -> SdkInternalExecutionAttribute.SELECTED_AUTH_SCHEME).readMapping(SdkExecutionAttribute::signerChecksumReadMapping).writeMapping(SdkExecutionAttribute::signerChecksumWriteMapping).build();
    public static final ExecutionAttribute<Algorithm> HTTP_CHECKSUM_VALIDATION_ALGORITHM = new ExecutionAttribute("HttpChecksumValidationAlgorithm");
    public static final ExecutionAttribute<ChecksumValidation> HTTP_RESPONSE_CHECKSUM_VALIDATION = new ExecutionAttribute("HttpResponseChecksumValidation");
    private static final ImmutableMap<ChecksumAlgorithm, Algorithm> ALGORITHM_MAP = ImmutableMap.of(DefaultChecksumAlgorithm.SHA256, Algorithm.SHA256, DefaultChecksumAlgorithm.SHA1, Algorithm.SHA1, DefaultChecksumAlgorithm.CRC32, Algorithm.CRC32, DefaultChecksumAlgorithm.CRC32C, Algorithm.CRC32C);
    private static final ImmutableMap<Algorithm, ChecksumAlgorithm> CHECKSUM_ALGORITHM_MAP = ImmutableMap.of(Algorithm.SHA256, DefaultChecksumAlgorithm.SHA256, Algorithm.SHA1, DefaultChecksumAlgorithm.SHA1, Algorithm.CRC32, DefaultChecksumAlgorithm.CRC32, Algorithm.CRC32C, DefaultChecksumAlgorithm.CRC32C);

    protected SdkExecutionAttribute() {
    }

    private static <T extends Identity> ChecksumSpecs signerChecksumReadMapping(ChecksumSpecs checksumSpecs, SelectedAuthScheme<T> authScheme) {
        if (checksumSpecs == null || authScheme == null) {
            return checksumSpecs;
        }
        ChecksumAlgorithm checksumAlgorithm = authScheme.authSchemeOption().signerProperty(AwsV4FamilyHttpSigner.CHECKSUM_ALGORITHM);
        return ChecksumSpecs.builder().algorithm(checksumAlgorithm != null ? ALGORITHM_MAP.get(checksumAlgorithm) : null).isRequestStreaming(checksumSpecs.isRequestStreaming()).isRequestChecksumRequired(checksumSpecs.isRequestChecksumRequired()).isValidationEnabled(checksumSpecs.isValidationEnabled()).headerName(checksumAlgorithm != null ? ChecksumUtil.checksumHeaderName(checksumAlgorithm) : null).responseValidationAlgorithms(checksumSpecs.responseValidationAlgorithms()).build();
    }

    private static <T extends Identity> SelectedAuthScheme<?> signerChecksumWriteMapping(SelectedAuthScheme<T> authScheme, ChecksumSpecs checksumSpecs) {
        ChecksumAlgorithm checksumAlgorithm;
        ChecksumAlgorithm checksumAlgorithm2 = checksumAlgorithm = checksumSpecs == null ? null : CHECKSUM_ALGORITHM_MAP.get((Object)checksumSpecs.algorithm());
        if (authScheme == null) {
            return new SelectedAuthScheme<UnsetIdentity>(CompletableFuture.completedFuture(new UnsetIdentity()), new UnsetHttpSigner(), (AuthSchemeOption)AuthSchemeOption.builder().schemeId("unset").putSignerProperty(AwsV4FamilyHttpSigner.CHECKSUM_ALGORITHM, checksumAlgorithm).build());
        }
        return new SelectedAuthScheme<T>(authScheme.identity(), authScheme.signer(), (AuthSchemeOption)authScheme.authSchemeOption().copy(o -> o.putSignerProperty(AwsV4FamilyHttpSigner.CHECKSUM_ALGORITHM, checksumAlgorithm)));
    }

    private static class UnsetHttpSigner
    implements HttpSigner<UnsetIdentity> {
        private UnsetHttpSigner() {
        }

        @Override
        public SignedRequest sign(SignRequest<? extends UnsetIdentity> request) {
            throw new IllegalStateException("A signer was not configured.");
        }

        @Override
        public CompletableFuture<AsyncSignedRequest> signAsync(AsyncSignRequest<? extends UnsetIdentity> request) {
            return CompletableFutureUtils.failedFuture(new IllegalStateException("A signer was not configured."));
        }
    }

    private static class UnsetIdentity
    implements Identity {
        private UnsetIdentity() {
        }
    }
}

