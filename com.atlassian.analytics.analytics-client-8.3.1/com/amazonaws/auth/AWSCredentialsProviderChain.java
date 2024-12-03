/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.auth;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AWSCredentialsProviderChain
implements AWSCredentialsProvider {
    private static final Log log = LogFactory.getLog(AWSCredentialsProviderChain.class);
    private final List<AWSCredentialsProvider> credentialsProviders = new LinkedList<AWSCredentialsProvider>();
    private boolean reuseLastProvider = true;
    private AWSCredentialsProvider lastUsedProvider;

    public AWSCredentialsProviderChain(List<? extends AWSCredentialsProvider> credentialsProviders) {
        if (credentialsProviders == null || credentialsProviders.size() == 0) {
            throw new IllegalArgumentException("No credential providers specified");
        }
        this.credentialsProviders.addAll(credentialsProviders);
    }

    public AWSCredentialsProviderChain(AWSCredentialsProvider ... credentialsProviders) {
        if (credentialsProviders == null || credentialsProviders.length == 0) {
            throw new IllegalArgumentException("No credential providers specified");
        }
        for (AWSCredentialsProvider provider : credentialsProviders) {
            this.credentialsProviders.add(provider);
        }
    }

    public boolean getReuseLastProvider() {
        return this.reuseLastProvider;
    }

    public void setReuseLastProvider(boolean b) {
        this.reuseLastProvider = b;
    }

    @Override
    public AWSCredentials getCredentials() {
        if (this.reuseLastProvider && this.lastUsedProvider != null) {
            return this.lastUsedProvider.getCredentials();
        }
        LinkedList<String> exceptionMessages = null;
        for (AWSCredentialsProvider provider : this.credentialsProviders) {
            try {
                AWSCredentials credentials = provider.getCredentials();
                if (credentials.getAWSAccessKeyId() == null || credentials.getAWSSecretKey() == null) continue;
                log.debug((Object)("Loading credentials from " + provider.toString()));
                this.lastUsedProvider = provider;
                return credentials;
            }
            catch (Exception e) {
                String message = provider + ": " + e.getMessage();
                log.debug((Object)("Unable to load credentials from " + message));
                if (exceptionMessages == null) {
                    exceptionMessages = new LinkedList<String>();
                }
                exceptionMessages.add(message);
            }
        }
        throw new SdkClientException("Unable to load AWS credentials from any provider in the chain: " + exceptionMessages);
    }

    @Override
    public void refresh() {
        for (AWSCredentialsProvider provider : this.credentialsProviders) {
            provider.refresh();
        }
    }
}

