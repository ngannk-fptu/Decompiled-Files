/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.manager.sso.InvalidGlobalSamlConfigurationException
 *  com.atlassian.crowd.model.sso.SamlConfiguration
 */
package com.atlassian.crowd.manager.sso;

import com.atlassian.crowd.manager.sso.InvalidGlobalSamlConfigurationException;
import com.atlassian.crowd.model.sso.SamlConfiguration;
import java.security.cert.X509Certificate;
import java.util.Optional;

public interface CrowdSamlConfigurationService {
    public Optional<X509Certificate> getCertificateToVerifySignature();

    public X509Certificate regenerateCertificateAndPrivateKeyToSign();

    public Optional<SamlConfiguration> getConfiguration();

    public Optional<String> getMetadata() throws InvalidGlobalSamlConfigurationException;
}

