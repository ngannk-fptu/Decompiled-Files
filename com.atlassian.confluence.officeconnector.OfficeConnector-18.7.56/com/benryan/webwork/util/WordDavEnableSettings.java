/*
 * Decompiled with CFR 0.152.
 */
package com.benryan.webwork.util;

import java.io.Serializable;

public class WordDavEnableSettings
implements Cloneable,
Serializable {
    private boolean enableDocImport;
    private boolean enableEdit;
    private boolean enableViewFile;
    private int locationCode;

    public WordDavEnableSettings(boolean def) {
        this.enableDocImport = def;
        this.enableEdit = def;
        this.enableViewFile = def;
        this.locationCode = 2;
    }

    public boolean getEnableDocImport() {
        return this.enableDocImport;
    }

    public void setEnableDocImport(boolean enableDocImport) {
        this.enableDocImport = enableDocImport;
    }

    public boolean getEnableEdit() {
        return this.enableEdit;
    }

    public void setEnableEdit(boolean enableEdit) {
        this.enableEdit = enableEdit;
    }

    public boolean getEnableViewFile() {
        return this.enableViewFile;
    }

    public void setEnableViewFile(boolean enableViewFile) {
        this.enableViewFile = enableViewFile;
    }

    public int getLocationCode() {
        return this.locationCode;
    }

    public void setLocationCode(int locationCode) {
        this.locationCode = locationCode;
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            return null;
        }
    }
}

