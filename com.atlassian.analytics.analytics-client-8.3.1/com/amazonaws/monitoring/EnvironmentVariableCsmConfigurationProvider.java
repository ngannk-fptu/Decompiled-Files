/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.monitoring;

import com.amazonaws.SdkClientException;
import com.amazonaws.annotation.ThreadSafe;
import com.amazonaws.monitoring.CsmConfiguration;
import com.amazonaws.monitoring.CsmConfigurationProvider;

@ThreadSafe
public final class EnvironmentVariableCsmConfigurationProvider
implements CsmConfigurationProvider {
    @Override
    public CsmConfiguration getConfiguration() {
        String enabled = System.getenv("AWS_CSM_ENABLED");
        if (enabled == null) {
            throw new SdkClientException("Unable to load Client Side Monitoring configurations from environment variables!");
        }
        String host = System.getenv("AWS_CSM_HOST");
        host = host == null ? "127.0.0.1" : host;
        String port = System.getenv("AWS_CSM_PORT");
        String clientId = System.getenv("AWS_CSM_CLIENT_ID");
        clientId = clientId == null ? "" : clientId;
        try {
            int portNumber = port == null ? 31000 : Integer.parseInt(port);
            return CsmConfiguration.builder().withEnabled(Boolean.parseBoolean(enabled)).withHost(host).withPort(portNumber).withClientId(clientId).build();
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to load Client Side Monitoring configurations from environment variables!", e);
        }
    }
}

