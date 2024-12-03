/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.BlogPost
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.rpc.soap.beans;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.rpc.soap.beans.RemoteBlogEntrySummary;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class RemoteBlogEntry
extends RemoteBlogEntrySummary {
    private String content;
    private int version;
    public static final String __PARANAMER_DATA = "<init> com.atlassian.confluence.pages.BlogPost blogPost \nequals java.lang.Object o \nsetContent java.lang.String content \nsetVersion int version \n";

    public RemoteBlogEntry() {
    }

    public RemoteBlogEntry(BlogPost blogPost) {
        super((AbstractPage)blogPost);
        this.content = blogPost.getBodyAsString();
        this.version = blogPost.getVersion();
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RemoteBlogEntry)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        RemoteBlogEntry remoteBlogEntry = (RemoteBlogEntry)o;
        return !(this.content != null ? !this.content.equals(remoteBlogEntry.content) : remoteBlogEntry.content != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (this.content != null ? this.content.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this);
    }
}

