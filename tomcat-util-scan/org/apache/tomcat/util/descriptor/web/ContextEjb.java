/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.descriptor.web.ResourceBase;

public class ContextEjb
extends ResourceBase {
    private static final long serialVersionUID = 1L;
    private String home = null;
    private String link = null;
    private String remote = null;

    public String getHome() {
        return this.home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getRemote() {
        return this.remote;
    }

    public void setRemote(String remote) {
        this.remote = remote;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ContextEjb[");
        sb.append("name=");
        sb.append(this.getName());
        if (this.getDescription() != null) {
            sb.append(", description=");
            sb.append(this.getDescription());
        }
        if (this.getType() != null) {
            sb.append(", type=");
            sb.append(this.getType());
        }
        if (this.home != null) {
            sb.append(", home=");
            sb.append(this.home);
        }
        if (this.remote != null) {
            sb.append(", remote=");
            sb.append(this.remote);
        }
        if (this.link != null) {
            sb.append(", link=");
            sb.append(this.link);
        }
        sb.append(']');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.home == null ? 0 : this.home.hashCode());
        result = 31 * result + (this.link == null ? 0 : this.link.hashCode());
        result = 31 * result + (this.remote == null ? 0 : this.remote.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ContextEjb other = (ContextEjb)obj;
        if (this.home == null ? other.home != null : !this.home.equals(other.home)) {
            return false;
        }
        if (this.link == null ? other.link != null : !this.link.equals(other.link)) {
            return false;
        }
        return !(this.remote == null ? other.remote != null : !this.remote.equals(other.remote));
    }
}

