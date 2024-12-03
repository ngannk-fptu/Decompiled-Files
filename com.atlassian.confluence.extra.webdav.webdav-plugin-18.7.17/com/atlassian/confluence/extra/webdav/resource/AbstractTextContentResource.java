/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 */
package com.atlassian.confluence.extra.webdav.resource;

import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.resource.AbstractContentResource;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.lock.LockManager;

public abstract class AbstractTextContentResource
extends AbstractContentResource {
    public static final String CONTENT_TYPE = "text/plain";
    private final SettingsManager settingsManager;

    public AbstractTextContentResource(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession, @ComponentImport SettingsManager settingsManager) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession);
        this.settingsManager = settingsManager;
    }

    protected SettingsManager getSettingsManager() {
        return this.settingsManager;
    }

    protected abstract byte[] getTextContentAsBytes(String var1) throws UnsupportedEncodingException;

    protected String getContentTypeBase() {
        return CONTENT_TYPE;
    }

    @Override
    protected String getContentType() {
        return this.getContentTypeBase();
    }

    @Override
    protected InputStream getContent() {
        try {
            return new ByteArrayInputStream(this.getTextContentAsBytes(this.settingsManager.getGlobalSettings().getDefaultEncoding()));
        }
        catch (UnsupportedEncodingException uee) {
            throw new RuntimeException(uee);
        }
    }

    @Override
    protected long getContentLength() {
        try {
            return this.getTextContentAsBytes(this.settingsManager.getGlobalSettings().getDefaultEncoding()).length;
        }
        catch (UnsupportedEncodingException uee) {
            throw new RuntimeException(uee);
        }
    }
}

