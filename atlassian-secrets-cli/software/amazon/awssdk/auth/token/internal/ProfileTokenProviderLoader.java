/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.token.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.token.credentials.ChildProfileTokenProviderFactory;
import software.amazon.awssdk.auth.token.credentials.SdkTokenProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.internal.util.ClassLoaderHelper;
import software.amazon.awssdk.profiles.Profile;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.internal.ProfileSection;
import software.amazon.awssdk.utils.Lazy;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class ProfileTokenProviderLoader {
    private static final String SSO_OIDC_TOKEN_PROVIDER_FACTORY = "software.amazon.awssdk.services.ssooidc.SsoOidcProfileTokenProviderFactory";
    private final Supplier<ProfileFile> profileFileSupplier;
    private final String profileName;
    private volatile ProfileFile currentProfileFile;
    private volatile SdkTokenProvider currentTokenProvider;
    private final Lazy<ChildProfileTokenProviderFactory> factory;

    public ProfileTokenProviderLoader(Supplier<ProfileFile> profileFile, String profileName) {
        this.profileFileSupplier = Validate.paramNotNull(profileFile, "profileFile");
        this.profileName = Validate.paramNotNull(profileName, "profileName");
        this.factory = new Lazy<ChildProfileTokenProviderFactory>(this::ssoTokenProviderFactory);
    }

    public Optional<SdkTokenProvider> tokenProvider() {
        return Optional.ofNullable(this.ssoProfileCredentialsProvider());
    }

    private SdkTokenProvider ssoProfileCredentialsProvider() {
        return () -> this.ssoProfileCredentialsProvider(this.profileFileSupplier, this.profileName).resolveToken();
    }

    private SdkTokenProvider ssoProfileCredentialsProvider(ProfileFile profileFile, Profile profile2) {
        String profileSsoSectionName = this.profileSsoSectionName(profile2);
        Profile ssoProfile = this.ssoProfile(profileFile, profileSsoSectionName);
        this.validateRequiredProperties(ssoProfile, "sso_region", "sso_start_url");
        return this.factory.getValue().create(profileFile, profile2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private SdkTokenProvider ssoProfileCredentialsProvider(Supplier<ProfileFile> profileFile, String profileName) {
        ProfileFile profileFileInstance = profileFile.get();
        if (!Objects.equals(profileFileInstance, this.currentProfileFile)) {
            ProfileTokenProviderLoader profileTokenProviderLoader = this;
            synchronized (profileTokenProviderLoader) {
                if (!Objects.equals(profileFileInstance, this.currentProfileFile)) {
                    Profile profileInstance = this.resolveProfile(profileFileInstance, profileName);
                    this.currentProfileFile = profileFileInstance;
                    this.currentTokenProvider = this.ssoProfileCredentialsProvider(profileFileInstance, profileInstance);
                }
            }
        }
        return this.currentTokenProvider;
    }

    private Profile resolveProfile(ProfileFile profileFile, String profileName) {
        return profileFile.profile(profileName).orElseThrow(() -> {
            String errorMessage = String.format("Profile file contained no information for profile '%s': %s", profileName, profileFile);
            return SdkClientException.builder().message(errorMessage).build();
        });
    }

    private String profileSsoSectionName(Profile profile2) {
        return (String)Optional.ofNullable(profile2).flatMap(p -> p.property(ProfileSection.SSO_SESSION.getPropertyKeyName())).orElseThrow(() -> new IllegalArgumentException("Profile " + this.profileName + " does not have sso_session property"));
    }

    private Profile ssoProfile(ProfileFile profileFile, String profileSsoSectionName) {
        return profileFile.getSection(ProfileSection.SSO_SESSION.getSectionTitle(), profileSsoSectionName).orElseThrow(() -> new IllegalArgumentException("Sso-session section not found with sso-session title " + profileSsoSectionName));
    }

    private void validateRequiredProperties(Profile ssoProfile, String ... requiredProperties) {
        Arrays.stream(requiredProperties).forEach(p -> Validate.isTrue(ssoProfile.properties().containsKey(p), "Property '%s' was not configured for profile '%s'.", p, this.profileName));
    }

    private ChildProfileTokenProviderFactory ssoTokenProviderFactory() {
        try {
            Class<?> ssoOidcTokenProviderFactory = ClassLoaderHelper.loadClass(SSO_OIDC_TOKEN_PROVIDER_FACTORY, this.getClass());
            return (ChildProfileTokenProviderFactory)ssoOidcTokenProviderFactory.getConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (ClassNotFoundException e) {
            throw new IllegalStateException("To use SSO OIDC related properties in the '" + this.profileName + "' profile, the 'ssooidc' service module must be on the class path.", e);
        }
        catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to create the '%s" + this.profileName + "' token provider factory.", e);
        }
    }
}

