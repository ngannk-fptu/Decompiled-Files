/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.springframework.core.io.Resource
 *  org.springframework.util.Assert
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.AttachmentResource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

public class InputStreamAttachmentResource
implements AttachmentResource {
    private final InputStream inputStream;
    private final String filename;
    private final String contentType;
    private final long contentLength;
    private final String comment;
    private final boolean minorEdit;
    private final boolean hidden;

    public InputStreamAttachmentResource(InputStream inputStream, String filename, String contentType, long contentLength) {
        this(inputStream, filename, contentType, contentLength, null);
    }

    public InputStreamAttachmentResource(InputStream inputStream, String filename, String contentType, long contentLength, String comment) {
        this(inputStream, filename, contentType, contentLength, comment, false);
    }

    public InputStreamAttachmentResource(InputStream inputStream, String filename, String contentType, long contentLength, String comment, boolean minorEdit) {
        this(inputStream, filename, contentType, contentLength, comment, minorEdit, false);
    }

    public InputStreamAttachmentResource(InputStream inputStream, String filename, String contentType, long contentLength, String comment, boolean minorEdit, boolean hidden) {
        Assert.notNull((Object)inputStream, (String)"Input stream cannot be null.");
        Assert.hasLength((String)filename, (String)"File name cannot be null or empty.");
        Assert.hasLength((String)contentType, (String)"Content type cannot be null or empty.");
        Assert.state((contentLength > 0L ? 1 : 0) != 0, (String)"Length of stream must be greater than 0.");
        this.inputStream = inputStream;
        this.filename = filename;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.comment = comment;
        this.minorEdit = minorEdit;
        this.hidden = hidden;
    }

    public String getDescription() {
        return "Attachment input stream resource.";
    }

    public String getFilename() {
        return this.filename;
    }

    public InputStream getInputStream() {
        return this.inputStream;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public long getContentLength() {
        return this.contentLength;
    }

    public long contentLength() {
        return this.contentLength;
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
        return false;
    }

    public boolean isOpen() {
        return false;
    }

    @Override
    public String getComment() {
        return this.comment;
    }

    public URL getURL() throws IOException {
        throw new UnsupportedOperationException("This is not supported.");
    }

    public File getFile() throws IOException {
        throw new UnsupportedOperationException("This is not supported.");
    }

    public Resource createRelative(String relativePath) throws IOException {
        throw new UnsupportedOperationException("This is not supported.");
    }

    public boolean isReadable() {
        return true;
    }

    public URI getURI() throws IOException {
        throw new UnsupportedOperationException("This is not supported.");
    }

    public long lastModified() throws IOException {
        return System.currentTimeMillis();
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append((Object)this.filename).append((Object)this.contentType).append(this.contentLength).toString();
    }
}

