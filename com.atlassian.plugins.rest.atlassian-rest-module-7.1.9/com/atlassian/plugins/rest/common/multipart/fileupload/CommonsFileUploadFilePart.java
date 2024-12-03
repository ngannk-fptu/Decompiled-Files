/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.fileupload.FileItem
 *  org.apache.commons.fileupload.util.mime.MimeUtility
 */
package com.atlassian.plugins.rest.common.multipart.fileupload;

import com.atlassian.plugins.rest.common.multipart.FilePart;
import com.atlassian.plugins.rest.common.multipart.UnsupportedFileNameEncodingException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Objects;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.util.mime.MimeUtility;

public final class CommonsFileUploadFilePart
implements FilePart {
    private final FileItem fileItem;
    private final String name;

    CommonsFileUploadFilePart(FileItem fileItem) {
        this.fileItem = Objects.requireNonNull(fileItem);
        try {
            this.name = fileItem.getName() == null ? null : new File(MimeUtility.decodeText((String)fileItem.getName())).getName();
        }
        catch (UnsupportedEncodingException e) {
            throw new UnsupportedFileNameEncodingException(fileItem.getName());
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.fileItem.getInputStream();
    }

    @Override
    public String getContentType() {
        return this.fileItem.getContentType();
    }

    @Override
    public void write(File file) throws IOException {
        try {
            this.fileItem.write(file);
        }
        catch (Exception e) {
            if (e instanceof IOException) {
                throw (IOException)e;
            }
            throw new IOException(e);
        }
    }

    @Override
    public String getValue() {
        return this.fileItem.getString();
    }

    @Override
    public boolean isFormField() {
        return this.fileItem.isFormField();
    }

    @Override
    public long getSize() {
        return this.fileItem.getSize();
    }
}

