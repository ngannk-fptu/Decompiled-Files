/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.transformer.TransformerParameters
 *  com.atlassian.plugin.webresource.transformer.TransformerUrlBuilder
 */
package com.atlassian.plugin.webresource.transformer;

import com.atlassian.plugin.webresource.transformer.TransformerParameters;
import com.atlassian.plugin.webresource.transformer.TransformerUrlBuilder;
import com.atlassian.plugin.webresource.transformer.UrlReadingContentTransformer;

public interface ContentTransformerFactory {
    public TransformerUrlBuilder makeUrlBuilder(TransformerParameters var1);

    public UrlReadingContentTransformer makeResourceTransformer(TransformerParameters var1);
}

