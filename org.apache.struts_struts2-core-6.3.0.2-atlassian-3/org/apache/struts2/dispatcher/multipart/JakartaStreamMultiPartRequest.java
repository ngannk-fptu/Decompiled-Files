/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.fileupload.FileItemIterator
 *  org.apache.commons.fileupload.FileItemStream
 *  org.apache.commons.fileupload.FileUploadBase$FileSizeLimitExceededException
 *  org.apache.commons.fileupload.FileUploadException
 *  org.apache.commons.fileupload.servlet.ServletFileUpload
 *  org.apache.commons.fileupload.util.Streams
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.dispatcher.multipart;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.LocalizedMessage;
import org.apache.struts2.dispatcher.multipart.AbstractMultiPartRequest;
import org.apache.struts2.dispatcher.multipart.StrutsUploadedFile;
import org.apache.struts2.dispatcher.multipart.UploadedFile;

public class JakartaStreamMultiPartRequest
extends AbstractMultiPartRequest {
    static final Logger LOG = LogManager.getLogger(JakartaStreamMultiPartRequest.class);
    protected Map<String, List<FileInfo>> fileInfos = new HashMap<String, List<FileInfo>>();
    protected Map<String, List<String>> parameters = new HashMap<String, List<String>>();

    @Override
    public void cleanUp() {
        LOG.debug("Performing File Upload temporary storage cleanup.");
        for (List<FileInfo> fileInfoList : this.fileInfos.values()) {
            for (FileInfo fileInfo : fileInfoList) {
                File file = fileInfo.getFile();
                LOG.debug("Deleting file '{}'.", (Object)file.getName());
                if (file.delete()) continue;
                LOG.warn("There was a problem attempting to delete file '{}'.", (Object)file.getName());
            }
        }
    }

    @Override
    public String[] getContentType(String fieldName) {
        List<FileInfo> infos = this.fileInfos.get(fieldName);
        if (infos == null) {
            return null;
        }
        ArrayList<String> types = new ArrayList<String>(infos.size());
        for (FileInfo fileInfo : infos) {
            types.add(fileInfo.getContentType());
        }
        return types.toArray(new String[0]);
    }

    @Override
    public UploadedFile[] getFile(String fieldName) {
        List<FileInfo> infos = this.fileInfos.get(fieldName);
        if (infos == null) {
            return null;
        }
        return (UploadedFile[])infos.stream().map(fileInfo -> StrutsUploadedFile.Builder.create(fileInfo.getFile()).withContentType(((FileInfo)fileInfo).contentType).withOriginalName(((FileInfo)fileInfo).originalName).build()).toArray(UploadedFile[]::new);
    }

    @Override
    public String[] getFileNames(String fieldName) {
        List<FileInfo> infos = this.fileInfos.get(fieldName);
        if (infos == null) {
            return null;
        }
        ArrayList<String> names = new ArrayList<String>(infos.size());
        for (FileInfo fileInfo : infos) {
            names.add(this.getCanonicalName(fileInfo.getOriginalName()));
        }
        return names.toArray(new String[0]);
    }

    @Override
    public Enumeration<String> getFileParameterNames() {
        return Collections.enumeration(this.fileInfos.keySet());
    }

    @Override
    public String[] getFilesystemName(String fieldName) {
        List<FileInfo> infos = this.fileInfos.get(fieldName);
        if (infos == null) {
            return null;
        }
        ArrayList<String> names = new ArrayList<String>(infos.size());
        for (FileInfo fileInfo : infos) {
            names.add(fileInfo.getFile().getName());
        }
        return names.toArray(new String[0]);
    }

    @Override
    public String getParameter(String name) {
        List<String> values = this.parameters.get(name);
        if (values != null && !values.isEmpty()) {
            return values.get(0);
        }
        return null;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(this.parameters.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        List<String> values = this.parameters.get(name);
        if (values != null && !values.isEmpty()) {
            return values.toArray(new String[0]);
        }
        return null;
    }

    @Override
    public void parse(HttpServletRequest request, String saveDir) throws IOException {
        block2: {
            try {
                this.setLocale(request);
                this.processUpload(request, saveDir);
            }
            catch (Exception e) {
                LOG.debug("Error occurred during parsing of multi part request", (Throwable)e);
                LocalizedMessage errorMessage = this.buildErrorMessage(e, new Object[0]);
                if (this.errors.contains(errorMessage)) break block2;
                this.errors.add(errorMessage);
            }
        }
    }

    protected void processUpload(HttpServletRequest request, String saveDir) throws IOException, FileUploadException {
        if (ServletFileUpload.isMultipartContent((HttpServletRequest)request)) {
            boolean requestSizePermitted = this.isRequestSizePermitted(request);
            ServletFileUpload servletFileUpload = new ServletFileUpload();
            if (this.maxSize != null) {
                servletFileUpload.setSizeMax(this.maxSize.longValue());
            }
            if (this.maxFiles != null) {
                servletFileUpload.setFileCountMax(this.maxFiles.longValue());
            }
            if (this.maxFileSize != null) {
                servletFileUpload.setFileSizeMax(this.maxFileSize.longValue());
            }
            FileItemIterator i = servletFileUpload.getItemIterator(request);
            while (i.hasNext()) {
                try {
                    FileItemStream itemStream = i.next();
                    if (itemStream.isFormField()) {
                        this.processFileItemStreamAsFormField(itemStream);
                        continue;
                    }
                    if (!requestSizePermitted) {
                        this.addFileSkippedError(itemStream.getName(), request);
                        LOG.debug("Skipped stream '{}', request maximum size ({}) exceeded.", (Object)itemStream.getName(), (Object)this.maxSize);
                        continue;
                    }
                    this.processFileItemStreamAsFileField(itemStream, saveDir);
                }
                catch (IOException e) {
                    LOG.warn("Error occurred during process upload", (Throwable)e);
                }
            }
        }
    }

    protected boolean isRequestSizePermitted(HttpServletRequest request) {
        if (this.maxSize == null || this.maxSize == -1L || request == null) {
            return true;
        }
        return (long)request.getContentLength() < this.maxSize;
    }

    protected long getRequestSize(HttpServletRequest request) {
        return request != null ? (long)request.getContentLength() : 0L;
    }

    protected void addFileSkippedError(String fileName, HttpServletRequest request) {
        String exceptionMessage = "Skipped file " + fileName + "; request size limit exceeded.";
        long allowedMaxSize = this.maxSize != null ? this.maxSize : -1L;
        FileUploadBase.FileSizeLimitExceededException exception = new FileUploadBase.FileSizeLimitExceededException(exceptionMessage, this.getRequestSize(request), allowedMaxSize);
        LocalizedMessage message = this.buildErrorMessage((Throwable)exception, new Object[]{fileName, this.getRequestSize(request), allowedMaxSize});
        if (!this.errors.contains(message)) {
            this.errors.add(message);
        }
    }

    protected void processFileItemStreamAsFormField(FileItemStream itemStream) {
        String fieldName = itemStream.getFieldName();
        try {
            List<Object> values;
            String fieldValue = Streams.asString((InputStream)itemStream.openStream());
            if (!this.parameters.containsKey(fieldName)) {
                values = new ArrayList();
                this.parameters.put(fieldName, values);
            } else {
                values = this.parameters.get(fieldName);
            }
            values.add(fieldValue);
        }
        catch (IOException e) {
            LOG.warn("Failed to handle form field '{}'.", (Object)fieldName, (Object)e);
        }
    }

    protected void processFileItemStreamAsFileField(FileItemStream itemStream, String location) {
        block6: {
            if (itemStream.getName() == null || itemStream.getName().trim().isEmpty()) {
                LOG.debug("No file has been uploaded for the field: {}", (Object)itemStream.getFieldName());
                return;
            }
            File file = null;
            try {
                file = this.createTemporaryFile(itemStream.getName(), location);
                if (this.streamFileToDisk(itemStream, file)) {
                    this.createFileInfoFromItemStream(itemStream, file);
                }
            }
            catch (IOException e) {
                if (file == null) break block6;
                try {
                    file.delete();
                }
                catch (SecurityException se) {
                    LOG.warn("Failed to delete '{}' due to security exception above.", (Object)file.getName(), (Object)se);
                }
            }
        }
    }

    protected File createTemporaryFile(String fileName, String location) throws IOException {
        String name;
        String prefix = name = fileName.substring(fileName.lastIndexOf(47) + 1).substring(fileName.lastIndexOf(92) + 1);
        String suffix = "";
        if (name.contains(".")) {
            prefix = name.substring(0, name.lastIndexOf(46));
            suffix = name.substring(name.lastIndexOf(46));
        }
        if (prefix.length() < 3) {
            prefix = UUID.randomUUID().toString();
        }
        File file = File.createTempFile(prefix + "_", suffix, new File(location));
        LOG.debug("Creating temporary file '{}' (originally '{}').", (Object)file.getName(), (Object)fileName);
        return file;
    }

    protected boolean streamFileToDisk(FileItemStream itemStream, File file) throws IOException {
        boolean result;
        try (InputStream input = itemStream.openStream();
             BufferedOutputStream output = new BufferedOutputStream(Files.newOutputStream(file.toPath(), new OpenOption[0]), this.bufferSize);){
            int length;
            byte[] buffer = new byte[this.bufferSize];
            LOG.debug("Streaming file using buffer size {}.", (Object)this.bufferSize);
            while ((length = input.read(buffer)) > 0) {
                ((OutputStream)output).write(buffer, 0, length);
            }
            result = true;
        }
        return result;
    }

    protected void createFileInfoFromItemStream(FileItemStream itemStream, File file) {
        String fileName = itemStream.getName();
        String fieldName = itemStream.getFieldName();
        FileInfo fileInfo = new FileInfo(file, itemStream.getContentType(), fileName);
        if (!this.fileInfos.containsKey(fieldName)) {
            ArrayList<FileInfo> infos = new ArrayList<FileInfo>();
            infos.add(fileInfo);
            this.fileInfos.put(fieldName, infos);
        } else {
            this.fileInfos.get(fieldName).add(fileInfo);
        }
    }

    public static class FileInfo
    implements Serializable {
        private static final long serialVersionUID = 1083158552766906037L;
        private final File file;
        private final String contentType;
        private final String originalName;

        public FileInfo(File file, String contentType, String originalName) {
            this.file = file;
            this.contentType = contentType;
            this.originalName = originalName;
        }

        public File getFile() {
            return this.file;
        }

        public String getContentType() {
            return this.contentType;
        }

        public String getOriginalName() {
            return this.originalName;
        }
    }
}

