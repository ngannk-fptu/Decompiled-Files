/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 */
package com.atlassian.confluence.plugins.createcontent.rest;

import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;

public interface IconUrlProvider {
    public String getDefaultIconUrl();

    public String getIconURL(WebItemModuleDescriptor var1);
}

