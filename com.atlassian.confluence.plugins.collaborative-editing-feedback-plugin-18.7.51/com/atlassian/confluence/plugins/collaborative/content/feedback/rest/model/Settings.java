/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.rest.model;

import java.io.Serializable;
import java.util.Objects;

public class Settings
implements Serializable {
    private String destinationFolder;
    private int maxFiles;
    private long maxConcurrentRequests;
    private long operationTimeout;
    private boolean editorReportsEnabled;
    private int retention;

    public Settings() {
    }

    public Settings(String destinationFolder, int maxFiles, long maxConcurrentRequests, long operationTimeout, boolean editorReportsEnabled, int retention) {
        this.destinationFolder = Objects.requireNonNull(destinationFolder);
        this.maxFiles = maxFiles;
        this.maxConcurrentRequests = maxConcurrentRequests;
        this.operationTimeout = operationTimeout;
        this.editorReportsEnabled = editorReportsEnabled;
        this.retention = retention;
    }

    public String getDestinationFolder() {
        return this.destinationFolder;
    }

    public int getMaxFiles() {
        return this.maxFiles;
    }

    public long getMaxConcurrentRequests() {
        return this.maxConcurrentRequests;
    }

    public long getOperationTimeout() {
        return this.operationTimeout;
    }

    public boolean isEditorReportsEnabled() {
        return this.editorReportsEnabled;
    }

    public int getRetention() {
        return this.retention;
    }

    public void setDestinationFolder(String destinationFolder) {
        this.destinationFolder = destinationFolder;
    }

    public void setMaxFiles(int maxFiles) {
        this.maxFiles = maxFiles;
    }

    public void setMaxConcurrentRequests(long maxConcurrentRequests) {
        this.maxConcurrentRequests = maxConcurrentRequests;
    }

    public void setOperationTimeout(long operationTimeout) {
        this.operationTimeout = operationTimeout;
    }

    public void setEditorReportsEnabled(boolean editorReportsEnabled) {
        this.editorReportsEnabled = editorReportsEnabled;
    }

    public void setRetention(int retention) {
        this.retention = retention;
    }
}

