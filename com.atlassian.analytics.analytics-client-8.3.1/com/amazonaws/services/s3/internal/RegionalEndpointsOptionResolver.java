/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.SdkClientException;
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
public final class RegionalEndpointsOptionResolver {
    private static final Log log = LogFactory.getLog(RegionalEndpointsOptionResolver.class);
    private static final String ENV_VAR = "AWS_S3_US_EAST_1_REGIONAL_ENDPOINT";
    private static final String PROFILE_PROPERTY = "s3_us_east_1_regional_endpoint";
    private final AwsProfileFileLocationProvider configFileLocationProvider;
    private volatile String profileName;
    private volatile ProfilesConfigFile configFile;
    private volatile boolean profileLoadAttempted;

    public RegionalEndpointsOptionResolver() {
        this.configFileLocationProvider = AwsProfileFileLocationProvider.DEFAULT_CONFIG_LOCATION_PROVIDER;
    }

    public RegionalEndpointsOptionResolver(AwsProfileFileLocationProvider configFileLocationProvider) {
        this.configFileLocationProvider = configFileLocationProvider;
    }

    public boolean useRegionalMode() {
        Option option = this.envVarOption();
        if (option == null) {
            option = this.profileOption();
        }
        return option == Option.REGIONAL;
    }

    private Option envVarOption() {
        String val = System.getenv(ENV_VAR);
        return this.resolveOption(val, String.format("Unexpected value set for %s environment variable: '%s'", ENV_VAR, val));
    }

    private synchronized Option profileOption() {
        String profileName = this.getProfileName();
        BasicProfile profile = this.getProfile(profileName);
        if (profile == null) {
            return null;
        }
        String val = profile.getPropertyValue(PROFILE_PROPERTY);
        return this.resolveOption(val, String.format("Unexpected option for '%s' property in profile '%s': %s", PROFILE_PROPERTY, profileName, val));
    }

    private Option resolveOption(String value, String errMsg) {
        if (value == null) {
            return null;
        }
        if ("legacy".equalsIgnoreCase(value)) {
            return Option.LEGACY;
        }
        if ("regional".equalsIgnoreCase(value)) {
            return Option.REGIONAL;
        }
        throw new SdkClientException(errMsg);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String getProfileName() {
        if (this.profileName == null) {
            RegionalEndpointsOptionResolver regionalEndpointsOptionResolver = this;
            synchronized (regionalEndpointsOptionResolver) {
                if (this.profileName == null) {
                    this.profileName = AwsProfileNameLoader.INSTANCE.loadProfileName();
                }
            }
        }
        return this.profileName;
    }

    private synchronized BasicProfile getProfile(String profileName) {
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
            RegionalEndpointsOptionResolver regionalEndpointsOptionResolver = this;
            synchronized (regionalEndpointsOptionResolver) {
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

    private static enum Option {
        LEGACY,
        REGIONAL;

    }
}

