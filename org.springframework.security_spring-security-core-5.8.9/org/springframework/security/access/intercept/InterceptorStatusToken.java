/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.access.intercept;

import java.util.Collection;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.context.SecurityContext;

@Deprecated
public class InterceptorStatusToken {
    private SecurityContext securityContext;
    private Collection<ConfigAttribute> attr;
    private Object secureObject;
    private boolean contextHolderRefreshRequired;

    public InterceptorStatusToken(SecurityContext securityContext, boolean contextHolderRefreshRequired, Collection<ConfigAttribute> attributes, Object secureObject) {
        this.securityContext = securityContext;
        this.contextHolderRefreshRequired = contextHolderRefreshRequired;
        this.attr = attributes;
        this.secureObject = secureObject;
    }

    public Collection<ConfigAttribute> getAttributes() {
        return this.attr;
    }

    public SecurityContext getSecurityContext() {
        return this.securityContext;
    }

    public Object getSecureObject() {
        return this.secureObject;
    }

    public boolean isContextHolderRefreshRequired() {
        return this.contextHolderRefreshRequired;
    }
}

