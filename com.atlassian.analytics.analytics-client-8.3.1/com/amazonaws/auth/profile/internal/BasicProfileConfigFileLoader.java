/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.auth.profile.internal;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.annotation.SdkTestInternalApi;
import com.amazonaws.auth.profile.ProfilesConfigFile;
import com.amazonaws.auth.profile.internal.AwsProfileNameLoader;
import com.amazonaws.auth.profile.internal.BasicProfile;
import com.amazonaws.profile.path.AwsProfileFileLocationProvider;
import com.amazonaws.util.ValidationUtils;
import java.io.File;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SdkInternalApi
public final class BasicProfileConfigFileLoader {
    public static final BasicProfileConfigFileLoader INSTANCE = new BasicProfileConfigFileLoader();
    private static final Log log = LogFactory.getLog(BasicProfileConfigFileLoader.class);
    private final AwsProfileFileLocationProvider configFileLocationProvider;
    private volatile String profileName;
    private volatile ProfilesConfigFile configFile;
    private volatile boolean profileLoadAttempted;

    private BasicProfileConfigFileLoader() {
        this.configFileLocationProvider = AwsProfileFileLocationProvider.DEFAULT_CONFIG_LOCATION_PROVIDER;
    }

    @SdkTestInternalApi
    public BasicProfileConfigFileLoader(AwsProfileFileLocationProvider configFileLocationProvider) {
        this.configFileLocationProvider = ValidationUtils.assertNotNull(configFileLocationProvider, "configFileLocationProvider");
    }

    public BasicProfile getProfile() {
        ProfilesConfigFile profilesConfigFile = this.getProfilesConfigFile();
        if (profilesConfigFile != null) {
            return profilesConfigFile.getBasicProfile(this.getProfileName());
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ProfilesConfigFile getProfilesConfigFile() {
        if (!this.profileLoadAttempted) {
            BasicProfileConfigFileLoader basicProfileConfigFileLoader = this;
            synchronized (basicProfileConfigFileLoader) {
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String getProfileName() {
        if (this.profileName == null) {
            BasicProfileConfigFileLoader basicProfileConfigFileLoader = this;
            synchronized (basicProfileConfigFileLoader) {
                if (this.profileName == null) {
                    this.profileName = AwsProfileNameLoader.INSTANCE.loadProfileName();
                }
            }
        }
        return this.profileName;
    }
}

