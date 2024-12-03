/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.confluence.extra.webdav.resource;

import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.resource.AbstractTextContentResource;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.io.UnsupportedEncodingException;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.lock.LockManager;

public class SpaceContentResourceImpl
extends AbstractTextContentResource {
    public static final String DISPLAY_NAME_SUFFIX = ".txt";
    private final SpaceManager spaceManager;
    private final String spaceKey;

    public SpaceContentResourceImpl(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession, @ComponentImport SettingsManager settingsManager, @ComponentImport SpaceManager spaceManager, String spaceKey) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession, settingsManager);
        this.spaceManager = spaceManager;
        this.spaceKey = spaceKey;
    }

    public Space getSpace() {
        return this.spaceManager.getSpace(this.spaceKey);
    }

    @Override
    public boolean exists() {
        return super.exists() && !((ConfluenceDavSession)this.getSession()).getResourceStates().isSpaceDescriptionHidden(this.getSpace());
    }

    @Override
    protected byte[] getTextContentAsBytes(String encoding) throws UnsupportedEncodingException {
        return StringUtils.defaultString((String)this.getSpace().getDescription().getBodyContent().getBody()).getBytes(encoding);
    }

    @Override
    public long getModificationTime() {
        return this.getSpace().getLastModificationDate().getTime();
    }

    @Override
    protected long getCreationtTime() {
        return this.getSpace().getCreationDate().getTime();
    }

    @Override
    public String getDisplayName() {
        return this.getSpace().getName() + DISPLAY_NAME_SUFFIX;
    }
}

