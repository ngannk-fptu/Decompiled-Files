/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.fileupload;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadBase;

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

