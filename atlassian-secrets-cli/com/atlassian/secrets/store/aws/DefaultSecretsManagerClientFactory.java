/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.secrets.store.aws;

import com.atlassian.secrets.store.aws.SecretsManagerClientFactory;
import java.net.URI;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClientBuilder;

public class DefaultSecretsManagerClientFactory
implements SecretsManagerClientFactory {
    @Override
    public SecretsManagerClient getClient(String region) {
        return (SecretsManagerClient)((SecretsManagerClientBuilder)((SecretsManagerClientBuilder)SecretsManagerClient.builder().credentialsProvider(DefaultCredentialsProvider.create())).region(Region.of(region))).build();
    }

    @Override
    public SecretsManagerClient getClient(String region, URI endpointOverride) {
        return (SecretsManagerClient)((SecretsManagerClientBuilder)((SecretsManagerClientBuilder)((SecretsManagerClientBuilder)SecretsManagerClient.builder().credentialsProvider(DefaultCredentialsProvider.create())).region(Region.of(region))).endpointOverride(endpointOverride)).build();
    }
}

