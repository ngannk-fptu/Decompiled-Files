/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth.profile.internal.securitytoken;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.internal.securitytoken.ProfileCredentialsService;
import com.amazonaws.auth.profile.internal.securitytoken.RoleInfo;
import com.amazonaws.auth.profile.internal.securitytoken.STSProfileCredentialsServiceProvider;

@SdkInternalApi
public class STSProfileCredentialsServiceLoader
implements ProfileCredentialsService {
    private static final STSProfileCredentialsServiceLoader INSTANCE = new STSProfileCredentialsServiceLoader();

    private STSProfileCredentialsServiceLoader() {
    }

    @Override
    public AWSCredentialsProvider getAssumeRoleCredentialsProvider(RoleInfo targetRoleInfo) {
        return new STSProfileCredentialsServiceProvider(targetRoleInfo);
    }

    public static STSProfileCredentialsServiceLoader getInstance() {
        return INSTANCE;
    }
}

