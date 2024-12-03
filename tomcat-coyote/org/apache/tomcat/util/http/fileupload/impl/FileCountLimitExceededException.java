/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.fileupload.impl;

import org.apache.tomcat.util.http.fileupload.FileUploadException;

public class FileCountLimitExceededException
extends FileUploadException {
    private static final long serialVersionUID = 2408766352570556046L;
    private final long limit;

    public FileCountLimitExceededException(String message, long limit) {
        super(message);
        this.limit = limit;
    }

    public long getLimit() {
        return this.limit;
    }
}

