/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.fileupload;

import org.apache.commons.fileupload.FileUploadException;

public class FileCountLimitExceededException
extends FileUploadException {
    private static final long serialVersionUID = 6904179610227521789L;
    private final long limit;

    public FileCountLimitExceededException(String message, long limit) {
        super(message);
        this.limit = limit;
    }

    public long getLimit() {
        return this.limit;
    }
}

