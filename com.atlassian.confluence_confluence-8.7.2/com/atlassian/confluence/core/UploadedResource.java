/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.FileUploadUtils$UploadedFile
 *  org.springframework.core.io.Resource
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.AttachmentResource;
import com.atlassian.xwork.FileUploadUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import org.springframework.core.io.Resource;

public class UploadedResource
implements AttachmentResource {
    private final File file;
    private final String filename;
    private final String comment;
    private final boolean minorEdit;
    private final boolean hidden;
    private final String contentType;

    public UploadedResource(FileUploadUtils.UploadedFile uploadedFile) {
        this(uploadedFile, null);
    }

    public UploadedResource(FileUploadUtils.UploadedFile uploadedFile, String comment) {
        this(uploadedFile, comment, false);
    }

    public UploadedResource(File file, String filename, String contentType, String comment) {
        this(file, filename, contentType, comment, false, false);
    }

    public UploadedResource(FileUploadUtils.UploadedFile uploadedFile, String comment, boolean minorEdit) {
        this(uploadedFile.getFile(), uploadedFile.getFileName(), uploadedFile.getContentType(), comment, minorEdit, false);
    }

    public UploadedResource(File file, String filename, String contentType, String comment, boolean minorEdit, boolean hidden) {
        this.file = file;
        this.filename = filename;
        this.contentType = contentType;
        this.comment = comment;
        this.minorEdit = minorEdit;
        this.hidden = hidden;
    }

    public String getDescription() {
        return "Uploaded file: " + this.filename;
    }

    public File getFile() {
        return this.file;
    }

    public String getFilename() throws IllegalStateException {
        return this.filename;
    }

    @Override
    public String getComment() {
        return this.comment;
    }

    public InputStream getInputStream() throws IOException {
        return new FileInputStream(this.getFile());
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public long getContentLength() {
        return this.contentLength();
    }

    public long contentLength() {
        return this.getFile().length();
    }

    @Override
    public boolean isMinorEdit() {
        return this.minorEdit;
    }

    @Override
    public boolean isHidden() {
        return this.hidden;
    }

    public boolean exists() {
        return this.getFile().exists();
    }

    public boolean isOpen() {
        return false;
    }

    public URL getURL() throws IOException {
        throw new UnsupportedOperationException("This is not supported.");
    }

    public Resource createRelative(String relativePath) throws IOException {
        throw new UnsupportedOperationException("This is not supported.");
    }

    public String toString() {
        return String.format("UploadedResource[filename: '%s', file: '%s']", this.filename, this.file.getAbsolutePath());
    }

    public boolean isReadable() {
        return this.getFile().canRead();
    }

    public URI getURI() throws IOException {
        return this.getFile().toURI();
    }

    public long lastModified() throws IOException {
        return this.getFile().lastModified();
    }
}

