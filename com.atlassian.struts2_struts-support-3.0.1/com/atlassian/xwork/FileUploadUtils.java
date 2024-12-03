/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletRequestWrapper
 *  org.apache.struts2.ServletActionContext
 *  org.apache.struts2.dispatcher.LocalizedMessage
 *  org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper
 *  org.apache.struts2.dispatcher.multipart.UploadedFile
 */
package com.atlassian.xwork;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.LocalizedMessage;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;

public class FileUploadUtils {
    public static File getSingleFile() throws FileUploadException {
        UploadedFile uploadedFile = FileUploadUtils.getSingleUploadedFile();
        return uploadedFile == null ? null : uploadedFile.getFile();
    }

    public static UploadedFile getSingleUploadedFile() throws FileUploadException {
        List<UploadedFile> uploadedFiles = FileUploadUtils.getUploadedFiles();
        return uploadedFiles.isEmpty() ? null : uploadedFiles.get(0);
    }

    public static List<UploadedFile> getUploadedFiles() throws FileUploadException {
        return FileUploadUtils.getUploadedFiles(FileUploadUtils.unwrapMultiPartRequest(ServletActionContext.getRequest()), true);
    }

    public static UploadedFile[] handleFileUpload(MultiPartRequestWrapper multiWrapper, boolean clean) throws FileUploadException {
        return FileUploadUtils.getUploadedFiles(multiWrapper, clean).toArray(new UploadedFile[0]);
    }

    public static List<UploadedFile> getUploadedFiles(MultiPartRequestWrapper multiWrapper) throws FileUploadException {
        return FileUploadUtils.getUploadedFiles(multiWrapper, true);
    }

    public static List<UploadedFile> getUploadedFiles(MultiPartRequestWrapper multiWrapper, boolean clean) throws FileUploadException {
        FileUploadUtils.checkMultiPartRequestForErrors(multiWrapper);
        Enumeration e = multiWrapper.getFileParameterNames();
        ArrayList<UploadedFile> uploadedFiles = new ArrayList<UploadedFile>();
        while (e.hasMoreElements()) {
            String inputValue = (String)e.nextElement();
            org.apache.struts2.dispatcher.multipart.UploadedFile[] files = multiWrapper.getFiles(inputValue);
            for (int i = 0; i < files.length; ++i) {
                org.apache.struts2.dispatcher.multipart.UploadedFile file = files[i];
                if (file == null) {
                    if (clean) continue;
                    FileUploadException fileUploadException = new FileUploadException();
                    fileUploadException.addError(new LocalizedMessage(FileUploadUtils.class, "struts.messages.error.uploading", "Error uploading " + multiWrapper.getFileSystemNames(inputValue)[i], new Object[]{multiWrapper.getFileSystemNames(inputValue)[i]}));
                    throw fileUploadException;
                }
                UploadedFile uploadedFile = new UploadedFile(new File(file.getAbsolutePath()), multiWrapper.getFileNames(inputValue)[i], multiWrapper.getContentTypes(inputValue)[i]);
                uploadedFiles.add(uploadedFile);
            }
        }
        return uploadedFiles;
    }

    public static MultiPartRequestWrapper unwrapMultiPartRequest(HttpServletRequest request) {
        HttpServletRequest servletRequest = request;
        while (servletRequest instanceof HttpServletRequestWrapper) {
            if (servletRequest instanceof MultiPartRequestWrapper) {
                return (MultiPartRequestWrapper)servletRequest;
            }
            servletRequest = ((HttpServletRequestWrapper)servletRequest).getRequest();
        }
        return null;
    }

    public static void checkMultiPartRequestForErrors(MultiPartRequestWrapper multiWrapper) throws FileUploadException {
        if (!multiWrapper.hasErrors()) {
            return;
        }
        FileUploadException fileUploadException = new FileUploadException();
        multiWrapper.getErrors().forEach(fileUploadException::addError);
        throw fileUploadException;
    }

    public static final class FileUploadException
    extends Exception {
        private final List<LocalizedMessage> errors = new ArrayList<LocalizedMessage>();

        public void addError(LocalizedMessage error) {
            this.errors.add(error);
        }

        @Deprecated
        public String[] getErrors() {
            return (String[])this.errors.stream().map(LocalizedMessage::getDefaultMessage).toArray(String[]::new);
        }

        public List<LocalizedMessage> getErrorMsgs() {
            return Collections.unmodifiableList(this.errors);
        }

        @Override
        public String getMessage() {
            return this.errors.stream().map(LocalizedMessage::getDefaultMessage).collect(Collectors.joining(", "));
        }
    }

    public static final class UploadedFile {
        private final File file;
        private final String fileName;
        private final String contentType;

        public UploadedFile(File file, String fileName, String contentType) {
            this.file = file;
            this.fileName = fileName;
            this.contentType = contentType;
        }

        public File getFile() {
            return this.file;
        }

        public String getFileName() {
            return this.fileName;
        }

        public String getContentType() {
            return this.contentType;
        }
    }
}

