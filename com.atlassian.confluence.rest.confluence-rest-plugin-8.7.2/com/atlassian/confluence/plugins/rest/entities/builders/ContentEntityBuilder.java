/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 */
package com.atlassian.confluence.plugins.rest.entities.builders;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.rest.entities.ContentEntity;

public interface ContentEntityBuilder<T extends ContentEntityObject> {
    public ContentEntity build(T var1);
}

