/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.VersionHistorySummary
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.rpc.soap.beans;

import com.atlassian.confluence.core.VersionHistorySummary;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class RemotePageHistory {
    private long id;
    private int version;
    private String modifier;
    private Date modified;
    public static final String __PARANAMER_DATA = "<init> com.atlassian.confluence.core.VersionHistorySummary page \nequals java.lang.Object o \nsetId long id \nsetModified java.util.Date modified \nsetModifier java.lang.String modifier \nsetVersion int version \n";

    public RemotePageHistory() {
    }

    public RemotePageHistory(VersionHistorySummary page) {
        this.id = page.getId();
        this.version = page.getVersion();
        this.modifier = page.getLastModifierName();
        this.modified = new Date(page.getLastModificationDate().getTime());
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getModifier() {
        return this.modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Date getModified() {
        return this.modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RemotePageHistory)) {
            return false;
        }
        RemotePageHistory remotePageHistory = (RemotePageHistory)o;
        if (this.id != remotePageHistory.id) {
            return false;
        }
        if (this.version != remotePageHistory.version) {
            return false;
        }
        if (this.modified != null ? !this.modified.equals(remotePageHistory.modified) : remotePageHistory.modified != null) {
            return false;
        }
        return !(this.modifier != null ? !this.modifier.equals(remotePageHistory.modifier) : remotePageHistory.modifier != null);
    }

    public int hashCode() {
        int result = (int)(this.id ^ this.id >>> 32);
        result = 29 * result + this.version;
        result = 29 * result + (this.modifier != null ? this.modifier.hashCode() : 0);
        result = 29 * result + (this.modified != null ? this.modified.hashCode() : 0);
        return result;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this);
    }
}

