/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.AttachmentResource
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.InputStreamAttachmentResource
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.FileUploadManager
 *  com.atlassian.confluence.rpc.NotPermittedException
 *  com.atlassian.confluence.rpc.RemoteException
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.user.User
 *  org.apache.commons.io.IOUtils
 */
package com.atlassian.confluence.rpc.soap.services;

import com.atlassian.confluence.core.AttachmentResource;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.InputStreamAttachmentResource;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.FileUploadManager;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.confluence.rpc.RemoteException;
import com.atlassian.confluence.rpc.soap.beans.RemoteAttachment;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;

public class AttachmentsSoapService {
    private AttachmentManager attachmentManager;
    private FileUploadManager fileUploadManager;
    private ContentEntityManager contentEntityManager;
    private PermissionManager permissionManager;
    public static final String __PARANAMER_DATA = "addAttachment long,com.atlassian.confluence.rpc.soap.beans.RemoteAttachment,byte contentId,remoteAttachment,attachmentData \ngetAttachment long,java.lang.String,int contentId,fileName,version \ngetAttachmentData long,java.lang.String,int contentId,fileName,version \nmoveAttachment long,java.lang.String,long,java.lang.String contentId,name,newContentId,newName \nremoveAttachment long,java.lang.String contentId,fileName \nsetAttachmentManager com.atlassian.confluence.pages.AttachmentManager attachmentManager \nsetContentEntityManager com.atlassian.confluence.core.ContentEntityManager contentEntityManager \nsetFileUploadManager com.atlassian.confluence.pages.FileUploadManager fileUploadManager \nsetPermissionManager com.atlassian.confluence.security.PermissionManager permissionManager \n";

    public RemoteAttachment addAttachment(long contentId, RemoteAttachment remoteAttachment, byte[] attachmentData) throws RemoteException {
        ContentEntityObject ceo = this.getCEO(contentId);
        if (!this.permissionManager.hasCreatePermission((User)AuthenticatedUserThreadLocal.get(), (Object)ceo, Attachment.class)) {
            throw new NotPermittedException("You do not have the permissions to perform this action");
        }
        InputStreamAttachmentResource resource = new InputStreamAttachmentResource((InputStream)new ByteArrayInputStream(attachmentData), remoteAttachment.getFileName(), remoteAttachment.getContentType(), (long)attachmentData.length, remoteAttachment.getComment(), false);
        try {
            this.fileUploadManager.storeResource((AttachmentResource)resource, ceo);
        }
        catch (RuntimeException e) {
            throw new RemoteException("Error saving attachment", (Throwable)e);
        }
        Attachment newAttachment = this.attachmentManager.getAttachment(ceo, remoteAttachment.getFileName());
        return new RemoteAttachment(newAttachment);
    }

    public RemoteAttachment getAttachment(long contentId, String fileName, int version) throws RemoteException {
        Attachment attachment = this.getAttachmentInternal(contentId, fileName, version);
        return new RemoteAttachment(attachment);
    }

    private Attachment getAttachmentInternal(long contentId, String fileName, int version) throws RemoteException {
        ContentEntityObject ceo = this.getCEO(contentId);
        Attachment attachment = this.getExistingAttachment(ceo, fileName, version);
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)attachment)) {
            throw new NotPermittedException("You do not have the permissions to perform this action");
        }
        return attachment;
    }

    public byte[] getAttachmentData(long contentId, String fileName, int version) throws RemoteException {
        Attachment attachment = this.getAttachmentInternal(contentId, fileName, version);
        ByteArrayOutputStream baos = new ByteArrayOutputStream((int)attachment.getFileSize());
        try (InputStream attachmentStream = this.attachmentManager.getAttachmentData(attachment);){
            IOUtils.copy((InputStream)attachmentStream, (OutputStream)baos);
        }
        catch (IOException ioe) {
            throw new RemoteException("Error reading attachment contents", (Throwable)ioe);
        }
        return baos.toByteArray();
    }

    public boolean removeAttachment(long contentId, String fileName) throws RemoteException {
        ContentEntityObject ceo = this.getCEO(contentId);
        Attachment attachment = this.getExistingCurrentAttachment(ceo, fileName);
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.REMOVE, (Object)attachment)) {
            throw new NotPermittedException("You do not have the permissions to perform this action");
        }
        this.attachmentManager.removeAttachmentFromServer(attachment);
        return true;
    }

    public boolean moveAttachment(long contentId, String name, long newContentId, String newName) throws RemoteException {
        ContentEntityObject existingCeo = this.getCEO(contentId);
        Attachment attachment = this.getExistingCurrentAttachment(existingCeo, name);
        ContentEntityObject newCeo = this.getCEO(newContentId);
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.REMOVE, (Object)attachment) || !this.permissionManager.hasCreatePermission((User)AuthenticatedUserThreadLocal.get(), (Object)newCeo, Attachment.class)) {
            throw new NotPermittedException("You do not have the permissions to perform this action");
        }
        this.attachmentManager.moveAttachment(attachment, newName, newCeo);
        return true;
    }

    private ContentEntityObject getCEO(long id) throws RemoteException {
        ContentEntityObject ceo = this.contentEntityManager.getById(id);
        if (ceo == null) {
            throw new RemoteException("No content with id " + id + " exists.");
        }
        return ceo;
    }

    private Attachment getExistingAttachment(ContentEntityObject ceo, String fileName, int version) throws RemoteException {
        Attachment attachment = this.attachmentManager.getAttachment(ceo, fileName, version);
        if (attachment == null) {
            throw new RemoteException("No attachment on content with id " + ceo.getId() + ", name '" + fileName + "' and version " + version + " exists.");
        }
        return attachment;
    }

    private Attachment getExistingCurrentAttachment(ContentEntityObject ceo, String fileName) throws RemoteException {
        Attachment attachment = this.attachmentManager.getAttachment(ceo, fileName);
        if (attachment == null) {
            throw new RemoteException("No attachment on content with id " + ceo.getId() + ", name '" + fileName + "' exists.");
        }
        return attachment;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public void setContentEntityManager(ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }

    public void setFileUploadManager(FileUploadManager fileUploadManager) {
        this.fileUploadManager = fileUploadManager;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }
}

