/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth.profile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfilesConfigFile;
import com.amazonaws.auth.profile.internal.AwsProfileNameLoader;
import java.util.concurrent.Semaphore;

public class ProfileCredentialsProvider
implements AWSCredentialsProvider {
    private static final long DEFAULT_REFRESH_INTERVAL_NANOS = 300000000000L;
    private static final long DEFAULT_FORCE_RELOAD_INTERVAL_NANOS = 600000000000L;
    private volatile ProfilesConfigFile profilesConfigFile;
    private volatile long lastRefreshed;
    private final String profileName;
    private final Semaphore refreshSemaphore = new Semaphore(1);
    private long refreshIntervalNanos = 300000000000L;
    private long refreshForceIntervalNanos = 600000000000L;

    public ProfileCredentialsProvider() {
        this(null);
    }

    public ProfileCredentialsProvider(String profileName) {
        this((ProfilesConfigFile)null, profileName);
    }

    public ProfileCredentialsProvider(String profilesConfigFilePath, String profileName) {
        this(new ProfilesConfigFile(profilesConfigFilePath), profileName);
    }

    public ProfileCredentialsProvider(ProfilesConfigFile profilesConfigFile, String profileName) {
        this.profilesConfigFile = profilesConfigFile;
        if (this.profilesConfigFile != null) {
            this.lastRefreshed = System.nanoTime();
        }
        this.profileName = profileName == null ? AwsProfileNameLoader.INSTANCE.loadProfileName() : profileName;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AWSCredentials getCredentials() {
        long now;
        long age;
        if (this.profilesConfigFile == null) {
            ProfileCredentialsProvider profileCredentialsProvider = this;
            synchronized (profileCredentialsProvider) {
                if (this.profilesConfigFile == null) {
                    this.profilesConfigFile = new ProfilesConfigFile();
                    this.lastRefreshed = System.nanoTime();
                }
            }
        }
        if ((age = (now = System.nanoTime()) - this.lastRefreshed) > this.refreshForceIntervalNanos) {
            this.refresh();
        } else if (age > this.refreshIntervalNanos && this.refreshSemaphore.tryAcquire()) {
            try {
                this.refresh();
            }
            finally {
                this.refreshSemaphore.release();
            }
        }
        return this.profilesConfigFile.getCredentials(this.profileName);
    }

    @Override
    public void refresh() {
        if (this.profilesConfigFile != null) {
            this.profilesConfigFile.refresh();
            this.lastRefreshed = System.nanoTime();
        }
    }

    public long getRefreshIntervalNanos() {
        return this.refreshIntervalNanos;
    }

    public void setRefreshIntervalNanos(long refreshIntervalNanos) {
        this.refreshIntervalNanos = refreshIntervalNanos;
    }

    public long getRefreshForceIntervalNanos() {
        return this.refreshForceIntervalNanos;
    }

    public void setRefreshForceIntervalNanos(long refreshForceIntervalNanos) {
        this.refreshForceIntervalNanos = refreshForceIntervalNanos;
    }
}

