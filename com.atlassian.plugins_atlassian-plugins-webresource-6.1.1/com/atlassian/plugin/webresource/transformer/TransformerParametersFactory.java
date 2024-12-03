/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.transformer.TransformerParameters
 */
package com.atlassian.plugin.webresource.transformer;

import com.atlassian.plugin.webresource.WebResourceModuleDescriptor;
import com.atlassian.plugin.webresource.transformer.TransformerParameters;

@Deprecated
public class TransformerParametersFactory {
    public static TransformerParameters of(WebResourceModuleDescriptor webResourceModuleDescriptor) {
        return new TransformerParameters(webResourceModuleDescriptor.getPluginKey(), webResourceModuleDescriptor.getKey());
    }
}

