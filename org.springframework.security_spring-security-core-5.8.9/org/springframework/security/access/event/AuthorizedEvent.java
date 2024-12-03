/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.security.access.event;

import java.util.Collection;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.event.AbstractAuthorizationEvent;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

@Deprecated
public class AuthorizedEvent
extends AbstractAuthorizationEvent {
    private final Authentication authentication;
    private final Collection<ConfigAttribute> configAttributes;

    public AuthorizedEvent(Object secureObject, Collection<ConfigAttribute> attributes, Authentication authentication) {
        super(secureObject);
        Assert.isTrue((attributes != null && authentication != null ? 1 : 0) != 0, (String)"All parameters are required and cannot be null");
        this.configAttributes = attributes;
        this.authentication = authentication;
    }

    public Authentication getAuthentication() {
        return this.authentication;
    }

    public Collection<ConfigAttribute> getConfigAttributes() {
        return this.configAttributes;
    }
}

