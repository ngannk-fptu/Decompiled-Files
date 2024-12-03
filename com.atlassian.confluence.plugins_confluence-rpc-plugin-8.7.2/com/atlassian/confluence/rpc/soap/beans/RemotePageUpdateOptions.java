/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.rpc.soap.beans;

public class RemotePageUpdateOptions {
    private String versionComment;
    private boolean minorEdit;
    public static final String __PARANAMER_DATA = "equals java.lang.Object o \nsetMinorEdit boolean minorEdit \nsetVersionComment java.lang.String versionComment \n";

    public String getVersionComment() {
        return this.versionComment;
    }

    public void setVersionComment(String versionComment) {
        this.versionComment = versionComment;
    }

    public boolean isMinorEdit() {
        return this.minorEdit;
    }

    public void setMinorEdit(boolean minorEdit) {
        this.minorEdit = minorEdit;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RemotePageUpdateOptions that = (RemotePageUpdateOptions)o;
        if (this.minorEdit != that.minorEdit) {
            return false;
        }
        return !(this.versionComment != null ? !this.versionComment.equals(that.versionComment) : that.versionComment != null);
    }

    public int hashCode() {
        int result = this.versionComment != null ? this.versionComment.hashCode() : 0;
        result = 31 * result + (this.minorEdit ? 1 : 0);
        return result;
    }
}

