/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.apache.commons.fileupload.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

public class ServletFileUpload
extends FileUpload {
    private static final String POST_METHOD = "POST";

    public static final boolean isMultipartContent(HttpServletRequest request) {
        if (!POST_METHOD.equalsIgnoreCase(request.getMethod())) {
            return false;
        }
        return FileUploadBase.isMultipartContent(new ServletRequestContext(request));
    }

    public ServletFileUpload() {
    }

    public ServletFileUpload(FileItemFactory fileItemFactory) {
        super(fileItemFactory);
    }

    @Override
    public List<FileItem> parseRequest(HttpServletRequest request) throws FileUploadException {
        return this.parseRequest(new ServletRequestContext(request));
    }

    public Map<String, List<FileItem>> parseParameterMap(HttpServletRequest request) throws FileUploadException {
        return this.parseParameterMap(new ServletRequestContext(request));
    }

    public FileItemIterator getItemIterator(HttpServletRequest request) throws FileUploadException, IOException {
        return super.getItemIterator(new ServletRequestContext(request));
    }
}

