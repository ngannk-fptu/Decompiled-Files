/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 */
package com.atlassian.confluence.plugins.rest.entities.builders;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.rest.entities.builders.ContentEntityBuilder;
import com.atlassian.confluence.plugins.rest.entities.builders.SearchEntityBuilder;

public interface EntityBuilderFactory {
    public SearchEntityBuilder createBuilder(String var1);

    public <T extends ContentEntityObject> ContentEntityBuilder<? super T> createContentEntityBuilder(Class<? extends T> var1);
}

