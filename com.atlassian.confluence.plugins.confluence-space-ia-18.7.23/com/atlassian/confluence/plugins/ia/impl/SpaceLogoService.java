/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.AttachmentResource
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.InputStreamAttachmentResource
 *  com.atlassian.confluence.event.events.space.SpaceLogoUpdateEvent
 *  com.atlassian.confluence.event.events.space.SpaceLogoUpdateEvent$SpaceLogoActions
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.FileUploadManager
 *  com.atlassian.confluence.rpc.NotPermittedException
 *  com.atlassian.confluence.security.GateKeeper
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.actions.TemporaryUploadedPicture
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugins.rest.common.multipart.FilePart
 *  com.atlassian.user.User
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.io.FilenameUtils
 */
package com.atlassian.confluence.plugins.ia.impl;

import com.atlassian.confluence.core.AttachmentResource;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.InputStreamAttachmentResource;
import com.atlassian.confluence.event.events.space.SpaceLogoUpdateEvent;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.FileUploadManager;
import com.atlassian.confluence.plugins.ia.impl.AbstractSidebarService;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.confluence.security.GateKeeper;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.actions.TemporaryUploadedPicture;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugins.rest.common.multipart.FilePart;
import com.atlassian.user.User;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;

public class SpaceLogoService
extends AbstractSidebarService {
    private final GateKeeper gateKeeper;
    private final FileUploadManager fileUploadManager;
    private AttachmentManager attachmentManager;
    private EventPublisher eventPublisher;

    public SpaceLogoService(SpaceManager spaceManager, PermissionManager permissionManager, GateKeeper gateKeeper, FileUploadManager fileUploadManager, AttachmentManager attachmentManager, EventPublisher eventPublisher) {
        super(permissionManager, spaceManager);
        this.gateKeeper = gateKeeper;
        this.fileUploadManager = fileUploadManager;
        this.attachmentManager = attachmentManager;
        this.eventPublisher = eventPublisher;
    }

    public TemporaryUploadedPicture createTempLogoFile(FilePart filePart) throws IOException {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        TemporaryUploadedPicture tempLogoPic = TemporaryUploadedPicture.getPicture((InputStream)filePart.getInputStream(), (String)FilenameUtils.getBaseName((String)filePart.getName()), (String)currentUser.getName());
        if (tempLogoPic == null) {
            return null;
        }
        Predicate<User> permissionPredicate = u -> this.permissionManager.hasPermission(u, Permission.VIEW, PermissionManager.TARGET_APPLICATION);
        this.gateKeeper.addKey(tempLogoPic.getThumbnailFileDownloadPath(), (User)currentUser, permissionPredicate);
        return tempLogoPic;
    }

    public void saveLogo(Space space, String logoDataURI) throws NotPermittedException {
        this.checkPermissions(space);
        String base64Data = logoDataURI.substring(logoDataURI.indexOf(",") + 1);
        this.saveLogoData(space, Base64.decodeBase64((String)base64Data));
    }

    private void saveLogoData(Space space, byte[] byteArray) {
        InputStreamAttachmentResource attachmentResource = new InputStreamAttachmentResource((InputStream)new ByteArrayInputStream(byteArray), space.getKey(), "image/png", (long)byteArray.length);
        this.fileUploadManager.storeResource((AttachmentResource)attachmentResource, (ContentEntityObject)space.getDescription());
        this.eventPublisher.publish((Object)new SpaceLogoUpdateEvent((Object)this, space, SpaceLogoUpdateEvent.SpaceLogoActions.fromString((String)"upload"), this.getLogo(space)));
    }

    public void changeSpaceName(Space space, String newSpaceName) throws NotPermittedException {
        this.checkPermissions(space);
        if (newSpaceName != null && !newSpaceName.equals(space.getName())) {
            space.setName(newSpaceName);
            this.spaceManager.saveSpace(space);
        }
    }

    private Attachment getLogo(Space space) {
        return this.attachmentManager.getAttachment((ContentEntityObject)space.getDescription(), space.getKey());
    }
}

