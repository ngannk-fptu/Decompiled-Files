/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.servlet.download.SafeContentHeaderGuesser
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 */
package com.atlassian.confluence.extra.webdav.resource;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.resource.AbstractAttachmentResource;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.servlet.download.SafeContentHeaderGuesser;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.lock.LockManager;

public class SpaceAttachmentResourceImpl
extends AbstractAttachmentResource {
    private final SpaceManager spaceManager;
    private final String spaceKey;

    public SpaceAttachmentResourceImpl(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession, @ComponentImport SpaceManager spaceManager, @ComponentImport SafeContentHeaderGuesser attachmentSafeContentHeaderGuesser, @ComponentImport AttachmentManager attachmentManager, String spaceKey, String attachmentName, String userAgent) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession, attachmentSafeContentHeaderGuesser, attachmentManager, attachmentName, userAgent);
        this.spaceManager = spaceManager;
        this.spaceKey = spaceKey;
    }

    @Override
    public ContentEntityObject getContentEntityObject() {
        return this.spaceManager.getSpace(this.spaceKey).getDescription();
    }
}

