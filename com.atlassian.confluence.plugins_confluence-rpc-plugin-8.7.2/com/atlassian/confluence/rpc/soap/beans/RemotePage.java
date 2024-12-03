/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Page
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.rpc.soap.beans;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.rpc.soap.beans.RemotePageSummary;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class RemotePage
extends RemotePageSummary {
    Date created;
    Date modified;
    String creator;
    String modifier;
    String content;
    String contentStatus;
    boolean homePage;
    public static final String __PARANAMER_DATA = "<init> com.atlassian.confluence.pages.Page page \nequals java.lang.Object o \nsetContent java.lang.String content \nsetContentStatus java.lang.String contentStatus \nsetCreated java.util.Date created \nsetCreator java.lang.String creator \nsetHomePage boolean homePage \nsetModified java.util.Date modified \nsetModifier java.lang.String modifier \n";

    public RemotePage() {
    }

    public RemotePage(Page page) {
        super(page);
        this.content = page.getBodyAsString();
        if (page.getCreationDate() != null) {
            this.created = new Date(page.getCreationDate().getTime());
        }
        if (page.getCreatorName() != null) {
            this.creator = page.getCreatorName();
        }
        if (page.getLastModificationDate() != null) {
            this.modified = new Date(page.getLastModificationDate().getTime());
        }
        if (page.getLastModifierName() != null) {
            this.modifier = page.getLastModifierName();
        }
        this.contentStatus = page.isCurrent() ? "current" : "deleted";
        this.homePage = page.isHomePage();
    }

    public Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return this.modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getModifier() {
        return this.modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentStatus() {
        return this.contentStatus;
    }

    public void setContentStatus(String contentStatus) {
        this.contentStatus = contentStatus;
    }

    public boolean isHomePage() {
        return this.homePage;
    }

    public void setHomePage(boolean homePage) {
        this.homePage = homePage;
    }

    public boolean isCurrent() {
        return "current".equals(this.contentStatus);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RemotePage)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        RemotePage remotePage = (RemotePage)o;
        if (this.homePage != remotePage.homePage) {
            return false;
        }
        if (this.content != null ? !this.content.equals(remotePage.content) : remotePage.content != null) {
            return false;
        }
        if (this.created != null ? !this.created.equals(remotePage.created) : remotePage.created != null) {
            return false;
        }
        if (this.creator != null ? !this.creator.equals(remotePage.creator) : remotePage.creator != null) {
            return false;
        }
        if (this.modified != null ? !this.modified.equals(remotePage.modified) : remotePage.modified != null) {
            return false;
        }
        return !(this.modifier != null ? !this.modifier.equals(remotePage.modifier) : remotePage.modifier != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (this.created != null ? this.created.hashCode() : 0);
        result = 29 * result + (this.modified != null ? this.modified.hashCode() : 0);
        result = 29 * result + (this.creator != null ? this.creator.hashCode() : 0);
        result = 29 * result + (this.modifier != null ? this.modifier.hashCode() : 0);
        result = 29 * result + (this.content != null ? this.content.hashCode() : 0);
        result = 29 * result + (this.homePage ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this);
    }
}

