/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.util.GeneralUtil
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.rpc.soap.beans;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.GeneralUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class RemoteSpaceSummary {
    String key;
    String name;
    String url;
    String type;
    public static final String __PARANAMER_DATA = "<init> com.atlassian.confluence.spaces.Space space \nequals java.lang.Object o \nsetKey java.lang.String key \nsetName java.lang.String name \nsetType java.lang.String type \nsetUrl java.lang.String url \n";

    public RemoteSpaceSummary() {
    }

    public RemoteSpaceSummary(Space space) {
        this.key = space.getKey();
        this.name = space.getName();
        this.type = space.getSpaceType().toString();
        this.url = GeneralUtil.getGlobalSettings().getBaseUrl() + "/display/" + space.getKey();
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RemoteSpaceSummary)) {
            return false;
        }
        RemoteSpaceSummary remoteSpaceSummary = (RemoteSpaceSummary)o;
        if (!this.key.equals(remoteSpaceSummary.key)) {
            return false;
        }
        if (this.name != null ? !this.name.equals(remoteSpaceSummary.name) : remoteSpaceSummary.name != null) {
            return false;
        }
        return this.url.equals(remoteSpaceSummary.url);
    }

    public int hashCode() {
        int result = this.key.hashCode();
        result = 29 * result + (this.name != null ? this.name.hashCode() : 0);
        result = 29 * result + this.url.hashCode();
        return result;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this);
    }
}

