/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.renderer.WikiStyleRenderer
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.rpc.soap.beans;

import com.atlassian.confluence.rpc.soap.beans.RemoteSpaceSummary;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.renderer.WikiStyleRenderer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class RemoteSpace
extends RemoteSpaceSummary {
    String description;
    String spaceGroup;
    long homePage;
    public static final String __PARANAMER_DATA = "<init> com.atlassian.confluence.spaces.Space,com.atlassian.renderer.WikiStyleRenderer space,renderer \nequals java.lang.Object o \nsetDescription java.lang.String description \nsetHomePage long homePage \nsetSpaceGroup java.lang.String spaceGroup \n";

    public RemoteSpace(Space space, WikiStyleRenderer renderer) {
        super(space);
        if (space.getSpaceGroup() != null) {
            this.spaceGroup = space.getSpaceGroup().getKey();
        }
        if (space.getHomePage() != null) {
            this.homePage = space.getHomePage().getId();
        }
        if (space.getDescription() != null && StringUtils.isNotEmpty((CharSequence)space.getDescription().getBodyContent().getBody())) {
            this.description = space.getDescription().getBodyContent().getBody();
        }
    }

    public RemoteSpace() {
    }

    @Deprecated
    public String getSpaceGroup() {
        return this.spaceGroup;
    }

    @Deprecated
    public void setSpaceGroup(String spaceGroup) {
        this.spaceGroup = spaceGroup;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getHomePage() {
        return this.homePage;
    }

    public void setHomePage(long homePage) {
        this.homePage = homePage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        RemoteSpace that = (RemoteSpace)o;
        if (this.homePage != that.homePage) {
            return false;
        }
        if (this.description != null ? !this.description.equals(that.description) : that.description != null) {
            return false;
        }
        return !(this.spaceGroup != null ? !this.spaceGroup.equals(that.spaceGroup) : that.spaceGroup != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.description != null ? this.description.hashCode() : 0);
        result = 31 * result + (this.spaceGroup != null ? this.spaceGroup.hashCode() : 0);
        result = 31 * result + (int)(this.homePage ^ this.homePage >>> 32);
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this);
    }
}

