/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.MimetypesFileTypeMap
 */
package com.atlassian.sal.api.net;

import java.io.File;
import javax.activation.MimetypesFileTypeMap;

public class RequestFilePart {
    private String contentType;
    private String fileName;
    private final File file;
    private final String parameterName;

    public RequestFilePart(String contentType, String fileName, File file, String parameterName) {
        this.contentType = contentType;
        this.fileName = fileName;
        this.file = file;
        this.parameterName = parameterName;
    }

    public RequestFilePart(File file, String parameterName) {
        this.file = file;
        this.parameterName = parameterName;
    }

    public String getFileName() {
        return this.fileName != null ? this.fileName : this.file.getName();
    }

    public String getContentType() {
        MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
        return this.contentType != null ? this.contentType : mimetypesFileTypeMap.getContentType(this.file);
    }

    public File getFile() {
        return this.file;
    }

    public String getParameterName() {
        return this.parameterName;
    }
}

