/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.util.StringUtils;

public class EnvironmentVariableCredentialsProvider
implements AWSCredentialsProvider {
    @Override
    public AWSCredentials getCredentials() {
        String secretKey;
        String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
        if (accessKey == null) {
            accessKey = System.getenv("AWS_ACCESS_KEY");
        }
        if ((secretKey = System.getenv("AWS_SECRET_KEY")) == null) {
            secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
        }
        accessKey = StringUtils.trim(accessKey);
        secretKey = StringUtils.trim(secretKey);
        String sessionToken = StringUtils.trim(System.getenv("AWS_SESSION_TOKEN"));
        if (StringUtils.isNullOrEmpty(accessKey) || StringUtils.isNullOrEmpty(secretKey)) {
            throw new SdkClientException("Unable to load AWS credentials from environment variables (AWS_ACCESS_KEY_ID (or AWS_ACCESS_KEY) and AWS_SECRET_KEY (or AWS_SECRET_ACCESS_KEY))");
        }
        return sessionToken == null ? new BasicAWSCredentials(accessKey, secretKey) : new BasicSessionCredentials(accessKey, secretKey, sessionToken);
    }

    @Override
    public void refresh() {
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }
}

