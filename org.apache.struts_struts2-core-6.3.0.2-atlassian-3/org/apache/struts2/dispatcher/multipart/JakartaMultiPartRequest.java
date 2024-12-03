/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletInputStream
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.fileupload.FileCountLimitExceededException
 *  org.apache.commons.fileupload.FileItem
 *  org.apache.commons.fileupload.FileItemFactory
 *  org.apache.commons.fileupload.FileUploadBase$FileSizeLimitExceededException
 *  org.apache.commons.fileupload.FileUploadBase$SizeLimitExceededException
 *  org.apache.commons.fileupload.FileUploadException
 *  org.apache.commons.fileupload.RequestContext
 *  org.apache.commons.fileupload.disk.DiskFileItem
 *  org.apache.commons.fileupload.disk.DiskFileItemFactory
 *  org.apache.commons.fileupload.servlet.ServletFileUpload
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.dispatcher.multipart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileCountLimitExceededException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.LocalizedMessage;
import org.apache.struts2.dispatcher.multipart.AbstractMultiPartRequest;
import org.apache.struts2.dispatcher.multipart.StrutsUploadedFile;
import org.apache.struts2.dispatcher.multipart.UploadedFile;

public class JakartaMultiPartRequest
extends AbstractMultiPartRequest {
    static final Logger LOG = LogManager.getLogger(JakartaMultiPartRequest.class);
    protected Map<String, List<FileItem>> files = new HashMap<String, List<FileItem>>();
    protected Map<String, List<String>> params = new HashMap<String, List<String>>();

    @Override
    public void parse(HttpServletRequest request, String saveDir) throws IOException {
        block10: {
            try {
                this.setLocale(request);
                this.processUpload(request, saveDir);
            }
            catch (FileUploadException e) {
                LocalizedMessage errorMessage;
                LOG.debug("Request exceeded size limit!", (Throwable)e);
                if (e instanceof FileUploadBase.SizeLimitExceededException) {
                    FileUploadBase.SizeLimitExceededException ex = (FileUploadBase.SizeLimitExceededException)e;
                    errorMessage = this.buildErrorMessage(e, new Object[]{ex.getPermittedSize(), ex.getActualSize()});
                } else if (e instanceof FileUploadBase.FileSizeLimitExceededException) {
                    FileUploadBase.FileSizeLimitExceededException ex = (FileUploadBase.FileSizeLimitExceededException)e;
                    errorMessage = this.buildErrorMessage(e, new Object[]{ex.getFileName(), ex.getPermittedSize(), ex.getActualSize()});
                } else if (e instanceof FileCountLimitExceededException) {
                    FileCountLimitExceededException ex = (FileCountLimitExceededException)e;
                    errorMessage = this.buildErrorMessage(e, new Object[]{ex.getLimit()});
                } else {
                    errorMessage = this.buildErrorMessage(e, new Object[0]);
                }
                if (!this.errors.contains(errorMessage)) {
                    this.errors.add(errorMessage);
                }
            }
            catch (Exception e) {
                LOG.debug("Unable to parse request", (Throwable)e);
                LocalizedMessage errorMessage = this.buildErrorMessage(e, new Object[0]);
                if (this.errors.contains(errorMessage)) break block10;
                this.errors.add(errorMessage);
            }
        }
    }

    protected void processUpload(HttpServletRequest request, String saveDir) throws FileUploadException, UnsupportedEncodingException {
        if (ServletFileUpload.isMultipartContent((HttpServletRequest)request)) {
            for (FileItem item : this.parseRequest(request, saveDir)) {
                LOG.debug("Found file item: [{}]", (Object)this.sanitizeNewlines(item.getFieldName()));
                if (item.isFormField()) {
                    this.processNormalFormField(item, request.getCharacterEncoding());
                    continue;
                }
                this.processFileField(item);
            }
        }
    }

    protected void processFileField(FileItem item) {
        LOG.debug("Item is a file upload");
        if (item.getName() == null || item.getName().trim().isEmpty()) {
            LOG.debug("No file has been uploaded for the field: {}", (Object)this.sanitizeNewlines(item.getFieldName()));
            return;
        }
        List<FileItem> values = this.files.get(item.getFieldName()) != null ? this.files.get(item.getFieldName()) : new ArrayList<FileItem>();
        values.add(item);
        this.files.put(item.getFieldName(), values);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void processNormalFormField(FileItem item, String charset) throws UnsupportedEncodingException {
        try {
            LOG.debug("Item is a normal form field");
            List<Object> values = this.params.get(item.getFieldName()) != null ? this.params.get(item.getFieldName()) : new ArrayList();
            long size = item.getSize();
            if (size > this.maxStringLength) {
                LOG.debug("Form field {} of size {} bytes exceeds limit of {}.", (Object)this.sanitizeNewlines(item.getFieldName()), (Object)size, (Object)this.maxStringLength);
                String errorKey = "struts.messages.upload.error.parameter.too.long";
                LocalizedMessage localizedMessage = new LocalizedMessage(this.getClass(), errorKey, null, new Object[]{item.getFieldName(), this.maxStringLength, size});
                if (!this.errors.contains(localizedMessage)) {
                    this.errors.add(localizedMessage);
                }
                return;
            }
            if (size == 0L) {
                values.add("");
            } else if (charset == null) {
                values.add(item.getString());
            } else {
                values.add(item.getString(charset));
            }
            this.params.put(item.getFieldName(), values);
        }
        finally {
            item.delete();
        }
    }

    protected List<FileItem> parseRequest(HttpServletRequest servletRequest, String saveDir) throws FileUploadException {
        DiskFileItemFactory fac = this.createDiskFileItemFactory(saveDir);
        ServletFileUpload upload = this.createServletFileUpload(fac);
        return upload.parseRequest(this.createRequestContext(servletRequest));
    }

    protected ServletFileUpload createServletFileUpload(DiskFileItemFactory fac) {
        ServletFileUpload upload = new ServletFileUpload((FileItemFactory)fac);
        if (this.maxSize != null) {
            upload.setSizeMax(this.maxSize.longValue());
        }
        if (this.maxFiles != null) {
            upload.setFileCountMax(this.maxFiles.longValue());
        }
        if (this.maxFileSize != null) {
            upload.setFileSizeMax(this.maxFileSize.longValue());
        }
        return upload;
    }

    protected DiskFileItemFactory createDiskFileItemFactory(String saveDir) {
        DiskFileItemFactory fac = new DiskFileItemFactory();
        fac.setSizeThreshold(-1);
        if (saveDir != null) {
            fac.setRepository(new File(saveDir));
        }
        return fac;
    }

    @Override
    public Enumeration<String> getFileParameterNames() {
        return Collections.enumeration(this.files.keySet());
    }

    @Override
    public String[] getContentType(String fieldName) {
        List<FileItem> items = this.files.get(fieldName);
        if (items == null) {
            return null;
        }
        ArrayList<String> contentTypes = new ArrayList<String>(items.size());
        for (FileItem fileItem : items) {
            contentTypes.add(fileItem.getContentType());
        }
        return contentTypes.toArray(new String[0]);
    }

    @Override
    public UploadedFile[] getFile(String fieldName) {
        List<FileItem> items = this.files.get(fieldName);
        if (items == null) {
            return null;
        }
        ArrayList<UploadedFile> fileList = new ArrayList<UploadedFile>(items.size());
        for (FileItem fileItem : items) {
            DiskFileItem diskFileItem = (DiskFileItem)fileItem;
            File storeLocation = diskFileItem.getStoreLocation();
            if (diskFileItem.getSize() == 0L && storeLocation != null && !storeLocation.exists()) {
                try {
                    storeLocation.createNewFile();
                }
                catch (IOException e) {
                    LOG.error("Cannot write uploaded empty file to disk: {}", (Object)storeLocation.getAbsolutePath(), (Object)e);
                }
            }
            UploadedFile uploadedFile = StrutsUploadedFile.Builder.create(storeLocation).withContentType(fileItem.getContentType()).withOriginalName(fileItem.getName()).build();
            fileList.add(uploadedFile);
        }
        return fileList.toArray(new UploadedFile[0]);
    }

    @Override
    public String[] getFileNames(String fieldName) {
        List<FileItem> items = this.files.get(fieldName);
        if (items == null) {
            return null;
        }
        ArrayList<String> fileNames = new ArrayList<String>(items.size());
        for (FileItem fileItem : items) {
            fileNames.add(this.getCanonicalName(fileItem.getName()));
        }
        return fileNames.toArray(new String[0]);
    }

    @Override
    public String[] getFilesystemName(String fieldName) {
        List<FileItem> items = this.files.get(fieldName);
        if (items == null) {
            return null;
        }
        ArrayList<String> fileNames = new ArrayList<String>(items.size());
        for (FileItem fileItem : items) {
            fileNames.add(((DiskFileItem)fileItem).getStoreLocation().getName());
        }
        return fileNames.toArray(new String[0]);
    }

    @Override
    public String getParameter(String name) {
        List<String> v = this.params.get(name);
        if (v != null && !v.isEmpty()) {
            return v.get(0);
        }
        return null;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(this.params.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        List<String> v = this.params.get(name);
        if (v != null && !v.isEmpty()) {
            return v.toArray(new String[0]);
        }
        return null;
    }

    protected RequestContext createRequestContext(final HttpServletRequest req) {
        return new RequestContext(){

            public String getCharacterEncoding() {
                return req.getCharacterEncoding();
            }

            public String getContentType() {
                return req.getContentType();
            }

            public int getContentLength() {
                return req.getContentLength();
            }

            public InputStream getInputStream() throws IOException {
                ServletInputStream in = req.getInputStream();
                if (in == null) {
                    throw new IOException("Missing content in the request");
                }
                return req.getInputStream();
            }
        };
    }

    @Override
    public void cleanUp() {
        Set<String> names = this.files.keySet();
        for (String name : names) {
            List<FileItem> items = this.files.get(name);
            for (FileItem item : items) {
                LOG.debug("Removing file {} {}", (Object)name, (Object)item);
                if (item.isInMemory()) continue;
                item.delete();
            }
        }
    }

    private String sanitizeNewlines(String before) {
        return before.replaceAll("[\n\r]", "_");
    }
}

