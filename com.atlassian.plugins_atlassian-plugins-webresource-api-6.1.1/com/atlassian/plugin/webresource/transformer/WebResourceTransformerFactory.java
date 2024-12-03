/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.dom4j.Element
 */
package com.atlassian.plugin.webresource.transformer;

import com.atlassian.plugin.webresource.transformer.TransformerParameters;
import com.atlassian.plugin.webresource.transformer.TransformerUrlBuilder;
import com.atlassian.plugin.webresource.transformer.UrlReadingWebResourceTransformer;
import org.dom4j.Element;

public interface WebResourceTransformerFactory {
    public TransformerUrlBuilder makeUrlBuilder(TransformerParameters var1);

    public UrlReadingWebResourceTransformer makeResourceTransformer(TransformerParameters var1);

    @Deprecated
    default public UrlReadingWebResourceTransformer makeResourceTransformer(Element configElement, TransformerParameters parameters) {
        return this.makeResourceTransformer(parameters);
    }
}

