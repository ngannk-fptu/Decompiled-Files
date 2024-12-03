/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.FileUploadUtils
 *  com.atlassian.xwork.FileUploadUtils$FileUploadException
 *  com.atlassian.xwork.FileUploadUtils$UploadedFile
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.StrutsUtil;
import com.atlassian.xwork.FileUploadUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachFileHelper {
    private static final Logger log = LoggerFactory.getLogger(AttachFileHelper.class);
    private final MultiPartRequestWrapper multipartRequestWrapper;
    private final Set<String> errors = new HashSet<String>();
    private final List<FileUploadUtils.UploadedFile> uploadedFiles;
    private final Map<String, String> filenameToCommentMap = new HashMap<String, String>();
    private final int maxAttachments;

    public AttachFileHelper(MultiPartRequestWrapper multipartRequestWrapper, int maxAttachments) {
        this.multipartRequestWrapper = multipartRequestWrapper;
        this.maxAttachments = maxAttachments;
        this.uploadedFiles = this.initUploadedFiles();
    }

    public List<FileUploadUtils.UploadedFile> getUploadedFiles() {
        return this.uploadedFiles;
    }

    private List<FileUploadUtils.UploadedFile> initUploadedFiles() {
        try {
            return FileUploadUtils.getUploadedFiles((MultiPartRequestWrapper)this.multipartRequestWrapper);
        }
        catch (FileUploadUtils.FileUploadException e) {
            this.errors.addAll(StrutsUtil.localizeMultipartErrorMessages(e));
            return Collections.emptyList();
        }
    }

    public void validateAttachments() {
        for (int i = 0; i < this.maxAttachments; ++i) {
            String[] fileNames = this.multipartRequestWrapper.getFileNames("file_" + i);
            if (fileNames == null || fileNames.length == 0) continue;
            String fileName = fileNames[0];
            String comment = this.multipartRequestWrapper.getParameter("comment_" + i);
            Collection<String> fileValidationErrors = this.verifyFile(fileName);
            for (String error : fileValidationErrors) {
                this.errors.add(error);
                this.removeUploadedFile(fileName);
            }
            if (StringUtils.isNotEmpty((CharSequence)comment) && comment.length() > 255) {
                this.errors.add(this.makeEscapedFileNameError(fileName, "attachment.comment.too.long"));
                this.removeUploadedFile(fileName);
                continue;
            }
            this.filenameToCommentMap.put(fileName, comment);
        }
    }

    public String getCommentForFilename(String filename) {
        return this.filenameToCommentMap.get(filename);
    }

    public Collection<String> getErrors() {
        return this.errors;
    }

    private Collection<String> verifyFile(String fileName) {
        log.debug("Entering verifyFile() fileName=" + fileName);
        ArrayList<String> fileErrors = new ArrayList<String>();
        if (StringUtils.isNotEmpty((CharSequence)fileName)) {
            boolean fileNameMatches = false;
            for (FileUploadUtils.UploadedFile uploadedFile : this.getUploadedFiles()) {
                log.debug("fileName={}, uploadedFile.getFileName()={}", (Object)fileName, (Object)uploadedFile.getFileName());
                if (!fileName.equals(uploadedFile.getFileName())) continue;
                fileNameMatches = true;
                break;
            }
            if (!fileNameMatches) {
                fileErrors.add(this.makeEscapedFileNameError(fileName, "fileName.does.not.match.uploaded.file"));
            }
        } else {
            fileErrors.add(GeneralUtil.getI18n().getText("fileName.required"));
        }
        return fileErrors;
    }

    private String makeEscapedFileNameError(String fileName, String messageKey) {
        Object[] argsArray = new String[]{HtmlUtil.htmlEncode(fileName)};
        return GeneralUtil.getI18n().getText(messageKey, argsArray);
    }

    private void removeUploadedFile(String filename) {
        this.getUploadedFiles().removeIf(uploadedFile -> StringUtils.equals((CharSequence)filename, (CharSequence)uploadedFile.getFileName()));
    }
}

