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
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.util.Assert;

@Deprecated
public class AuthenticationCredentialsNotFoundEvent
extends AbstractAuthorizationEvent {
    private final AuthenticationCredentialsNotFoundException credentialsNotFoundException;
    private final Collection<ConfigAttribute> configAttribs;

    public AuthenticationCredentialsNotFoundEvent(Object secureObject, Collection<ConfigAttribute> attributes, AuthenticationCredentialsNotFoundException credentialsNotFoundException) {
        super(secureObject);
        Assert.isTrue((attributes != null && credentialsNotFoundException != null ? 1 : 0) != 0, (String)"All parameters are required and cannot be null");
        this.configAttribs = attributes;
        this.credentialsNotFoundException = credentialsNotFoundException;
    }

    public Collection<ConfigAttribute> getConfigAttributes() {
        return this.configAttribs;
    }

    public AuthenticationCredentialsNotFoundException getCredentialsNotFoundException() {
        return this.credentialsNotFoundException;
    }
}

