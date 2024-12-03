/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.dispatcher.multipart;

import java.io.File;
import org.apache.struts2.dispatcher.multipart.UploadedFile;

public class StrutsUploadedFile
implements UploadedFile {
    private final File file;
    private final String contentType;
    private final String originalName;

    @Deprecated
    public StrutsUploadedFile(File file) {
        this.file = file;
        this.contentType = null;
        this.originalName = null;
    }

    private StrutsUploadedFile(File file, String contentType, String originalName) {
        this.file = file;
        this.contentType = contentType;
        this.originalName = originalName;
    }

    @Override
    public Long length() {
        return this.file.length();
    }

    @Override
    public String getName() {
        return this.file.getName();
    }

    @Override
    public boolean isFile() {
        return this.file.isFile();
    }

    @Override
    public boolean delete() {
        return this.file.delete();
    }

    @Override
    public String getAbsolutePath() {
        return this.file.getAbsolutePath();
    }

    @Override
    public File getContent() {
        return this.file;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public String getOriginalName() {
        return this.originalName;
    }

    public String toString() {
        return "StrutsUploadedFile{contentType='" + this.contentType + '\'' + ", originalName='" + this.originalName + '\'' + '}';
    }

    public static class Builder {
        private final File file;
        private String contentType;
        private String originalName;

        private Builder(File file) {
            this.file = file;
        }

        public static Builder create(File file) {
            return new Builder(file);
        }

        public Builder withContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder withOriginalName(String originalName) {
            this.originalName = originalName;
            return this;
        }

        public UploadedFile build() {
            return new StrutsUploadedFile(this.file, this.contentType, this.originalName);
        }
    }
}

