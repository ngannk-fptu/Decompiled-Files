/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth.profile.internal.securitytoken;

import com.amazonaws.annotation.SdkProtectedApi;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.internal.securitytoken.RoleInfo;

@SdkProtectedApi
public interface ProfileCredentialsService {
    public AWSCredentialsProvider getAssumeRoleCredentialsProvider(RoleInfo var1);
}

