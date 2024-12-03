/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.fileupload.impl;

import org.apache.tomcat.util.http.fileupload.impl.SizeException;

public class FileSizeLimitExceededException
extends SizeException {
    private static final long serialVersionUID = 8150776562029630058L;
    private String fileName;
    private String fieldName;

    public FileSizeLimitExceededException(String message, long actual, long permitted) {
        super(message, actual, permitted);
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String pFileName) {
        this.fileName = pFileName;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(String pFieldName) {
        this.fieldName = pFieldName;
    }
}

