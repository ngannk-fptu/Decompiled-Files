/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.fileupload;

import java.io.File;
import org.apache.commons.fileupload.disk.DiskFileItem;

@Deprecated
public class DefaultFileItem
extends DiskFileItem {
    @Deprecated
    public DefaultFileItem(String fieldName, String contentType, boolean isFormField, String fileName, int sizeThreshold, File repository) {
        super(fieldName, contentType, isFormField, fileName, sizeThreshold, repository);
    }
}

