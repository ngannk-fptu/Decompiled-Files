/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.conversion.convert.html.HtmlConversionResult
 */
package com.benryan.components;

import com.atlassian.plugins.conversion.convert.html.HtmlConversionResult;
import com.benryan.components.AttachmentCacheKey;
import com.benryan.components.ConversionCacheManager;

public interface HtmlCacheManager
extends ConversionCacheManager {
    public HtmlConversionResult getHtmlConversionData(AttachmentCacheKey var1);

    public void addHtmlConversionData(AttachmentCacheKey var1, HtmlConversionResult var2);
}

