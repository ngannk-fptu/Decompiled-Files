/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.fileupload.FileCountLimitExceededException
 *  org.apache.commons.fileupload.FileItem
 *  org.apache.commons.fileupload.FileItemFactory
 *  org.apache.commons.fileupload.FileUploadBase$FileSizeLimitExceededException
 *  org.apache.commons.fileupload.FileUploadBase$SizeLimitExceededException
 *  org.apache.commons.fileupload.FileUploadException
 *  org.apache.commons.fileupload.RequestContext
 *  org.apache.commons.fileupload.disk.DiskFileItemFactory
 *  org.apache.commons.fileupload.servlet.ServletFileUpload
 *  org.apache.commons.fileupload.servlet.ServletRequestContext
 */
package com.atlassian.plugins.rest.common.multipart.fileupload;

import com.atlassian.plugins.rest.common.multipart.FileCountLimitExceededException;
import com.atlassian.plugins.rest.common.multipart.FilePart;
import com.atlassian.plugins.rest.common.multipart.FileSizeLimitExceededException;
import com.atlassian.plugins.rest.common.multipart.MultipartForm;
import com.atlassian.plugins.rest.common.multipart.MultipartHandler;
import com.atlassian.plugins.rest.common.multipart.fileupload.CommonsFileUploadFilePart;
import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

public class CommonsFileUploadMultipartHandler
implements MultipartHandler {
    public static final long NO_LIMIT = -1L;
    public static final long DEFAULT_REQUEST_PART_LIMIT = 1000L;
    private final ServletFileUpload servletFileUpload = new ServletFileUpload((FileItemFactory)new DiskFileItemFactory());

    public CommonsFileUploadMultipartHandler() {
        this(-1L, -1L, 1000L);
    }

    public CommonsFileUploadMultipartHandler(long maxFileSize, long maxSize, long maxFileCount) {
        this.servletFileUpload.setFileSizeMax(maxFileSize);
        this.servletFileUpload.setSizeMax(maxSize);
        this.servletFileUpload.setFileCountMax(maxFileCount);
    }

    @Deprecated
    public CommonsFileUploadMultipartHandler(long maxFileSize, long maxSize) {
        this(maxFileSize, maxSize, 1000L);
    }

    @Override
    public FilePart getFilePart(HttpServletRequest request, String field) {
        return this.getForm(request).getFilePart(field);
    }

    @Override
    public MultipartForm getForm(HttpServletRequest request) {
        return this.getForm((RequestContext)new ServletRequestContext(request));
    }

    public MultipartForm getForm(RequestContext request) {
        try {
            return new CommonsFileUploadMultipartForm(this.servletFileUpload.parseRequest(request));
        }
        catch (FileUploadException e) {
            if (e instanceof FileUploadBase.FileSizeLimitExceededException || e instanceof FileUploadBase.SizeLimitExceededException) {
                throw new FileSizeLimitExceededException(e.getMessage());
            }
            if (e instanceof org.apache.commons.fileupload.FileCountLimitExceededException) {
                throw new FileCountLimitExceededException(e.getMessage());
            }
            throw new RuntimeException(e);
        }
    }

    private static class CommonsFileUploadMultipartForm
    implements MultipartForm {
        private final Collection<FileItem> fileItems;

        private CommonsFileUploadMultipartForm(Collection<FileItem> fileItems) {
            this.fileItems = fileItems;
        }

        @Override
        public FilePart getFilePart(String field) {
            for (FileItem item : this.fileItems) {
                if (!item.getFieldName().equals(field)) continue;
                return new CommonsFileUploadFilePart(item);
            }
            return null;
        }

        @Override
        public Collection<FilePart> getFileParts(String field) {
            ArrayList<FilePart> fileParts = new ArrayList<FilePart>();
            for (FileItem item : this.fileItems) {
                if (!item.getFieldName().equals(field)) continue;
                fileParts.add(new CommonsFileUploadFilePart(item));
            }
            return fileParts;
        }
    }
}

