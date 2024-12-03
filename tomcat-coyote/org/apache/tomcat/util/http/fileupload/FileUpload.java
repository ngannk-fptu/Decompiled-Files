/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.fileupload;

import org.apache.tomcat.util.http.fileupload.FileItemFactory;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;

public class FileUpload
extends FileUploadBase {
    private FileItemFactory fileItemFactory;

    public FileUpload() {
    }

    public FileUpload(FileItemFactory fileItemFactory) {
        this.fileItemFactory = fileItemFactory;
    }

    @Override
    public FileItemFactory getFileItemFactory() {
        return this.fileItemFactory;
    }

    @Override
    public void setFileItemFactory(FileItemFactory factory) {
        this.fileItemFactory = factory;
    }
}

