/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.renderer.util.FileTypeUtil
 *  com.atlassian.user.User
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.confluence.extra.webdav.util;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.renderer.util.FileTypeUtil;
import com.atlassian.user.User;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.webdav.io.InputContext;

public class ResourceHelper {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static File getInputContextContentAsFile(InputContext inputContext) throws IOException {
        File file;
        File tempFile = File.createTempFile("webdav", null);
        InputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = inputContext.getInputStream();
            out = new BufferedOutputStream(new FileOutputStream(tempFile));
            IOUtils.copy((InputStream)in, (OutputStream)out);
            file = tempFile;
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly((InputStream)in);
            throw throwable;
        }
        IOUtils.closeQuietly((OutputStream)out);
        IOUtils.closeQuietly((InputStream)in);
        return file;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void addOrUpdateAttachment(AttachmentManager attachmentManager, ContentEntityObject ceo, String fileName, InputContext inputContext) throws IOException, CloneNotSupportedException {
        Attachment attachment = attachmentManager.getAttachment(ceo, fileName);
        Attachment previousVersionOfAttachment = null;
        File inputContentFile = null;
        BufferedInputStream inputContentStream = null;
        if (null == attachment) {
            attachment = new Attachment();
        } else {
            previousVersionOfAttachment = (Attachment)attachment.clone();
        }
        try {
            inputContentFile = ResourceHelper.getInputContextContentAsFile(inputContext);
            if (!fileName.startsWith("._") && inputContentFile.length() > 0L || fileName.startsWith("._") || !attachment.isPersistent()) {
                User user = AuthenticatedUserThreadLocal.getUser();
                attachment.setFileName(fileName);
                attachment.setFileSize(inputContentFile.length());
                attachment.setCreatorName(user.getName());
                attachment.setCreationDate(new Date());
                attachment.setLastModifierName(user.getName());
                attachment.setLastModificationDate(attachment.getCreationDate());
                attachment.setContainer(ceo);
                attachment.setContentType(StringUtils.isBlank((String)inputContext.getContentType()) ? FileTypeUtil.getContentType((String)fileName) : inputContext.getContentType());
                ceo.addAttachment(attachment);
                inputContentStream = new BufferedInputStream(new FileInputStream(inputContentFile));
                attachmentManager.saveAttachment(attachment, previousVersionOfAttachment, (InputStream)inputContentStream);
            }
        }
        finally {
            IOUtils.closeQuietly(inputContentStream);
            if (null != inputContentFile) {
                inputContentFile.delete();
            }
        }
    }
}

