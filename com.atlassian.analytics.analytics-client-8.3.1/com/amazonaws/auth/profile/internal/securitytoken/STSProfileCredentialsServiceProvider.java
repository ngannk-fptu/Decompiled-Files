/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth.profile.internal.securitytoken;

import com.amazonaws.SdkClientException;
import com.amazonaws.annotation.ThreadSafe;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.internal.securitytoken.ProfileCredentialsService;
import com.amazonaws.auth.profile.internal.securitytoken.RoleInfo;

@ThreadSafe
public class STSProfileCredentialsServiceProvider
implements AWSCredentialsProvider {
    private static final String CLASS_NAME = "com.amazonaws.services.securitytoken.internal.STSProfileCredentialsService";
    private static volatile ProfileCredentialsService STS_CREDENTIALS_SERVICE;
    private final RoleInfo roleInfo;
    private volatile AWSCredentialsProvider profileCredentialsProvider;

    public STSProfileCredentialsServiceProvider(RoleInfo roleInfo) {
        this.roleInfo = roleInfo;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private AWSCredentialsProvider getProfileCredentialsProvider() {
        if (this.profileCredentialsProvider != null) return this.profileCredentialsProvider;
        Class<STSProfileCredentialsServiceProvider> clazz = STSProfileCredentialsServiceProvider.class;
        synchronized (STSProfileCredentialsServiceProvider.class) {
            if (this.profileCredentialsProvider != null) return this.profileCredentialsProvider;
            this.profileCredentialsProvider = STSProfileCredentialsServiceProvider.getProfileCredentialService().getAssumeRoleCredentialsProvider(this.roleInfo);
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return this.profileCredentialsProvider;
        }
    }

    private static synchronized ProfileCredentialsService getProfileCredentialService() {
        if (STS_CREDENTIALS_SERVICE == null) {
            try {
                STS_CREDENTIALS_SERVICE = (ProfileCredentialsService)Class.forName(CLASS_NAME).newInstance();
            }
            catch (ClassNotFoundException ex) {
                throw new SdkClientException("To use assume role profiles the aws-java-sdk-sts module must be on the class path.", ex);
            }
            catch (InstantiationException ex) {
                throw new SdkClientException("Failed to instantiate com.amazonaws.services.securitytoken.internal.STSProfileCredentialsService", ex);
            }
            catch (IllegalAccessException ex) {
                throw new SdkClientException("Failed to instantiate com.amazonaws.services.securitytoken.internal.STSProfileCredentialsService", ex);
            }
        }
        return STS_CREDENTIALS_SERVICE;
    }

    @Override
    public AWSCredentials getCredentials() {
        return this.getProfileCredentialsProvider().getCredentials();
    }

    @Override
    public void refresh() {
        this.getProfileCredentialsProvider().refresh();
    }
}

