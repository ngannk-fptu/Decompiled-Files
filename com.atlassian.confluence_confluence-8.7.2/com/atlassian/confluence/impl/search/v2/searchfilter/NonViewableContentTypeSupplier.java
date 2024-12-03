/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.search.v2.searchfilter;

import com.atlassian.confluence.content.ContentType;
import com.atlassian.confluence.content.ContentTypeModuleDescriptor;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.PluginAccessor;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NonViewableContentTypeSupplier
implements Supplier<Set<String>> {
    private static final Logger log = LoggerFactory.getLogger(NonViewableContentTypeSupplier.class);
    private final PluginAccessor pluginAccessor;

    public NonViewableContentTypeSupplier(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    public Set<String> get() {
        List contentTypeModuleDescriptors = this.pluginAccessor.getEnabledModuleDescriptorsByClass(ContentTypeModuleDescriptor.class);
        if (contentTypeModuleDescriptors.isEmpty()) {
            return Collections.emptySet();
        }
        HashSet<String> contentTypeModuleKeys = new HashSet<String>();
        for (ContentTypeModuleDescriptor contentTypeModuleDescriptor : contentTypeModuleDescriptors) {
            ContentType contentType = null;
            try {
                contentType = contentTypeModuleDescriptor.getModule();
            }
            catch (Exception e) {
                log.debug("Error creating module: " + contentTypeModuleDescriptor, (Throwable)e);
            }
            try {
                if (contentType != null && contentType.getPermissionDelegate().canView(AuthenticatedUserThreadLocal.get())) continue;
                contentTypeModuleKeys.add(contentTypeModuleDescriptor.getCompleteKey());
            }
            catch (Exception e) {
                log.debug("Error: ", (Throwable)e);
            }
        }
        return contentTypeModuleKeys;
    }
}

