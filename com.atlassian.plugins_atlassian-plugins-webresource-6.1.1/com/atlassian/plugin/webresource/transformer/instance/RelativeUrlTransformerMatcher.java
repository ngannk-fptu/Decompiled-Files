/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.elements.ResourceLocation
 */
package com.atlassian.plugin.webresource.transformer.instance;

import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.webresource.transformer.WebResourceTransformerMatcher;

public class RelativeUrlTransformerMatcher
implements WebResourceTransformerMatcher {
    @Override
    public boolean matches(String type) {
        if (type != null) {
            String fileType = type.toLowerCase();
            return fileType.equals("css") || fileType.equals("less");
        }
        return false;
    }

    @Override
    public boolean matches(ResourceLocation resourceLocation) {
        if (resourceLocation.getName() != null) {
            String location = resourceLocation.getName().toLowerCase();
            return location.endsWith(".css") || location.endsWith(".less");
        }
        return false;
    }
}

