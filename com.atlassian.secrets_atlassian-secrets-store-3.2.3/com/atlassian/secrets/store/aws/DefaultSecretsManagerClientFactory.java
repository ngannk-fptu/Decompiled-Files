/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
 *  software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
 *  software.amazon.awssdk.services.secretsmanager.SecretsManagerClientBuilder
 */
package com.atlassian.secrets.store.aws;

import com.atlassian.secrets.store.aws.SecretsManagerClientFactory;
import java.net.URI;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClientBuilder;

public class DefaultSecretsManagerClientFactory
implements SecretsManagerClientFactory {
    @Override
    public SecretsManagerClient getClient(String region) {
        return (SecretsManagerClient)((SecretsManagerClientBuilder)((SecretsManagerClientBuilder)SecretsManagerClient.builder().credentialsProvider((AwsCredentialsProvider)DefaultCredentialsProvider.create())).region(Region.of((String)region))).build();
    }

    @Override
    public SecretsManagerClient getClient(String region, URI endpointOverride) {
        return (SecretsManagerClient)((SecretsManagerClientBuilder)((SecretsManagerClientBuilder)((SecretsManagerClientBuilder)SecretsManagerClient.builder().credentialsProvider((AwsCredentialsProvider)DefaultCredentialsProvider.create())).region(Region.of((String)region))).endpointOverride(endpointOverride)).build();
    }
}

