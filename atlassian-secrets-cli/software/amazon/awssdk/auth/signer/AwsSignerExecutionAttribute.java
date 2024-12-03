/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.signer;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.CredentialUtils;
import software.amazon.awssdk.core.SelectedAuthScheme;
import software.amazon.awssdk.core.interceptor.ExecutionAttribute;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4FamilyHttpSigner;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4HttpSigner;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4aHttpSigner;
import software.amazon.awssdk.http.auth.aws.signer.RegionSet;
import software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption;
import software.amazon.awssdk.http.auth.spi.signer.AsyncSignRequest;
import software.amazon.awssdk.http.auth.spi.signer.AsyncSignedRequest;
import software.amazon.awssdk.http.auth.spi.signer.HttpSigner;
import software.amazon.awssdk.http.auth.spi.signer.SignRequest;
import software.amazon.awssdk.http.auth.spi.signer.SignedRequest;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.Identity;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.RegionScope;
import software.amazon.awssdk.utils.CompletableFutureUtils;

@Deprecated
@SdkProtectedApi
public final class AwsSignerExecutionAttribute
extends SdkExecutionAttribute {
    @Deprecated
    public static final ExecutionAttribute<AwsCredentials> AWS_CREDENTIALS = ExecutionAttribute.derivedBuilder("AwsCredentials", AwsCredentials.class, SdkInternalExecutionAttribute.SELECTED_AUTH_SCHEME).readMapping(AwsSignerExecutionAttribute::awsCredentialsReadMapping).writeMapping(AwsSignerExecutionAttribute::awsCredentialsWriteMapping).build();
    @Deprecated
    public static final ExecutionAttribute<Region> SIGNING_REGION = ExecutionAttribute.derivedBuilder("SigningRegion", Region.class, SdkInternalExecutionAttribute.SELECTED_AUTH_SCHEME).readMapping(AwsSignerExecutionAttribute::signingRegionReadMapping).writeMapping(AwsSignerExecutionAttribute::signingRegionWriteMapping).build();
    @Deprecated
    public static final ExecutionAttribute<RegionScope> SIGNING_REGION_SCOPE = ExecutionAttribute.derivedBuilder("SigningRegionScope", RegionScope.class, SdkInternalExecutionAttribute.SELECTED_AUTH_SCHEME).readMapping(AwsSignerExecutionAttribute::signingRegionScopeReadMapping).writeMapping(AwsSignerExecutionAttribute::signingRegionScopeWriteMapping).build();
    @Deprecated
    public static final ExecutionAttribute<String> SERVICE_SIGNING_NAME = ExecutionAttribute.derivedBuilder("ServiceSigningName", String.class, SdkInternalExecutionAttribute.SELECTED_AUTH_SCHEME).readMapping(AwsSignerExecutionAttribute::serviceSigningNameReadMapping).writeMapping(AwsSignerExecutionAttribute::serviceSigningNameWriteMapping).build();
    @Deprecated
    public static final ExecutionAttribute<Boolean> SIGNER_DOUBLE_URL_ENCODE = ExecutionAttribute.derivedBuilder("DoubleUrlEncode", Boolean.class, SdkInternalExecutionAttribute.SELECTED_AUTH_SCHEME).readMapping(AwsSignerExecutionAttribute::signerDoubleUrlEncodeReadMapping).writeMapping(AwsSignerExecutionAttribute::signerDoubleUrlEncodeWriteMapping).build();
    @Deprecated
    public static final ExecutionAttribute<Boolean> SIGNER_NORMALIZE_PATH = ExecutionAttribute.derivedBuilder("NormalizePath", Boolean.class, SdkInternalExecutionAttribute.SELECTED_AUTH_SCHEME).readMapping(AwsSignerExecutionAttribute::signerNormalizePathReadMapping).writeMapping(AwsSignerExecutionAttribute::signerNormalizePathWriteMapping).build();
    @Deprecated
    public static final ExecutionAttribute<Clock> SIGNING_CLOCK = ExecutionAttribute.derivedBuilder("Clock", Clock.class, SdkInternalExecutionAttribute.SELECTED_AUTH_SCHEME).readMapping(AwsSignerExecutionAttribute::signingClockReadMapping).writeMapping(AwsSignerExecutionAttribute::signingClockWriteMapping).build();
    @Deprecated
    public static final ExecutionAttribute<Instant> PRESIGNER_EXPIRATION = ExecutionAttribute.derivedBuilder("PresignerExpiration", Instant.class, SdkInternalExecutionAttribute.SELECTED_AUTH_SCHEME).readMapping(AwsSignerExecutionAttribute::presignerExpirationReadMapping).writeMapping(AwsSignerExecutionAttribute::presignerExpirationWriteMapping).build();
    private static Clock presignerExpirationClock = Clock.systemUTC();

    private AwsSignerExecutionAttribute() {
    }

    private static AwsCredentials awsCredentialsReadMapping(SelectedAuthScheme<?> authScheme) {
        if (authScheme == null) {
            return null;
        }
        Identity identity = (Identity)CompletableFutureUtils.joinLikeSync(authScheme.identity());
        if (!(identity instanceof AwsCredentialsIdentity)) {
            return null;
        }
        return CredentialUtils.toCredentials((AwsCredentialsIdentity)identity);
    }

    private static <T extends Identity> SelectedAuthScheme<?> awsCredentialsWriteMapping(SelectedAuthScheme<T> authScheme, AwsCredentials awsCredentials) {
        if (authScheme == null) {
            return new SelectedAuthScheme<AwsCredentialsIdentity>(CompletableFuture.completedFuture(awsCredentials), AwsV4HttpSigner.create(), (AuthSchemeOption)AuthSchemeOption.builder().schemeId("aws.auth#sigv4").build());
        }
        return new SelectedAuthScheme<AwsCredentials>(CompletableFuture.completedFuture(awsCredentials), authScheme.signer(), authScheme.authSchemeOption());
    }

    private static Region signingRegionReadMapping(SelectedAuthScheme<?> authScheme) {
        if (authScheme == null) {
            return null;
        }
        String regionName = authScheme.authSchemeOption().signerProperty(AwsV4HttpSigner.REGION_NAME);
        if (regionName == null) {
            return null;
        }
        return Region.of(regionName);
    }

    private static <T extends Identity> SelectedAuthScheme<?> signingRegionWriteMapping(SelectedAuthScheme<T> authScheme, Region region) {
        String regionString;
        String string = regionString = region == null ? null : region.id();
        if (authScheme == null) {
            return new SelectedAuthScheme<UnsetIdentity>(CompletableFuture.completedFuture(new UnsetIdentity()), new UnsetHttpSigner(), (AuthSchemeOption)AuthSchemeOption.builder().schemeId("unset").putSignerProperty(AwsV4HttpSigner.REGION_NAME, regionString).build());
        }
        return new SelectedAuthScheme<T>(authScheme.identity(), authScheme.signer(), (AuthSchemeOption)authScheme.authSchemeOption().copy(o -> o.putSignerProperty(AwsV4HttpSigner.REGION_NAME, regionString)));
    }

    private static RegionScope signingRegionScopeReadMapping(SelectedAuthScheme<?> authScheme) {
        if (authScheme == null) {
            return null;
        }
        RegionSet regionSet = authScheme.authSchemeOption().signerProperty(AwsV4aHttpSigner.REGION_SET);
        if (regionSet == null || regionSet.asString().isEmpty()) {
            return null;
        }
        return RegionScope.create(regionSet.asString());
    }

    private static <T extends Identity> SelectedAuthScheme<?> signingRegionScopeWriteMapping(SelectedAuthScheme<T> authScheme, RegionScope regionScope) {
        RegionSet regionSet;
        RegionSet regionSet2 = regionSet = regionScope != null ? RegionSet.create(regionScope.id()) : null;
        if (authScheme == null) {
            return new SelectedAuthScheme<UnsetIdentity>(CompletableFuture.completedFuture(new UnsetIdentity()), new UnsetHttpSigner(), (AuthSchemeOption)AuthSchemeOption.builder().schemeId("unset").putSignerProperty(AwsV4aHttpSigner.REGION_SET, regionSet).build());
        }
        return new SelectedAuthScheme<T>(authScheme.identity(), authScheme.signer(), (AuthSchemeOption)authScheme.authSchemeOption().copy(o -> o.putSignerProperty(AwsV4aHttpSigner.REGION_SET, regionSet)));
    }

    private static String serviceSigningNameReadMapping(SelectedAuthScheme<?> authScheme) {
        if (authScheme == null) {
            return null;
        }
        return authScheme.authSchemeOption().signerProperty(AwsV4FamilyHttpSigner.SERVICE_SIGNING_NAME);
    }

    private static <T extends Identity> SelectedAuthScheme<?> serviceSigningNameWriteMapping(SelectedAuthScheme<T> authScheme, String signingName) {
        if (authScheme == null) {
            return new SelectedAuthScheme<UnsetIdentity>(CompletableFuture.completedFuture(new UnsetIdentity()), new UnsetHttpSigner(), (AuthSchemeOption)AuthSchemeOption.builder().schemeId("unset").putSignerProperty(AwsV4FamilyHttpSigner.SERVICE_SIGNING_NAME, signingName).build());
        }
        return new SelectedAuthScheme<T>(authScheme.identity(), authScheme.signer(), (AuthSchemeOption)authScheme.authSchemeOption().copy(o -> o.putSignerProperty(AwsV4FamilyHttpSigner.SERVICE_SIGNING_NAME, signingName)));
    }

    private static Boolean signerDoubleUrlEncodeReadMapping(SelectedAuthScheme<?> authScheme) {
        if (authScheme == null) {
            return null;
        }
        AuthSchemeOption authOption = authScheme.authSchemeOption();
        return authOption.signerProperty(AwsV4FamilyHttpSigner.DOUBLE_URL_ENCODE);
    }

    private static <T extends Identity> SelectedAuthScheme<?> signerDoubleUrlEncodeWriteMapping(SelectedAuthScheme<T> authScheme, Boolean doubleUrlEncode) {
        if (authScheme == null) {
            return new SelectedAuthScheme<UnsetIdentity>(CompletableFuture.completedFuture(new UnsetIdentity()), new UnsetHttpSigner(), (AuthSchemeOption)AuthSchemeOption.builder().schemeId("unset").putSignerProperty(AwsV4FamilyHttpSigner.DOUBLE_URL_ENCODE, doubleUrlEncode).build());
        }
        return new SelectedAuthScheme<T>(authScheme.identity(), authScheme.signer(), (AuthSchemeOption)authScheme.authSchemeOption().copy(o -> o.putSignerProperty(AwsV4FamilyHttpSigner.DOUBLE_URL_ENCODE, doubleUrlEncode)));
    }

    private static Boolean signerNormalizePathReadMapping(SelectedAuthScheme<?> authScheme) {
        if (authScheme == null) {
            return null;
        }
        AuthSchemeOption authOption = authScheme.authSchemeOption();
        return authOption.signerProperty(AwsV4FamilyHttpSigner.NORMALIZE_PATH);
    }

    private static <T extends Identity> SelectedAuthScheme<?> signerNormalizePathWriteMapping(SelectedAuthScheme<T> authScheme, Boolean normalizePath) {
        if (authScheme == null) {
            return new SelectedAuthScheme<UnsetIdentity>(CompletableFuture.completedFuture(new UnsetIdentity()), new UnsetHttpSigner(), (AuthSchemeOption)AuthSchemeOption.builder().schemeId("unset").putSignerProperty(AwsV4FamilyHttpSigner.NORMALIZE_PATH, normalizePath).build());
        }
        return new SelectedAuthScheme<T>(authScheme.identity(), authScheme.signer(), (AuthSchemeOption)authScheme.authSchemeOption().copy(o -> o.putSignerProperty(AwsV4FamilyHttpSigner.NORMALIZE_PATH, normalizePath)));
    }

    private static Clock signingClockReadMapping(SelectedAuthScheme<?> authScheme) {
        if (authScheme == null) {
            return null;
        }
        return authScheme.authSchemeOption().signerProperty(HttpSigner.SIGNING_CLOCK);
    }

    private static <T extends Identity> SelectedAuthScheme<?> signingClockWriteMapping(SelectedAuthScheme<T> authScheme, Clock clock) {
        if (authScheme == null) {
            return new SelectedAuthScheme<UnsetIdentity>(CompletableFuture.completedFuture(new UnsetIdentity()), new UnsetHttpSigner(), (AuthSchemeOption)AuthSchemeOption.builder().schemeId("unset").putSignerProperty(HttpSigner.SIGNING_CLOCK, clock).build());
        }
        return new SelectedAuthScheme<T>(authScheme.identity(), authScheme.signer(), (AuthSchemeOption)authScheme.authSchemeOption().copy(o -> o.putSignerProperty(HttpSigner.SIGNING_CLOCK, clock)));
    }

    @SdkTestInternalApi
    static void presignerExpirationClock(Clock clock) {
        presignerExpirationClock = clock;
    }

    private static Instant presignerExpirationReadMapping(SelectedAuthScheme<?> authScheme) {
        if (authScheme == null) {
            return null;
        }
        Duration expirationDuration = authScheme.authSchemeOption().signerProperty(AwsV4FamilyHttpSigner.EXPIRATION_DURATION);
        if (expirationDuration == null) {
            return null;
        }
        return presignerExpirationClock.instant().plus(expirationDuration);
    }

    private static <T extends Identity> SelectedAuthScheme<?> presignerExpirationWriteMapping(SelectedAuthScheme<T> authScheme, Instant expiration) {
        Duration expirationDuration;
        Duration duration = expirationDuration = expiration == null ? null : Duration.between(presignerExpirationClock.instant(), expiration);
        if (authScheme == null) {
            return new SelectedAuthScheme<UnsetIdentity>(CompletableFuture.completedFuture(new UnsetIdentity()), new UnsetHttpSigner(), (AuthSchemeOption)AuthSchemeOption.builder().schemeId("unset").putSignerProperty(AwsV4FamilyHttpSigner.EXPIRATION_DURATION, expirationDuration).build());
        }
        return new SelectedAuthScheme<T>(authScheme.identity(), authScheme.signer(), (AuthSchemeOption)authScheme.authSchemeOption().copy(o -> o.putSignerProperty(AwsV4FamilyHttpSigner.EXPIRATION_DURATION, expirationDuration)));
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

