/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.ext;

import java.util.List;
import org.apache.xml.security.stax.ext.SecurityContext;
import org.apache.xml.security.stax.securityToken.OutboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenProvider;

public interface OutboundSecurityContext
extends SecurityContext {
    public void registerSecurityTokenProvider(String var1, SecurityTokenProvider<OutboundSecurityToken> var2);

    public SecurityTokenProvider<OutboundSecurityToken> getSecurityTokenProvider(String var1);

    public List<SecurityTokenProvider<OutboundSecurityToken>> getRegisteredSecurityTokenProviders();
}

