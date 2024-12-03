/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.fileupload;

import java.io.File;
import org.apache.commons.fileupload.DefaultFileItem;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

@Deprecated
public class DefaultFileItemFactory
extends DiskFileItemFactory {
    @Deprecated
    public DefaultFileItemFactory() {
    }

    @Deprecated
    public DefaultFileItemFactory(int sizeThreshold, File repository) {
        super(sizeThreshold, repository);
    }

    @Override
    @Deprecated
    public FileItem createItem(String fieldName, String contentType, boolean isFormField, String fileName) {
        return new DefaultFileItem(fieldName, contentType, isFormField, fileName, this.getSizeThreshold(), this.getRepository());
    }
}

