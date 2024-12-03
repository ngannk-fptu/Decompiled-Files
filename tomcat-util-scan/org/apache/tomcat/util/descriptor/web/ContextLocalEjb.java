/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.descriptor.web.ResourceBase;

public class ContextLocalEjb
extends ResourceBase {
    private static final long serialVersionUID = 1L;
    private String home = null;
    private String link = null;
    private String local = null;

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

    public String getLocal() {
        return this.local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ContextLocalEjb[");
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
        if (this.link != null) {
            sb.append(", link=");
            sb.append(this.link);
        }
        if (this.local != null) {
            sb.append(", local=");
            sb.append(this.local);
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
        result = 31 * result + (this.local == null ? 0 : this.local.hashCode());
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
        ContextLocalEjb other = (ContextLocalEjb)obj;
        if (this.home == null ? other.home != null : !this.home.equals(other.home)) {
            return false;
        }
        if (this.link == null ? other.link != null : !this.link.equals(other.link)) {
            return false;
        }
        return !(this.local == null ? other.local != null : !this.local.equals(other.local));
    }
}

