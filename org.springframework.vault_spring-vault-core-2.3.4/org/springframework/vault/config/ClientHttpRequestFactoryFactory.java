/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.client.ClientHttpRequestFactory
 */
package org.springframework.vault.config;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.vault.support.ClientOptions;
import org.springframework.vault.support.SslConfiguration;

@Deprecated
public class ClientHttpRequestFactoryFactory {
    public static ClientHttpRequestFactory create(ClientOptions options, SslConfiguration sslConfiguration) {
        return org.springframework.vault.client.ClientHttpRequestFactoryFactory.create(options, sslConfiguration);
    }
}

