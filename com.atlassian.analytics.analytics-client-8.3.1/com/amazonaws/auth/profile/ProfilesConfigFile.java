/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth.profile;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.internal.AllProfiles;
import com.amazonaws.auth.profile.internal.BasicProfile;
import com.amazonaws.auth.profile.internal.BasicProfileConfigLoader;
import com.amazonaws.auth.profile.internal.Profile;
import com.amazonaws.auth.profile.internal.ProfileAssumeRoleCredentialsProvider;
import com.amazonaws.auth.profile.internal.ProfileProcessCredentialsProvider;
import com.amazonaws.auth.profile.internal.ProfileStaticCredentialsProvider;
import com.amazonaws.auth.profile.internal.securitytoken.ProfileCredentialsService;
import com.amazonaws.auth.profile.internal.securitytoken.STSProfileCredentialsServiceLoader;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.profile.path.AwsProfileFileLocationProvider;
import com.amazonaws.util.ValidationUtils;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProfilesConfigFile {
    @Deprecated
    public static final String AWS_PROFILE_ENVIRONMENT_VARIABLE = "AWS_PROFILE";
    @Deprecated
    public static final String AWS_PROFILE_SYSTEM_PROPERTY = "aws.profile";
    @Deprecated
    public static final String DEFAULT_PROFILE_NAME = "default";
    private final File profileFile;
    private final ProfileCredentialsService profileCredentialsService;
    private final ConcurrentHashMap<String, AWSCredentialsProvider> credentialProviderCache = new ConcurrentHashMap();
    private volatile AllProfiles allProfiles;
    private volatile long profileFileLastModified;

    public ProfilesConfigFile() throws SdkClientException {
        this(ProfilesConfigFile.getCredentialProfilesFile());
    }

    public ProfilesConfigFile(String filePath) {
        this(new File(ProfilesConfigFile.validateFilePath(filePath)));
    }

    public ProfilesConfigFile(String filePath, ProfileCredentialsService credentialsService) throws SdkClientException {
        this(new File(ProfilesConfigFile.validateFilePath(filePath)), credentialsService);
    }

    private static String validateFilePath(String filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("Unable to load AWS profiles: specified file path is null.");
        }
        return filePath;
    }

    public ProfilesConfigFile(File file) throws SdkClientException {
        this(file, (ProfileCredentialsService)STSProfileCredentialsServiceLoader.getInstance());
    }

    public ProfilesConfigFile(File file, ProfileCredentialsService credentialsService) throws SdkClientException {
        this.profileFile = ValidationUtils.assertNotNull(file, "profile file");
        this.profileCredentialsService = credentialsService;
        this.profileFileLastModified = file.lastModified();
        this.allProfiles = ProfilesConfigFile.loadProfiles(this.profileFile);
    }

    public AWSCredentials getCredentials(String profileName) {
        AWSCredentialsProvider provider = this.credentialProviderCache.get(profileName);
        if (provider != null) {
            return provider.getCredentials();
        }
        BasicProfile profile = this.allProfiles.getProfile(profileName);
        if (profile == null) {
            throw new IllegalArgumentException("No AWS profile named '" + profileName + "'");
        }
        AWSCredentialsProvider newProvider = this.fromProfile(profile);
        this.credentialProviderCache.put(profileName, newProvider);
        return newProvider.getCredentials();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void refresh() {
        if (this.profileFile.lastModified() > this.profileFileLastModified) {
            ProfilesConfigFile profilesConfigFile = this;
            synchronized (profilesConfigFile) {
                if (this.profileFile.lastModified() > this.profileFileLastModified) {
                    this.allProfiles = ProfilesConfigFile.loadProfiles(this.profileFile);
                    this.profileFileLastModified = this.profileFile.lastModified();
                }
            }
        }
        this.credentialProviderCache.clear();
    }

    public BasicProfile getBasicProfile(String profile) {
        return this.allProfiles.getProfile(profile);
    }

    public Map<String, BasicProfile> getAllBasicProfiles() {
        return this.allProfiles.getProfiles();
    }

    @Deprecated
    public Map<String, Profile> getAllProfiles() {
        HashMap<String, Profile> legacyProfiles = new HashMap<String, Profile>();
        for (Map.Entry<String, BasicProfile> entry : this.getAllBasicProfiles().entrySet()) {
            String profileName = entry.getKey();
            legacyProfiles.put(profileName, new Profile(profileName, entry.getValue().getProperties(), new StaticCredentialsProvider(this.getCredentials(profileName))));
        }
        return legacyProfiles;
    }

    private static File getCredentialProfilesFile() {
        return AwsProfileFileLocationProvider.DEFAULT_CREDENTIALS_LOCATION_PROVIDER.getLocation();
    }

    private static AllProfiles loadProfiles(File file) {
        return BasicProfileConfigLoader.INSTANCE.loadProfiles(file);
    }

    private AWSCredentialsProvider fromProfile(BasicProfile profile) {
        if (profile.isRoleBasedProfile()) {
            return new ProfileAssumeRoleCredentialsProvider(this.profileCredentialsService, this.allProfiles, profile);
        }
        if (profile.isProcessBasedProfile()) {
            return new ProfileProcessCredentialsProvider(profile);
        }
        return new ProfileStaticCredentialsProvider(profile);
    }
}

