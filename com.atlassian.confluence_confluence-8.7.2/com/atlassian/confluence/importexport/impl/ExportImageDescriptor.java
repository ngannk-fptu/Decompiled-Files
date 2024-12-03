/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.impl;

public class ExportImageDescriptor {
    private String imagePath;
    private String exportPath;

    public ExportImageDescriptor(String imagePath, String exportPath) {
        this.imagePath = imagePath;
        this.exportPath = exportPath;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public String getExportPath() {
        return this.exportPath;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ExportImageDescriptor that = (ExportImageDescriptor)o;
        return this.exportPath.equals(that.exportPath);
    }

    public int hashCode() {
        return this.exportPath.hashCode();
    }
}

