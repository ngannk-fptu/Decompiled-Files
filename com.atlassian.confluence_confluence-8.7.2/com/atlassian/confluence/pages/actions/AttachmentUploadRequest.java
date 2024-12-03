/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper
 *  org.apache.struts2.dispatcher.multipart.UploadedFile
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.core.AttachmentResource;
import com.atlassian.confluence.core.UploadedResource;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.UploadedFile;

public class AttachmentUploadRequest {
    private List<AttachmentResource> resources;

    public AttachmentUploadRequest(MultiPartRequestWrapper request) throws IllegalStateException {
        if (request.hasErrors()) {
            throw new IllegalStateException("Client should check for errors in the multi-part request");
        }
        this.resources = AttachmentUploadRequest.toUploadedResources(request);
    }

    private static List<AttachmentResource> toUploadedResources(MultiPartRequestWrapper request) {
        ArrayList<String> fileParameterNames = Collections.list(request.getFileParameterNames());
        ArrayList<AttachmentResource> result = new ArrayList<AttachmentResource>();
        for (String parameterName : fileParameterNames) {
            UploadedFile[] files = request.getFiles(parameterName);
            String[] filenames = request.getFileNames(parameterName);
            String[] contentTypes = request.getContentTypes(parameterName);
            for (int i = 0; i < files.length; ++i) {
                if (files[i] == null) continue;
                UploadedFile file = files[i];
                String filename = filenames[i];
                String customFilename = request.getParameter("fileName_" + result.size());
                filename = StringUtils.isBlank((CharSequence)customFilename) ? filename : customFilename;
                String contentType = contentTypes[i];
                String comment = request.getParameter("comment_" + result.size());
                boolean minorEdit = Boolean.parseBoolean(request.getParameter("minorEdit_" + result.size()));
                boolean hidden = Boolean.parseBoolean(request.getParameter("hidden_" + result.size()));
                result.add(new UploadedResource((File)file.getContent(), filename, contentType, comment, minorEdit, hidden));
            }
        }
        return result;
    }

    public List<AttachmentResource> getResources() {
        return this.resources;
    }

    public int getResourceCount() {
        return this.resources.size();
    }
}

