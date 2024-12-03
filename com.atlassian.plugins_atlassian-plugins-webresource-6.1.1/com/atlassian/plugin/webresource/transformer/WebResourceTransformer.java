/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.elements.ResourceLocation
 *  com.atlassian.plugin.servlet.DownloadableResource
 *  org.dom4j.Element
 */
package com.atlassian.plugin.webresource.transformer;

import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.servlet.DownloadableResource;
import org.dom4j.Element;

@Deprecated
public interface WebResourceTransformer {
    @Deprecated
    public DownloadableResource transform(Element var1, ResourceLocation var2, String var3, DownloadableResource var4);

    @Deprecated
    default public DownloadableResource transform(Element configElement, ResourceLocation location, DownloadableResource nextResource) {
        return this.transform(configElement, location, "", nextResource);
    }
}

