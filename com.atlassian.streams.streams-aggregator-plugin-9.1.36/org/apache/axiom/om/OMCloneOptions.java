/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om;

public class OMCloneOptions {
    private boolean fetchDataHandlers;
    private boolean copyOMDataSources;
    private boolean preserveModel;

    public boolean isFetchDataHandlers() {
        return this.fetchDataHandlers;
    }

    public void setFetchDataHandlers(boolean fetchDataHandlers) {
        this.fetchDataHandlers = fetchDataHandlers;
    }

    public boolean isCopyOMDataSources() {
        return this.copyOMDataSources;
    }

    public void setCopyOMDataSources(boolean copyOMDataSources) {
        this.copyOMDataSources = copyOMDataSources;
    }

    public boolean isPreserveModel() {
        return this.preserveModel;
    }

    public void setPreserveModel(boolean preserveModel) {
        this.preserveModel = preserveModel;
    }
}

