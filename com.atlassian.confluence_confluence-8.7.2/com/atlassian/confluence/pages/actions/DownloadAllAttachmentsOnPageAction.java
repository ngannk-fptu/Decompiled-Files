/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.FileUtils
 *  com.atlassian.core.util.RandomGenerator
 *  com.atlassian.event.Event
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.io.ByteStreams
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.event.events.content.attachment.AttachmentViewEvent;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.actions.AbstractPageAwareAction;
import com.atlassian.confluence.security.GateKeeper;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.setup.settings.ConfluenceDirectories;
import com.atlassian.confluence.util.io.ConfluenceFileUtils;
import com.atlassian.core.util.FileUtils;
import com.atlassian.core.util.RandomGenerator;
import com.atlassian.event.Event;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DownloadAllAttachmentsOnPageAction
extends AbstractPageAwareAction {
    AttachmentManager attachmentManager;
    private static final String ZIP_FILE_PATTERN = "download{0}{1,time,HHmmss}";
    public static final String DELIMITER_DEFAULT = ",";
    private String downloadPath;
    private File tempDirectoryForZipping;
    private String zipFilename;
    private GateKeeper gateKeeper;
    private ConfluenceDirectories confluenceDirectories;
    private String attachmentIds;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        List attachmentIdList = this.getAttachmentIds() != null ? Arrays.asList(this.getAttachmentIds().split(DELIMITER_DEFAULT)) : Collections.emptyList();
        List<Attachment> attachmentsToDownload = this.attachmentManager.getLatestVersionsOfAttachments(this.getPage()).stream().filter(attachment -> attachmentIdList.isEmpty() || attachmentIdList.contains(attachment.getIdAsString())).collect(Collectors.toList());
        for (Attachment attachment2 : attachmentsToDownload) {
            File tmpFile = new File(this.getTempDirectoryForZipping(), ConfluenceFileUtils.extractFileName(attachment2.getFileName()));
            InputStream inputStream = this.attachmentManager.getAttachmentData(attachment2);
            try (FileOutputStream fileOutputStream = new FileOutputStream(tmpFile);){
                ByteStreams.copy((InputStream)inputStream, (OutputStream)fileOutputStream);
            }
            finally {
                if (inputStream == null) continue;
                inputStream.close();
            }
        }
        File zipFile = new File(this.getConfluenceTempDirectoryPath() + File.separator + this.getZipFilename() + ".zip");
        FileUtils.createZipFile((File)this.getTempDirectoryForZipping(), (File)zipFile);
        FileUtils.deleteDir((File)this.getTempDirectoryForZipping());
        this.downloadPath = this.prepareDownloadPath(zipFile.getPath()) + "?contentType=application/zip";
        Predicate<User> permissionPredicate = u -> this.permissionManager.hasPermission((User)u, Permission.VIEW, this.getPage());
        this.gateKeeper.addKey(this.prepareDownloadPath(zipFile.getPath()), this.getAuthenticatedUser(), permissionPredicate);
        if (!attachmentsToDownload.isEmpty()) {
            this.eventManager.publishEvent((Event)new AttachmentViewEvent((Object)this, attachmentsToDownload, true));
        }
        return "success";
    }

    private File getTempDirectoryForZipping() throws IOException {
        if (this.tempDirectoryForZipping == null) {
            this.tempDirectoryForZipping = new File(this.getConfluenceTempDirectoryPath() + File.separator + this.getZipFilename());
            if (!this.tempDirectoryForZipping.exists()) {
                if (this.tempDirectoryForZipping.mkdirs()) {
                    return this.tempDirectoryForZipping;
                }
                throw new IOException("Could not create directory: " + this.tempDirectoryForZipping.getPath());
            }
        }
        return this.tempDirectoryForZipping;
    }

    private String getConfluenceTempDirectoryPath() {
        return this.confluenceDirectories.getTempDirectory().toString();
    }

    private String getZipFilename() {
        if (this.zipFilename == null) {
            this.zipFilename = MessageFormat.format(ZIP_FILE_PATTERN, RandomGenerator.randomString((int)5), new Date());
        }
        return this.zipFilename;
    }

    public String prepareDownloadPath(String path) throws IOException {
        String canonicalPath = new File(path).getCanonicalPath();
        int exportDirIndex = canonicalPath.indexOf(this.getConfluenceTempDirectoryPath());
        if (exportDirIndex != -1) {
            path = canonicalPath.substring(exportDirIndex + this.getConfluenceTempDirectoryPath().length());
        }
        return "/download/export" + path.replaceAll("\\\\", "/");
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public String getDownloadPath() {
        return this.downloadPath;
    }

    public void setGateKeeper(GateKeeper gateKeeper) {
        this.gateKeeper = gateKeeper;
    }

    public void setConfluenceDirectories(ConfluenceDirectories confluenceDirectories) {
        this.confluenceDirectories = confluenceDirectories;
    }

    public String getAttachmentIds() {
        return this.attachmentIds;
    }

    public void setAttachmentIds(String attachmentIds) {
        this.attachmentIds = attachmentIds;
    }
}

