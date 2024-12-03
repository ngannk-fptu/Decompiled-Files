/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.services.s3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class S3CredentialsProviderChain
extends DefaultAWSCredentialsProviderChain {
    private static Log LOG = LogFactory.getLog(S3CredentialsProviderChain.class);

    S3CredentialsProviderChain() {
    }

    @Override
    public AWSCredentials getCredentials() {
        try {
            return super.getCredentials();
        }
        catch (AmazonClientException amazonClientException) {
            LOG.debug((Object)"No credentials available; falling back to anonymous access");
            return new AnonymousAWSCredentials();
        }
    }
}

