/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.extra.webdav.ConfluenceDavSession
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.FileUploadManager
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.servlet.download.SafeContentHeaderGuesser
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.v2.macro.MacroManager
 *  com.benryan.components.OcSettingsManager
 *  org.apache.commons.lang.StringUtils
 *  org.apache.jackrabbit.webdav.DavException
 *  org.apache.jackrabbit.webdav.DavResource
 *  org.apache.jackrabbit.webdav.DavResourceFactory
 *  org.apache.jackrabbit.webdav.DavResourceLocator
 *  org.apache.jackrabbit.webdav.DavServletRequest
 *  org.apache.jackrabbit.webdav.DavServletResponse
 *  org.apache.jackrabbit.webdav.DavSession
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.benryan.servlet.webdav;

import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.FileUploadManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.servlet.download.SafeContentHeaderGuesser;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.v2.macro.MacroManager;
import com.benryan.components.OcSettingsManager;
import com.benryan.servlet.webdav.ResourceBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavServletRequest;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.DavSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="resourceBackend")
public class EditInWordResourceFactory
implements DavResourceFactory {
    private final SpaceManager spaceManager;
    private final PageManager pageManager;
    private final OcSettingsManager ocSettingsManager;
    private final MacroManager macroManager;
    private final PermissionManager permissionManager;
    private final AttachmentManager attachmentManager;
    private final FileUploadManager fileUploadManager;
    private final SafeContentHeaderGuesser attachmentSafeContentHeaderGuesser;

    @Autowired
    public EditInWordResourceFactory(@ComponentImport SpaceManager spaceManager, @ComponentImport PageManager pageManager, OcSettingsManager ocSettingsManager, @ComponentImport MacroManager macroManager, @ComponentImport PermissionManager permissionManager, @ComponentImport AttachmentManager attachmentManager, @ComponentImport FileUploadManager fileUploadManager, @ComponentImport SafeContentHeaderGuesser attachmentSafeContentHeaderGuesser) {
        this.spaceManager = spaceManager;
        this.pageManager = pageManager;
        this.ocSettingsManager = ocSettingsManager;
        this.macroManager = macroManager;
        this.permissionManager = permissionManager;
        this.attachmentManager = attachmentManager;
        this.fileUploadManager = fileUploadManager;
        this.attachmentSafeContentHeaderGuesser = attachmentSafeContentHeaderGuesser;
    }

    public DavResource createResource(DavResourceLocator davResourceLocator, DavServletRequest davServletRequest, DavServletResponse davServletResponse) throws DavException {
        DavSession davSession = (DavSession)davServletRequest.getSession().getAttribute(ConfluenceDavSession.class.getName());
        return this.createResource(davResourceLocator, davSession);
    }

    public DavResource createResource(DavResourceLocator davResourceLocator, DavSession davSession) throws DavException {
        ResourceBuilder builder = ResourceBuilder.initializeBuilder(this, davResourceLocator, davSession);
        String[] resourcePathTokens = StringUtils.split((String)davResourceLocator.getResourcePath(), (char)'/');
        if (resourcePathTokens.length < 2) {
            return builder.buildRootResource();
        }
        builder.pageId(resourcePathTokens[1]);
        if (resourcePathTokens.length == 2) {
            return builder.buildPageResource();
        }
        if ("attachments".equals(resourcePathTokens[2])) {
            if (resourcePathTokens.length >= 4) {
                return builder.buildAttachmentResource(this.attachmentManager, this.attachmentSafeContentHeaderGuesser, resourcePathTokens[3]);
            }
            return builder.buildAttachmentsResource();
        }
        return builder.buildContentResource();
    }

    public SpaceManager getSpaceManager() {
        return this.spaceManager;
    }

    public PermissionManager getPermissionManager() {
        return this.permissionManager;
    }

    public PageManager getPageManager() {
        return this.pageManager;
    }

    public AttachmentManager getAttachmentManager() {
        return this.attachmentManager;
    }

    public OcSettingsManager getOcSettingsManager() {
        return this.ocSettingsManager;
    }

    public MacroManager getMacroManager() {
        return this.macroManager;
    }

    public FileUploadManager getFileUploadManager() {
        return this.fileUploadManager;
    }
}

