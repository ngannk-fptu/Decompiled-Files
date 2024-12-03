/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.fileupload.impl;

import java.io.IOException;
import org.apache.tomcat.util.http.fileupload.FileUploadException;

public class FileUploadIOException
extends IOException {
    private static final long serialVersionUID = -7047616958165584154L;
    private final FileUploadException cause;

    public FileUploadIOException(FileUploadException pCause) {
        this.cause = pCause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

