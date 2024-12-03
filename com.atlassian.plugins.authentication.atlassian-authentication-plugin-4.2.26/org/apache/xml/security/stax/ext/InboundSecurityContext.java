/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.ext;

import java.util.List;
import org.apache.xml.security.stax.ext.SecurityContext;
import org.apache.xml.security.stax.securityToken.InboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenProvider;

public interface InboundSecurityContext
extends SecurityContext {
    public void registerSecurityTokenProvider(String var1, SecurityTokenProvider<? extends InboundSecurityToken> var2);

    public SecurityTokenProvider<? extends InboundSecurityToken> getSecurityTokenProvider(String var1);

    public List<SecurityTokenProvider<? extends InboundSecurityToken>> getRegisteredSecurityTokenProviders();
}

