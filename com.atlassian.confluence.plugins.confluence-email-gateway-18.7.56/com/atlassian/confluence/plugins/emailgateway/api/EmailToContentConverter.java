/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.validation.MessageHolder
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.annotations.PublicSpi;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.emailgateway.api.StagedEmailThread;
import com.atlassian.confluence.validation.MessageHolder;

@PublicSpi
public interface EmailToContentConverter<C extends ContentEntityObject> {
    public C publish(StagedEmailThread var1, MessageHolder var2);
}

