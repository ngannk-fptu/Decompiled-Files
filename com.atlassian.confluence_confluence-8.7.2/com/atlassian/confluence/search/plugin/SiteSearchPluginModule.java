/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.plugin;

import com.atlassian.confluence.search.plugin.ContentTypeSearchDescriptor;
import java.util.Collection;

public interface SiteSearchPluginModule {
    public Collection<ContentTypeSearchDescriptor> getContentTypeDescriptors();
}

