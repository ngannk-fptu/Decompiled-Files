/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.rpc.NotPermittedException
 *  com.atlassian.confluence.security.GateKeeper
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.PersonalInformation
 *  com.atlassian.confluence.user.PersonalInformationManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.user.actions.TemporaryUploadedPicture
 *  com.atlassian.core.exception.InfrastructureException
 *  com.atlassian.plugins.rest.common.multipart.FilePart
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.io.FilenameUtils
 *  org.apache.commons.io.IOUtils
 */
package com.atlassian.confluence.plugins.user.profile;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.confluence.security.GateKeeper;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.actions.TemporaryUploadedPicture;
import com.atlassian.core.exception.InfrastructureException;
import com.atlassian.plugins.rest.common.multipart.FilePart;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

public class UserAvatarService {
    private AttachmentManager attachmentManager;
    private final UserAccessor userAccessor;
    private final PersonalInformationManager personalInformationManager;
    private final PermissionManager permissionManager;
    private final GateKeeper gateKeeper;
    private static final String RESIZED_IMAGE_MIME_TYPE = "image/jpeg";
    private static final String AVATAR_NAME = "user-avatar";

    public UserAvatarService(AttachmentManager attachmentManager, UserAccessor userAccessor, PersonalInformationManager personalInformationManager, PermissionManager permissionManager, GateKeeper gateKeeper) {
        this.attachmentManager = attachmentManager;
        this.personalInformationManager = personalInformationManager;
        this.userAccessor = userAccessor;
        this.permissionManager = permissionManager;
        this.gateKeeper = gateKeeper;
    }

    public Attachment saveAvatar(String userKey, String avatarDataURI) throws NotPermittedException {
        ConfluenceUser targetUser = this.userAccessor.getUserByKey(new UserKey(userKey));
        this.checkPermissions((User)targetUser);
        String base64Data = avatarDataURI.substring(avatarDataURI.indexOf(",") + 1);
        return this.saveAvatarData((User)targetUser, Base64.decodeBase64((String)base64Data));
    }

    private Attachment saveAvatarData(User targetUser, byte[] imageBytes) {
        Attachment userPhotoAttachment = this.saveNewAttachment(imageBytes);
        this.userAccessor.setUserProfilePicture(targetUser, userPhotoAttachment);
        return userPhotoAttachment;
    }

    private Attachment saveNewAttachment(byte[] imageBytes) {
        PersonalInformation userPersonalInformation = this.personalInformationManager.getOrCreatePersonalInformation((User)AuthenticatedUserThreadLocal.get());
        Attachment attachment = this.attachmentManager.getAttachment((ContentEntityObject)userPersonalInformation, AVATAR_NAME);
        Attachment previousVersion = null;
        if (attachment == null) {
            attachment = new Attachment();
        } else {
            try {
                previousVersion = (Attachment)attachment.clone();
            }
            catch (Exception e) {
                throw new InfrastructureException((Throwable)e);
            }
        }
        attachment.setMediaType(RESIZED_IMAGE_MIME_TYPE);
        attachment.setFileName(AVATAR_NAME);
        attachment.setVersionComment("Uploaded Profile Picture");
        attachment.setFileSize((long)imageBytes.length);
        userPersonalInformation.addAttachment(attachment);
        ByteArrayInputStream is = new ByteArrayInputStream(imageBytes);
        try {
            this.attachmentManager.saveAttachment(attachment, previousVersion, (InputStream)is);
        }
        catch (IOException e) {
            throw new InfrastructureException("Error saving attachment data: " + e.getMessage(), (Throwable)e);
        }
        finally {
            IOUtils.closeQuietly((InputStream)is);
        }
        return attachment;
    }

    public TemporaryUploadedPicture createTempLogoFile(FilePart filePart) throws IOException {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        TemporaryUploadedPicture tempLogoPic = TemporaryUploadedPicture.getPicture((InputStream)filePart.getInputStream(), (String)FilenameUtils.getBaseName((String)filePart.getName()), (String)currentUser.getName());
        if (tempLogoPic == null) {
            return null;
        }
        Predicate<User> permissionPredicate = u -> this.permissionManager.hasPermission(u, Permission.VIEW, PermissionManager.TARGET_PEOPLE_DIRECTORY);
        this.gateKeeper.addKey(tempLogoPic.getThumbnailFileDownloadPath(), (User)currentUser, permissionPredicate);
        return tempLogoPic;
    }

    protected void checkPermissions(User user) throws NotPermittedException {
        if (user == null || !this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.EDIT, (Object)user)) {
            throw new NotPermittedException("You do not have permission to change user details.");
        }
    }
}

