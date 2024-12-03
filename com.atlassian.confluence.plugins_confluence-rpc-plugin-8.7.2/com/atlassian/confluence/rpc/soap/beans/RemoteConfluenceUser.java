/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.links.linktypes.UserProfileLink
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.GeneralUtil
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.rpc.soap.beans;

import com.atlassian.confluence.links.linktypes.UserProfileLink;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.GeneralUtil;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class RemoteConfluenceUser
implements Comparable<RemoteConfluenceUser> {
    String key;
    String name;
    String fullname;
    String email;
    String url;
    public static final String __PARANAMER_DATA = "<init> com.atlassian.confluence.user.ConfluenceUser user \ncompareTo com.atlassian.confluence.rpc.soap.beans.RemoteConfluenceUser other \nequals java.lang.Object o \nsetEmail java.lang.String email \nsetFullname java.lang.String fullname \nsetKey java.lang.String key \nsetName java.lang.String name \nsetUrl java.lang.String url \n";

    public RemoteConfluenceUser() {
    }

    public RemoteConfluenceUser(ConfluenceUser user) {
        this.key = user.getKey().getStringValue();
        this.name = user.getName();
        this.fullname = user.getFullName();
        this.email = user.getEmail();
        this.url = GeneralUtil.getGlobalSettings().getBaseUrl() + UserProfileLink.getLinkPath((String)this.name);
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

    public String getFullname() {
        return this.fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RemoteConfluenceUser)) {
            return false;
        }
        RemoteConfluenceUser remoteUser = (RemoteConfluenceUser)o;
        return new EqualsBuilder().append((Object)this.key, (Object)remoteUser.key).append((Object)this.name, (Object)remoteUser.name).append((Object)this.fullname, (Object)remoteUser.fullname).append((Object)this.email, (Object)remoteUser.email).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder().append((Object)this.key).append((Object)this.name).append((Object)this.fullname).append((Object)this.email).toHashCode();
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this);
    }

    @Override
    public int compareTo(RemoteConfluenceUser other) {
        if (this.fullname == null) {
            return other.fullname == null ? 0 : -1;
        }
        return this.fullname.compareToIgnoreCase(other.fullname);
    }
}

