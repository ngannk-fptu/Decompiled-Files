/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.client.reactive.ClientHttpConnector
 */
package org.springframework.vault.config;

import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.vault.support.ClientOptions;
import org.springframework.vault.support.SslConfiguration;

@Deprecated
public class ClientHttpConnectorFactory {
    public static ClientHttpConnector create(ClientOptions options, SslConfiguration sslConfiguration) {
        return org.springframework.vault.client.ClientHttpConnectorFactory.create(options, sslConfiguration);
    }
}

