/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Page
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.rpc.soap.beans;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.rpc.soap.beans.AbstractRemotePageSummary;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class RemotePageSummary
extends AbstractRemotePageSummary {
    long parentId;
    int version;
    public static final String __PARANAMER_DATA = "<init> com.atlassian.confluence.pages.Page page \nequals java.lang.Object o \nsetParentId long parentId \nsetVersion int version \n";

    public RemotePageSummary() {
    }

    public RemotePageSummary(Page page) {
        super((AbstractPage)page);
        if (page.getParent() != null) {
            this.parentId = page.getParent().getId();
        }
        this.version = page.getVersion();
    }

    public long getParentId() {
        return this.parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RemotePageSummary)) {
            return false;
        }
        RemotePageSummary remotePageSummary = (RemotePageSummary)o;
        if (this.id != remotePageSummary.id) {
            return false;
        }
        if (this.parentId != remotePageSummary.parentId) {
            return false;
        }
        if (this.version != remotePageSummary.version) {
            return false;
        }
        if (this.space != null ? !this.space.equals(remotePageSummary.space) : remotePageSummary.space != null) {
            return false;
        }
        if (this.title != null ? !this.title.equals(remotePageSummary.title) : remotePageSummary.title != null) {
            return false;
        }
        return !(this.url != null ? !this.url.equals(remotePageSummary.url) : remotePageSummary.url != null);
    }

    public int hashCode() {
        int result = (int)(this.id ^ this.id >>> 32);
        result = 29 * result + (int)(this.parentId ^ this.parentId >>> 32);
        result = 29 * result + this.version;
        result = 29 * result + (this.space != null ? this.space.hashCode() : 0);
        result = 29 * result + (this.title != null ? this.title.hashCode() : 0);
        result = 29 * result + (this.url != null ? this.url.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this);
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}

