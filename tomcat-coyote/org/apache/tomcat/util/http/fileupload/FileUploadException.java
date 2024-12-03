/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.fileupload;

import java.io.IOException;

public class FileUploadException
extends IOException {
    private static final long serialVersionUID = -4222909057964038517L;

    public FileUploadException() {
    }

    public FileUploadException(String msg) {
        super(msg);
    }

    public FileUploadException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public FileUploadException(Throwable cause) {
        super(cause);
    }
}

