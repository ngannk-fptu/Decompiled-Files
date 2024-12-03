/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.annotation.ThreadSafe;
import com.amazonaws.auth.profile.ProfilesConfigFile;
import com.amazonaws.auth.profile.internal.AwsProfileNameLoader;
import com.amazonaws.auth.profile.internal.BasicProfile;
import com.amazonaws.profile.path.AwsProfileFileLocationProvider;
import java.io.File;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@ThreadSafe
@SdkInternalApi
public final class UseArnRegionResolver {
    private static final Log log = LogFactory.getLog(UseArnRegionResolver.class);
    private static final String ENV_VAR = "AWS_S3_USE_ARN_REGION";
    private static final String PROFILE_PROPERTY = "s3_use_arn_region";
    private final AwsProfileFileLocationProvider configFileLocationProvider;
    private final boolean useArnRegion;
    private volatile String profileName;
    private volatile ProfilesConfigFile configFile;
    private volatile boolean profileLoadAttempted;

    public UseArnRegionResolver() {
        this(AwsProfileFileLocationProvider.DEFAULT_CONFIG_LOCATION_PROVIDER);
    }

    public UseArnRegionResolver(AwsProfileFileLocationProvider configFileLocationProvider) {
        this.configFileLocationProvider = configFileLocationProvider;
        this.useArnRegion = this.resolveUseArnRegion();
    }

    public boolean useArnRegion() {
        return this.useArnRegion;
    }

    private boolean resolveUseArnRegion() {
        String useArnRegionString = this.envVar();
        if (useArnRegionString == null) {
            useArnRegionString = this.profile();
        }
        return Boolean.valueOf(useArnRegionString);
    }

    private String envVar() {
        return System.getenv(ENV_VAR);
    }

    private String profile() {
        String loadedProfileName = this.getProfileName();
        BasicProfile profile = this.getProfile(loadedProfileName);
        if (profile == null) {
            return null;
        }
        String val = profile.getPropertyValue(PROFILE_PROPERTY);
        return val;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String getProfileName() {
        if (this.profileName == null) {
            UseArnRegionResolver useArnRegionResolver = this;
            synchronized (useArnRegionResolver) {
                if (this.profileName == null) {
                    this.profileName = AwsProfileNameLoader.INSTANCE.loadProfileName();
                }
            }
        }
        return this.profileName;
    }

    private BasicProfile getProfile(String profileName) {
        ProfilesConfigFile profilesConfigFile = this.getProfilesConfigFile();
        if (profilesConfigFile != null) {
            return profilesConfigFile.getAllBasicProfiles().get(profileName);
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ProfilesConfigFile getProfilesConfigFile() {
        if (!this.profileLoadAttempted) {
            UseArnRegionResolver useArnRegionResolver = this;
            synchronized (useArnRegionResolver) {
                if (!this.profileLoadAttempted) {
                    File location = null;
                    try {
                        location = this.configFileLocationProvider.getLocation();
                        if (location != null) {
                            this.configFile = new ProfilesConfigFile(location);
                        }
                    }
                    catch (Exception e) {
                        if (log.isWarnEnabled()) {
                            log.warn((Object)("Unable to load config file " + location), (Throwable)e);
                        }
                    }
                    finally {
                        this.profileLoadAttempted = true;
                    }
                }
            }
        }
        return this.configFile;
    }
}

