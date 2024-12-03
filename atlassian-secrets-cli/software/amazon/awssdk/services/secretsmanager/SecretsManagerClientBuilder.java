/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager;

import software.amazon.awssdk.awscore.client.builder.AwsSyncClientBuilder;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerBaseClientBuilder;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

public interface SecretsManagerClientBuilder
extends AwsSyncClientBuilder<SecretsManagerClientBuilder, SecretsManagerClient>,
SecretsManagerBaseClientBuilder<SecretsManagerClientBuilder, SecretsManagerClient> {
}

