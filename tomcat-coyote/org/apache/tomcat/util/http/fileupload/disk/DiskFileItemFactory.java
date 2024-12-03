/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.fileupload.disk;

import java.io.File;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemFactory;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;

public class DiskFileItemFactory
implements FileItemFactory {
    public static final int DEFAULT_SIZE_THRESHOLD = 10240;
    private File repository;
    private int sizeThreshold = 10240;
    private String defaultCharset = "ISO-8859-1";

    public DiskFileItemFactory() {
        this(10240, null);
    }

    public DiskFileItemFactory(int sizeThreshold, File repository) {
        this.sizeThreshold = sizeThreshold;
        this.repository = repository;
    }

    public File getRepository() {
        return this.repository;
    }

    public void setRepository(File repository) {
        this.repository = repository;
    }

    public int getSizeThreshold() {
        return this.sizeThreshold;
    }

    public void setSizeThreshold(int sizeThreshold) {
        this.sizeThreshold = sizeThreshold;
    }

    @Override
    public FileItem createItem(String fieldName, String contentType, boolean isFormField, String fileName) {
        DiskFileItem result = new DiskFileItem(fieldName, contentType, isFormField, fileName, this.sizeThreshold, this.repository);
        result.setDefaultCharset(this.defaultCharset);
        return result;
    }

    public String getDefaultCharset() {
        return this.defaultCharset;
    }

    public void setDefaultCharset(String pCharset) {
        this.defaultCharset = pCharset;
    }
}

