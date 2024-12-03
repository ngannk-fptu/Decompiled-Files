/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.awscore.client.builder.AwsAsyncClientBuilder
 */
package software.amazon.awssdk.services.secretsmanager;

import software.amazon.awssdk.awscore.client.builder.AwsAsyncClientBuilder;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerAsyncClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerBaseClientBuilder;

public interface SecretsManagerAsyncClientBuilder
extends AwsAsyncClientBuilder<SecretsManagerAsyncClientBuilder, SecretsManagerAsyncClient>,
SecretsManagerBaseClientBuilder<SecretsManagerAsyncClientBuilder, SecretsManagerAsyncClient> {
}

