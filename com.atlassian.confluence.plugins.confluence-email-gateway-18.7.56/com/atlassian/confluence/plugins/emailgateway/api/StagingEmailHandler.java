/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 *  com.atlassian.confluence.core.ContentEntityObject
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.annotations.PublicSpi;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.emailgateway.api.EmailHandler;
import com.atlassian.confluence.plugins.emailgateway.api.EmailToContentConverter;

@PublicSpi
public interface StagingEmailHandler<C extends ContentEntityObject>
extends EmailHandler {
    public EmailToContentConverter<C> getConverter();
}

