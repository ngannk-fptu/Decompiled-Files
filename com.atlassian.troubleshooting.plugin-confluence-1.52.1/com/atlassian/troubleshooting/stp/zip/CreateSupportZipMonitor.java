/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.troubleshooting.stp.zip;

import com.atlassian.troubleshooting.stp.task.DefaultTaskMonitor;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.apache.commons.lang3.StringUtils;

public class CreateSupportZipMonitor
extends DefaultTaskMonitor<File> {
    public static final String ZIP_FILE_NAME = "zipFileName";
    public static final String TRUNCATED_FILES = "truncatedFiles";
    public static final String AGE_EXCLUDED_FILES = "ageExcludedFiles";
    private static final long serialVersionUID = 6792945444381648904L;
    private ArrayList<String> truncatedFiles = new ArrayList();
    private ArrayList<String> ageExcludedFiles = new ArrayList();
    private String zipFileName;

    public void setZipFileName(String fileName) {
        this.zipFileName = fileName;
    }

    @Override
    public Map<String, Serializable> getAttributes() {
        Map<String, Serializable> attributes = super.getAttributes();
        if (!this.isCancelled() && this.isDone()) {
            try {
                attributes.put(ZIP_FILE_NAME, (Serializable)((Object)((File)this.get()).getName()));
            }
            catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Error getting zip file name", e);
            }
        }
        return attributes;
    }

    @Override
    protected void addCustomAttributes(Map<String, Serializable> attributesToUpdate) {
        if (StringUtils.isNotBlank((CharSequence)this.zipFileName)) {
            attributesToUpdate.put(ZIP_FILE_NAME, (Serializable)((Object)this.zipFileName));
        }
        attributesToUpdate.put(TRUNCATED_FILES, this.truncatedFiles);
        attributesToUpdate.put(AGE_EXCLUDED_FILES, this.ageExcludedFiles);
    }

    @Override
    public void setCustomAttributes(Map<String, Serializable> attributesToRead) {
        this.zipFileName = (String)((Object)attributesToRead.get(ZIP_FILE_NAME));
        this.truncatedFiles = (ArrayList)attributesToRead.get(TRUNCATED_FILES);
        this.ageExcludedFiles = (ArrayList)attributesToRead.get(AGE_EXCLUDED_FILES);
    }

    public void addTruncatedFile(String fileName) {
        this.truncatedFiles.add(fileName);
    }

    public void addAgeExcludedFile(String fileName) {
        this.ageExcludedFiles.add(fileName);
    }

    public List<String> getTruncatedFiles() {
        return this.truncatedFiles;
    }

    public List<String> getAgeExcludedFiles() {
        return this.ageExcludedFiles;
    }
}

