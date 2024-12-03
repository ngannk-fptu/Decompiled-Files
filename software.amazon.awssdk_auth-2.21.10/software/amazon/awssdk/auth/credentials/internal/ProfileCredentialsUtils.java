/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.internal.util.ClassLoaderHelper
 *  software.amazon.awssdk.profiles.Profile
 *  software.amazon.awssdk.profiles.ProfileFile
 *  software.amazon.awssdk.profiles.internal.ProfileSection
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.auth.credentials.internal;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.ChildProfileCredentialsProviderFactory;
import software.amazon.awssdk.auth.credentials.ContainerCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProcessCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProviderFactory;
import software.amazon.awssdk.auth.credentials.ProfileProviderCredentialsContext;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.auth.credentials.internal.CredentialSourceType;
import software.amazon.awssdk.auth.credentials.internal.Ec2MetadataConfigProvider;
import software.amazon.awssdk.auth.credentials.internal.WebIdentityCredentialsUtils;
import software.amazon.awssdk.auth.credentials.internal.WebIdentityTokenCredentialProperties;
import software.amazon.awssdk.core.internal.util.ClassLoaderHelper;
import software.amazon.awssdk.profiles.Profile;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.internal.ProfileSection;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class ProfileCredentialsUtils {
    private static final String STS_PROFILE_CREDENTIALS_PROVIDER_FACTORY = "software.amazon.awssdk.services.sts.internal.StsProfileCredentialsProviderFactory";
    private static final String SSO_PROFILE_CREDENTIALS_PROVIDER_FACTORY = "software.amazon.awssdk.services.sso.auth.SsoProfileCredentialsProviderFactory";
    private final ProfileFile profileFile;
    private final Profile profile;
    private final String name;
    private final Map<String, String> properties;
    private final Function<String, Optional<Profile>> credentialsSourceResolver;

    public ProfileCredentialsUtils(ProfileFile profileFile, Profile profile, Function<String, Optional<Profile>> credentialsSourceResolver) {
        this.profileFile = (ProfileFile)Validate.paramNotNull((Object)profileFile, (String)"profileFile");
        this.profile = (Profile)Validate.paramNotNull((Object)profile, (String)"profile");
        this.name = profile.name();
        this.properties = profile.properties();
        this.credentialsSourceResolver = credentialsSourceResolver;
    }

    public Optional<AwsCredentialsProvider> credentialsProvider() {
        return this.credentialsProvider(new HashSet<String>());
    }

    private Optional<AwsCredentialsProvider> credentialsProvider(Set<String> children) {
        if (this.properties.containsKey("role_arn") && this.properties.containsKey("web_identity_token_file")) {
            return Optional.ofNullable(this.roleAndWebIdentityTokenProfileCredentialsProvider());
        }
        if (this.properties.containsKey("sso_role_name") || this.properties.containsKey("sso_account_id") || this.properties.containsKey("sso_region") || this.properties.containsKey("sso_start_url") || this.properties.containsKey(ProfileSection.SSO_SESSION.getPropertyKeyName())) {
            return Optional.ofNullable(this.ssoProfileCredentialsProvider());
        }
        if (this.properties.containsKey("role_arn")) {
            boolean hasSourceProfile = this.properties.containsKey("source_profile");
            boolean hasCredentialSource = this.properties.containsKey("credential_source");
            Validate.validState((!hasSourceProfile || !hasCredentialSource ? 1 : 0) != 0, (String)"Invalid profile file: profile has both %s and %s.", (Object[])new Object[]{"source_profile", "credential_source"});
            if (hasSourceProfile) {
                return Optional.ofNullable(this.roleAndSourceProfileBasedProfileCredentialsProvider(children));
            }
            if (hasCredentialSource) {
                return Optional.ofNullable(this.roleAndCredentialSourceBasedProfileCredentialsProvider());
            }
        }
        if (this.properties.containsKey("credential_process")) {
            return Optional.ofNullable(this.credentialProcessCredentialsProvider());
        }
        if (this.properties.containsKey("aws_session_token")) {
            return Optional.of(this.sessionProfileCredentialsProvider());
        }
        if (this.properties.containsKey("aws_access_key_id")) {
            return Optional.of(this.basicProfileCredentialsProvider());
        }
        return Optional.empty();
    }

    private AwsCredentialsProvider basicProfileCredentialsProvider() {
        this.requireProperties("aws_access_key_id", "aws_secret_access_key");
        AwsBasicCredentials credentials = AwsBasicCredentials.create(this.properties.get("aws_access_key_id"), this.properties.get("aws_secret_access_key"));
        return StaticCredentialsProvider.create(credentials);
    }

    private AwsCredentialsProvider sessionProfileCredentialsProvider() {
        this.requireProperties("aws_access_key_id", "aws_secret_access_key", "aws_session_token");
        AwsSessionCredentials credentials = AwsSessionCredentials.create(this.properties.get("aws_access_key_id"), this.properties.get("aws_secret_access_key"), this.properties.get("aws_session_token"));
        return StaticCredentialsProvider.create(credentials);
    }

    private AwsCredentialsProvider credentialProcessCredentialsProvider() {
        this.requireProperties("credential_process");
        return ProcessCredentialsProvider.builder().command(this.properties.get("credential_process")).build();
    }

    private AwsCredentialsProvider ssoProfileCredentialsProvider() {
        this.validateRequiredPropertiesForSsoCredentialsProvider();
        return this.ssoCredentialsProviderFactory().create(ProfileProviderCredentialsContext.builder().profile(this.profile).profileFile(this.profileFile).build());
    }

    private void validateRequiredPropertiesForSsoCredentialsProvider() {
        this.requireProperties("sso_account_id", "sso_role_name");
        if (!this.properties.containsKey(ProfileSection.SSO_SESSION.getPropertyKeyName())) {
            this.requireProperties("sso_region", "sso_start_url");
        }
    }

    private AwsCredentialsProvider roleAndWebIdentityTokenProfileCredentialsProvider() {
        this.requireProperties("role_arn", "web_identity_token_file");
        String roleArn = this.properties.get("role_arn");
        String roleSessionName = this.properties.get("role_session_name");
        Path webIdentityTokenFile = Paths.get(this.properties.get("web_identity_token_file"), new String[0]);
        WebIdentityTokenCredentialProperties credentialProperties = WebIdentityTokenCredentialProperties.builder().roleArn(roleArn).roleSessionName(roleSessionName).webIdentityTokenFile(webIdentityTokenFile).build();
        return WebIdentityCredentialsUtils.factory().create(credentialProperties);
    }

    private AwsCredentialsProvider roleAndSourceProfileBasedProfileCredentialsProvider(Set<String> children) {
        this.requireProperties("source_profile");
        Validate.validState((!children.contains(this.name) ? 1 : 0) != 0, (String)"Invalid profile file: Circular relationship detected with profiles %s.", (Object[])new Object[]{children});
        Validate.validState((this.credentialsSourceResolver != null ? 1 : 0) != 0, (String)"The profile '%s' must be configured with a source profile in order to use assumed roles.", (Object[])new Object[]{this.name});
        children.add(this.name);
        AwsCredentialsProvider sourceCredentialsProvider = (AwsCredentialsProvider)this.credentialsSourceResolver.apply(this.properties.get("source_profile")).flatMap(p -> new ProfileCredentialsUtils(this.profileFile, (Profile)p, this.credentialsSourceResolver).credentialsProvider(children)).orElseThrow(this::noSourceCredentialsException);
        return this.stsCredentialsProviderFactory().create(sourceCredentialsProvider, this.profile);
    }

    private AwsCredentialsProvider roleAndCredentialSourceBasedProfileCredentialsProvider() {
        this.requireProperties("credential_source");
        CredentialSourceType credentialSource = CredentialSourceType.parse(this.properties.get("credential_source"));
        AwsCredentialsProvider credentialsProvider = this.credentialSourceCredentialProvider(credentialSource);
        return this.stsCredentialsProviderFactory().create(credentialsProvider, this.profile);
    }

    private AwsCredentialsProvider credentialSourceCredentialProvider(CredentialSourceType credentialSource) {
        switch (credentialSource) {
            case ECS_CONTAINER: {
                return ContainerCredentialsProvider.builder().build();
            }
            case EC2_INSTANCE_METADATA: {
                Ec2MetadataConfigProvider configProvider = Ec2MetadataConfigProvider.builder().profileFile(() -> this.profileFile).profileName(this.name).build();
                return ((InstanceProfileCredentialsProvider.Builder)InstanceProfileCredentialsProvider.builder().endpoint(configProvider.getEndpoint())).build();
            }
            case ENVIRONMENT: {
                return AwsCredentialsProviderChain.builder().addCredentialsProvider(SystemPropertyCredentialsProvider.create()).addCredentialsProvider(EnvironmentVariableCredentialsProvider.create()).build();
            }
        }
        throw this.noSourceCredentialsException();
    }

    private void requireProperties(String ... requiredProperties) {
        Arrays.stream(requiredProperties).forEach(p -> Validate.isTrue((boolean)this.properties.containsKey(p), (String)"Profile property '%s' was not configured for '%s'.", (Object[])new Object[]{p, this.name}));
    }

    private IllegalStateException noSourceCredentialsException() {
        String error = String.format("The source profile of '%s' was configured to be '%s', but that source profile has no credentials configured.", this.name, this.properties.get("source_profile"));
        return new IllegalStateException(error);
    }

    private ChildProfileCredentialsProviderFactory stsCredentialsProviderFactory() {
        try {
            Class stsCredentialsProviderFactory = ClassLoaderHelper.loadClass((String)STS_PROFILE_CREDENTIALS_PROVIDER_FACTORY, (Class[])new Class[]{this.getClass()});
            return (ChildProfileCredentialsProviderFactory)stsCredentialsProviderFactory.getConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (ClassNotFoundException e) {
            throw new IllegalStateException("To use assumed roles in the '" + this.name + "' profile, the 'sts' service module must be on the class path.", e);
        }
        catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to create the '" + this.name + "' profile credentials provider.", e);
        }
    }

    private ProfileCredentialsProviderFactory ssoCredentialsProviderFactory() {
        try {
            Class ssoProfileCredentialsProviderFactory = ClassLoaderHelper.loadClass((String)SSO_PROFILE_CREDENTIALS_PROVIDER_FACTORY, (Class[])new Class[]{this.getClass()});
            return (ProfileCredentialsProviderFactory)ssoProfileCredentialsProviderFactory.getConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (ClassNotFoundException e) {
            throw new IllegalStateException("To use Sso related properties in the '" + this.name + "' profile, the 'sso' service module must be on the class path.", e);
        }
        catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to create the '" + this.name + "' profile credentials provider.", e);
        }
    }
}

