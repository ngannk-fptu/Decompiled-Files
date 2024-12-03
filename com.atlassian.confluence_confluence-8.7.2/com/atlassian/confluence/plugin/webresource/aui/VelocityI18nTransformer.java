/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.elements.ResourceLocation
 *  com.atlassian.plugin.servlet.DownloadableResource
 *  com.atlassian.plugin.webresource.transformer.WebResourceTransformer
 *  org.dom4j.Element
 */
package com.atlassian.confluence.plugin.webresource.aui;

import com.atlassian.confluence.plugin.webresource.aui.VelocityTranslatedDownloadableResource;
import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.servlet.DownloadableResource;
import com.atlassian.plugin.webresource.transformer.WebResourceTransformer;
import org.dom4j.Element;

public class VelocityI18nTransformer
implements WebResourceTransformer {
    public DownloadableResource transform(Element configElement, ResourceLocation location, String filePath, DownloadableResource nextResource) {
        return new VelocityTranslatedDownloadableResource(nextResource);
    }
}

