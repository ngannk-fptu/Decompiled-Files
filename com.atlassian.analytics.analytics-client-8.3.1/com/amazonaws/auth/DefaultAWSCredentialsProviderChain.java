/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth;

import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.EC2ContainerCredentialsProviderWrapper;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.auth.WebIdentityTokenCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;

public class DefaultAWSCredentialsProviderChain
extends AWSCredentialsProviderChain {
    private static final DefaultAWSCredentialsProviderChain INSTANCE = new DefaultAWSCredentialsProviderChain();

    public DefaultAWSCredentialsProviderChain() {
        super(new EnvironmentVariableCredentialsProvider(), new SystemPropertiesCredentialsProvider(), WebIdentityTokenCredentialsProvider.create(), new ProfileCredentialsProvider(), new EC2ContainerCredentialsProviderWrapper());
    }

    public static DefaultAWSCredentialsProviderChain getInstance() {
        return INSTANCE;
    }
}

