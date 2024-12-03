/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.FileUploadUtils
 *  com.atlassian.xwork.FileUploadUtils$FileUploadException
 *  com.opensymphony.xwork2.ActionSupport
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.actions.beans;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.xwork.FileUploadUtils;
import com.opensymphony.xwork2.ActionSupport;
import java.util.Enumeration;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileStorer {
    protected final ActionSupport action;
    protected final ContentEntityObject content;
    private static final Logger log = LoggerFactory.getLogger(FileStorer.class);
    protected Enumeration fileParameterNames;
    protected String fileName = null;
    protected boolean inited = false;

    public FileStorer(ActionSupport action, ContentEntityObject content) {
        this.action = action;
        this.content = content;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void processMultipartRequest(MultiPartRequestWrapper multiPartRequest) {
        try {
            FileUploadUtils.checkMultiPartRequestForErrors((MultiPartRequestWrapper)multiPartRequest);
        }
        catch (FileUploadUtils.FileUploadException e) {
            this.action.addActionError(this.action.getText("multipart.request.error"));
            log.warn("An error occured uploading a file to the server.", (Throwable)e);
        }
        this.fileParameterNames = multiPartRequest.getFileParameterNames();
        if (!this.fileParameterNames.hasMoreElements()) {
            this.action.addActionError(this.action.getText("null.file.error"));
            this.fileName = "";
        }
        if (this.action.getActionErrors().isEmpty()) {
            String theFileName;
            String fileParameterName = StringUtils.defaultString((String)((String)this.fileParameterNames.nextElement()));
            this.fileName = theFileName = multiPartRequest.getFileNames(fileParameterName)[0];
            if (theFileName == null) {
                this.action.addActionError(this.action.getText("null.file.error"));
            }
        }
        this.inited = true;
    }
}

