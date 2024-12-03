/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.model.evaluation;

public class ExportSettings {
    private String exportFormat;
    private boolean hideFixColumnHeaders;
    private String csvDelimiter;
    private String csvCustomDelimiter;
    private String spaceDetailsFormat;
    private String customSpaceDetailsFormat;

    public ExportSettings(String exportFormat, boolean hideFixColumnHeaders, String csvDelimiter, String csvCustomDelimiter, String spaceDetailsFormat, String customSpaceDetailsFormat) {
        this.exportFormat = exportFormat;
        this.hideFixColumnHeaders = hideFixColumnHeaders;
        this.csvDelimiter = csvDelimiter;
        this.csvCustomDelimiter = csvCustomDelimiter;
        this.spaceDetailsFormat = spaceDetailsFormat;
        this.customSpaceDetailsFormat = customSpaceDetailsFormat;
    }

    public String getExportFormat() {
        return this.exportFormat;
    }

    public boolean isHideFixColumnHeaders() {
        return this.hideFixColumnHeaders;
    }

    public String getCsvDelimiter() {
        return this.csvDelimiter;
    }

    public String getCsvCustomDelimiter() {
        return this.csvCustomDelimiter;
    }

    public String getSpaceDetailsFormat() {
        return this.spaceDetailsFormat;
    }

    public void setSpaceDetailsFormat(String spaceDetailsFormat) {
        this.spaceDetailsFormat = spaceDetailsFormat;
    }

    public String getCustomSpaceDetailsFormat() {
        return this.customSpaceDetailsFormat;
    }

    public String toString() {
        return "ExportSettings{exportFormat='" + this.exportFormat + "', hideFixColumnHeaders=" + this.hideFixColumnHeaders + ", csvDelimiter='" + this.csvDelimiter + "', csvCustomDelimiter='" + this.csvCustomDelimiter + "', spaceDetailsFormat='" + this.spaceDetailsFormat + "', customSpaceDetailsFormat='" + this.customSpaceDetailsFormat + "'}";
    }
}

