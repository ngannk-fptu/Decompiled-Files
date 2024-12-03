/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.security.authentication.event;

import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

public class InteractiveAuthenticationSuccessEvent
extends AbstractAuthenticationEvent {
    private final Class<?> generatedBy;

    public InteractiveAuthenticationSuccessEvent(Authentication authentication, Class<?> generatedBy) {
        super(authentication);
        Assert.notNull(generatedBy, (String)"generatedBy cannot be null");
        this.generatedBy = generatedBy;
    }

    public Class<?> getGeneratedBy() {
        return this.generatedBy;
    }
}

