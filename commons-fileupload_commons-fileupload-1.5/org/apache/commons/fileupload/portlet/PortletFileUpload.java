/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.portlet.ActionRequest
 */
package org.apache.commons.fileupload.portlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.portlet.ActionRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.portlet.PortletRequestContext;

public class PortletFileUpload
extends FileUpload {
    public static final boolean isMultipartContent(ActionRequest request) {
        return FileUploadBase.isMultipartContent(new PortletRequestContext(request));
    }

    public PortletFileUpload() {
    }

    public PortletFileUpload(FileItemFactory fileItemFactory) {
        super(fileItemFactory);
    }

    public List<FileItem> parseRequest(ActionRequest request) throws FileUploadException {
        return this.parseRequest(new PortletRequestContext(request));
    }

    public Map<String, List<FileItem>> parseParameterMap(ActionRequest request) throws FileUploadException {
        return this.parseParameterMap(new PortletRequestContext(request));
    }

    public FileItemIterator getItemIterator(ActionRequest request) throws FileUploadException, IOException {
        return super.getItemIterator(new PortletRequestContext(request));
    }
}

