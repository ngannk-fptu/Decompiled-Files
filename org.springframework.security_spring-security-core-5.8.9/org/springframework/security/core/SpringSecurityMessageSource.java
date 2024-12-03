/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.MessageSource
 *  org.springframework.context.support.MessageSourceAccessor
 *  org.springframework.context.support.ResourceBundleMessageSource
 */
package org.springframework.security.core;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;

public class SpringSecurityMessageSource
extends ResourceBundleMessageSource {
    public SpringSecurityMessageSource() {
        this.setBasename("org.springframework.security.messages");
    }

    public static MessageSourceAccessor getAccessor() {
        return new MessageSourceAccessor((MessageSource)new SpringSecurityMessageSource());
    }
}

