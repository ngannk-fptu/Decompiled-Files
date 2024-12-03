/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.links.linktypes.UserProfileLink
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.rpc.soap.beans;

import com.atlassian.confluence.links.linktypes.UserProfileLink;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.user.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class RemoteUser
implements Comparable<RemoteUser> {
    String name;
    String fullname;
    String email;
    String url;
    public static final String __PARANAMER_DATA = "<init> com.atlassian.user.User user \ncompareTo com.atlassian.confluence.rpc.soap.beans.RemoteUser other \nequals java.lang.Object o \nsetEmail java.lang.String email \nsetFullname java.lang.String fullname \nsetName java.lang.String name \nsetUrl java.lang.String url \n";

    public RemoteUser() {
    }

    public RemoteUser(User user) {
        this.name = user.getName();
        this.fullname = user.getFullName();
        this.email = user.getEmail();
        this.url = GeneralUtil.getGlobalSettings().getBaseUrl() + UserProfileLink.getLinkPath((String)this.name);
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
        if (!(o instanceof RemoteUser)) {
            return false;
        }
        RemoteUser remoteUser = (RemoteUser)o;
        return new EqualsBuilder().append((Object)this.name, (Object)remoteUser.name).append((Object)this.fullname, (Object)remoteUser.fullname).append((Object)this.email, (Object)remoteUser.email).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder().append((Object)this.name).append((Object)this.fullname).append((Object)this.email).toHashCode();
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this);
    }

    @Override
    public int compareTo(RemoteUser other) {
        if (this.fullname == null) {
            return other.fullname == null ? 0 : -1;
        }
        return this.fullname.compareToIgnoreCase(other.fullname);
    }
}

