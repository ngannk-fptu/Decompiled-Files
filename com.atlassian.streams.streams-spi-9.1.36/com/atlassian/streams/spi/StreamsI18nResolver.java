/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 */
package com.atlassian.streams.spi;

import com.atlassian.sal.api.message.I18nResolver;

public interface StreamsI18nResolver
extends I18nResolver {
    public void setRequestLanguages(String var1);
}

