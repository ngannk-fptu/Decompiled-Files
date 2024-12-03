/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.config.ConfigurationProperties;
import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.impl.AbstractSecurityContextImpl;
import org.apache.xml.security.stax.securityEvent.AlgorithmSuiteSecurityEvent;
import org.apache.xml.security.stax.securityEvent.SecurityEvent;
import org.apache.xml.security.stax.securityEvent.SecurityEventConstants;
import org.apache.xml.security.stax.securityToken.InboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenProvider;

public class InboundSecurityContextImpl
extends AbstractSecurityContextImpl
implements InboundSecurityContext {
    private static final Boolean allowMD5Algorithm = Boolean.valueOf(ConfigurationProperties.getProperty("AllowMD5Algorithm"));
    private final Map<String, SecurityTokenProvider<? extends InboundSecurityToken>> securityTokenProviders = new HashMap<String, SecurityTokenProvider<? extends InboundSecurityToken>>();

    @Override
    protected void forwardSecurityEvent(SecurityEvent securityEvent) throws XMLSecurityException {
        AlgorithmSuiteSecurityEvent algorithmSuiteSecurityEvent;
        if (!allowMD5Algorithm.booleanValue() && SecurityEventConstants.AlgorithmSuite.equals(securityEvent.getSecurityEventType()) && ((algorithmSuiteSecurityEvent = (AlgorithmSuiteSecurityEvent)securityEvent).getAlgorithmURI().contains("md5") || algorithmSuiteSecurityEvent.getAlgorithmURI().contains("MD5"))) {
            throw new XMLSecurityException("secureProcessing.AllowMD5Algorithm");
        }
        super.forwardSecurityEvent(securityEvent);
    }

    @Override
    public void registerSecurityTokenProvider(String id, SecurityTokenProvider<? extends InboundSecurityToken> securityTokenProvider) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }
        this.securityTokenProviders.put(id, securityTokenProvider);
    }

    @Override
    public SecurityTokenProvider<? extends InboundSecurityToken> getSecurityTokenProvider(String id) {
        return this.securityTokenProviders.get(id);
    }

    @Override
    public List<SecurityTokenProvider<? extends InboundSecurityToken>> getRegisteredSecurityTokenProviders() {
        return new ArrayList<SecurityTokenProvider<? extends InboundSecurityToken>>(this.securityTokenProviders.values());
    }
}

