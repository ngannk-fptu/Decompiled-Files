/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.security.access.event;

import java.util.Collection;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.event.AbstractAuthorizationEvent;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

@Deprecated
public class AuthorizationFailureEvent
extends AbstractAuthorizationEvent {
    private final AccessDeniedException accessDeniedException;
    private final Authentication authentication;
    private final Collection<ConfigAttribute> configAttributes;

    public AuthorizationFailureEvent(Object secureObject, Collection<ConfigAttribute> attributes, Authentication authentication, AccessDeniedException accessDeniedException) {
        super(secureObject);
        Assert.isTrue((attributes != null && authentication != null && accessDeniedException != null ? 1 : 0) != 0, (String)"All parameters are required and cannot be null");
        this.configAttributes = attributes;
        this.authentication = authentication;
        this.accessDeniedException = accessDeniedException;
    }

    public AccessDeniedException getAccessDeniedException() {
        return this.accessDeniedException;
    }

    public Authentication getAuthentication() {
        return this.authentication;
    }

    public Collection<ConfigAttribute> getConfigAttributes() {
        return this.configAttributes;
    }
}

