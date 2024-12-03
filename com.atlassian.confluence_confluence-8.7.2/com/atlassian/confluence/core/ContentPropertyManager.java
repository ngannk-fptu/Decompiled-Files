/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.ContentEntityObject;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ContentPropertyManager {
    @Transactional(readOnly=true)
    public @Nullable String getStringProperty(ContentEntityObject var1, String var2);

    @Transactional
    public void setStringProperty(ContentEntityObject var1, String var2, String var3);

    @Transactional(readOnly=true)
    public @Nullable String getTextProperty(ContentEntityObject var1, String var2);

    public void setTextProperty(ContentEntityObject var1, String var2, String var3);

    public void removeProperty(ContentEntityObject var1, String var2);

    public void removeProperties(ContentEntityObject var1);

    public void transferProperties(ContentEntityObject var1, ContentEntityObject var2);
}

