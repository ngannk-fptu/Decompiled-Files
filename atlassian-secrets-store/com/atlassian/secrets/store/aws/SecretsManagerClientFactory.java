/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
 */
package com.atlassian.secrets.store.aws;

import java.net.URI;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

public interface SecretsManagerClientFactory {
    public SecretsManagerClient getClient(String var1);

    public SecretsManagerClient getClient(String var1, URI var2);
}

