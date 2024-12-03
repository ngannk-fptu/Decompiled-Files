/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.rpc.soap.beans;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.rpc.soap.beans.AbstractRemotePageSummary;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class RemoteBlogEntrySummary
extends AbstractRemotePageSummary {
    private Date publishDate;
    private String author;
    public static final String __PARANAMER_DATA = "<init> com.atlassian.confluence.pages.AbstractPage page \nequals java.lang.Object o \nsetAuthor java.lang.String author \nsetPublishDate java.util.Date publishDate \n";

    public RemoteBlogEntrySummary() {
    }

    public RemoteBlogEntrySummary(AbstractPage page) {
        super(page);
        if (page.getCreationDate() != null) {
            this.publishDate = new Date(page.getCreationDate().getTime());
        }
        this.author = page.getCreatorName();
    }

    public Date getPublishDate() {
        return this.publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate != null ? new Date(publishDate.getTime()) : null;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RemoteBlogEntrySummary)) {
            return false;
        }
        RemoteBlogEntrySummary remoteBlogEntrySummary = (RemoteBlogEntrySummary)o;
        if (this.author != null ? !this.author.equals(remoteBlogEntrySummary.author) : remoteBlogEntrySummary.author != null) {
            return false;
        }
        return !(this.publishDate != null ? !this.publishDate.equals(remoteBlogEntrySummary.publishDate) : remoteBlogEntrySummary.publishDate != null);
    }

    public int hashCode() {
        int result = this.publishDate != null ? this.publishDate.hashCode() : 0;
        result = 29 * result + (this.author != null ? this.author.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this);
    }
}

