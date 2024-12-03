/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.xml.security.stax.ext.OutboundSecurityContext;
import org.apache.xml.security.stax.impl.AbstractSecurityContextImpl;
import org.apache.xml.security.stax.securityToken.OutboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenProvider;

public class OutboundSecurityContextImpl
extends AbstractSecurityContextImpl
implements OutboundSecurityContext {
    private final Map<String, SecurityTokenProvider<OutboundSecurityToken>> securityTokenProviders = new HashMap<String, SecurityTokenProvider<OutboundSecurityToken>>();

    @Override
    public void registerSecurityTokenProvider(String id, SecurityTokenProvider<OutboundSecurityToken> securityTokenProvider) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }
        this.securityTokenProviders.put(id, securityTokenProvider);
    }

    @Override
    public SecurityTokenProvider<OutboundSecurityToken> getSecurityTokenProvider(String id) {
        return this.securityTokenProviders.get(id);
    }

    @Override
    public List<SecurityTokenProvider<OutboundSecurityToken>> getRegisteredSecurityTokenProviders() {
        return new ArrayList<SecurityTokenProvider<OutboundSecurityToken>>(this.securityTokenProviders.values());
    }
}

