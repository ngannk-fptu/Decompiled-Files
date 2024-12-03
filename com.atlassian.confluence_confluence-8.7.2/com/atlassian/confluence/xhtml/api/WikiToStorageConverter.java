/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.xhtml.api;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.core.ContentEntityObject;
import java.util.List;

public interface WikiToStorageConverter {
    public String convertWikiToStorage(String var1, ConversionContext var2, List<RuntimeException> var3);

    public <T extends ContentEntityObject> T convertWikiBodyToStorage(T var1);
}

