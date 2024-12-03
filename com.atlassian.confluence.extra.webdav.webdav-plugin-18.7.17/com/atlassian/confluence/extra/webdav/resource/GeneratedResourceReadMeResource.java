/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 */
package com.atlassian.confluence.extra.webdav.resource;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.ResourceStates;
import com.atlassian.confluence.extra.webdav.resource.AbstractConfluenceResource;
import com.atlassian.confluence.extra.webdav.resource.AbstractExportsResource;
import com.atlassian.confluence.extra.webdav.resource.AbstractTextContentResource;
import com.atlassian.confluence.extra.webdav.resource.AbstractVersionsResource;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.io.UnsupportedEncodingException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.lock.LockManager;

public class GeneratedResourceReadMeResource
extends AbstractTextContentResource {
    public static final String DISPLAY_NAME = "README.txt";

    public GeneratedResourceReadMeResource(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession, @ComponentImport SettingsManager settingsManager) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession, settingsManager);
    }

    protected String getReadMeContent() {
        return ConfluenceActionSupport.getTextStatic((String)"webdav.resource.generatedresourcereadme.content");
    }

    @Override
    protected byte[] getTextContentAsBytes(String encoding) throws UnsupportedEncodingException {
        return this.getReadMeContent().getBytes(encoding);
    }

    @Override
    protected long getCreationtTime() {
        AbstractConfluenceResource parent = (AbstractConfluenceResource)this.getCollection();
        return parent.getCreationtTime();
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    private boolean isHidden() {
        DavResource parentResource = this.getCollection();
        ResourceStates resourceStates = ((ConfluenceDavSession)this.getSession()).getResourceStates();
        if (parentResource instanceof AbstractVersionsResource) {
            return resourceStates.isContentVersionsReadmeHidden(((AbstractVersionsResource)parentResource).getContentEntityObject());
        }
        if (parentResource instanceof AbstractExportsResource) {
            return resourceStates.isContentExportsReadmeHidden(((AbstractExportsResource)parentResource).getContentEntityObject());
        }
        return false;
    }

    @Override
    public boolean exists() {
        return super.exists() && !this.isHidden();
    }
}

