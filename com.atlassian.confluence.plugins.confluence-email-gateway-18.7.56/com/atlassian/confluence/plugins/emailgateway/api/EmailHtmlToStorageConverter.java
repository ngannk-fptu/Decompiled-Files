/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;

@PublicApi
public interface EmailHtmlToStorageConverter {
    public String convert(String var1, ConversionContext var2);
}

