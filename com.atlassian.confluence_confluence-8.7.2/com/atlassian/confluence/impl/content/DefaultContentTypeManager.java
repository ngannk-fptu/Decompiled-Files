/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.content;

import com.atlassian.confluence.content.ContentType;
import com.atlassian.confluence.content.ContentTypeManager;
import com.atlassian.confluence.content.ContentTypeModuleDescriptor;
import com.atlassian.confluence.content.UninstalledContentType;
import com.atlassian.confluence.content.custom.CustomContentType;
import com.atlassian.confluence.impl.content.ContentTypeModuleResolver;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import java.util.ArrayList;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultContentTypeManager
implements ContentTypeManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultContentTypeManager.class);
    private final ContentTypeModuleResolver contentTypeModuleResolver;
    private final WebResourceUrlProvider webResourceUrlProvider;

    public DefaultContentTypeManager(ContentTypeModuleResolver contentTypeModuleResolver, WebResourceUrlProvider webResourceUrlProvider) {
        this.contentTypeModuleResolver = contentTypeModuleResolver;
        this.webResourceUrlProvider = webResourceUrlProvider;
    }

    @Override
    public ContentType getContentType(String contentTypeKey) {
        return this.contentTypeModuleResolver.findContentType(contentTypeKey).orElseGet(() -> {
            log.info("Unable to locate content type: " + contentTypeKey + " it is either uninstalled or deactivated");
            return UninstalledContentType.getInstance(this.webResourceUrlProvider);
        });
    }

    @Override
    public String getImplementingPluginVersion(String contentTypeKey) {
        ContentTypeModuleDescriptor moduleDescriptor = this.contentTypeModuleResolver.findModuleDescriptor(contentTypeKey).orElseThrow(() -> new IllegalStateException("Expected an enabled ContentTypeModuleDescriptor for key " + contentTypeKey));
        return moduleDescriptor.getPlugin().getPluginInformation().getVersion();
    }

    @Override
    public Collection<CustomContentType> getEnabledCustomContentTypes() {
        return this.toContentTypes(this.contentTypeModuleResolver.getAllModuleDescriptors());
    }

    private Collection<? super CustomContentType> toContentTypes(Collection<ContentTypeModuleDescriptor> descriptors) {
        ArrayList<CustomContentType> contentTypes = new ArrayList<CustomContentType>();
        for (ContentTypeModuleDescriptor descriptor : descriptors) {
            try {
                ContentType type = descriptor.getModule();
                if (!(type instanceof CustomContentType)) continue;
                contentTypes.add((CustomContentType)type);
            }
            catch (Exception ex) {
                log.warn("Removing type from enabled content types.  Custom content type for module [{}] threw an exception with msg : {}", (Object)descriptor.getCompleteKey(), (Object)ex.getMessage());
                log.debug("More info :", (Throwable)ex);
            }
        }
        return contentTypes;
    }
}

