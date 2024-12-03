/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.RequestConfig;
import com.amazonaws.auth.AWSCredentialsProvider;

public class CredentialUtils {
    public static AWSCredentialsProvider getCredentialsProvider(AmazonWebServiceRequest req, AWSCredentialsProvider base) {
        if (req != null && req.getRequestCredentialsProvider() != null) {
            return req.getRequestCredentialsProvider();
        }
        return base;
    }

    public static AWSCredentialsProvider getCredentialsProvider(RequestConfig requestConfig, AWSCredentialsProvider base) {
        if (requestConfig.getCredentialsProvider() != null) {
            return requestConfig.getCredentialsProvider();
        }
        return base;
    }
}

