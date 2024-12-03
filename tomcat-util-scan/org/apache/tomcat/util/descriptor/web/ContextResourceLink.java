/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.descriptor.web.ResourceBase;

public class ContextResourceLink
extends ResourceBase {
    private static final long serialVersionUID = 1L;
    private String global = null;
    private String factory = null;

    public String getGlobal() {
        return this.global;
    }

    public void setGlobal(String global) {
        this.global = global;
    }

    public String getFactory() {
        return this.factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ContextResourceLink[");
        sb.append("name=");
        sb.append(this.getName());
        if (this.getType() != null) {
            sb.append(", type=");
            sb.append(this.getType());
        }
        if (this.getGlobal() != null) {
            sb.append(", global=");
            sb.append(this.getGlobal());
        }
        sb.append(']');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.factory == null ? 0 : this.factory.hashCode());
        result = 31 * result + (this.global == null ? 0 : this.global.hashCode());
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
        ContextResourceLink other = (ContextResourceLink)obj;
        if (this.factory == null ? other.factory != null : !this.factory.equals(other.factory)) {
            return false;
        }
        return !(this.global == null ? other.global != null : !this.global.equals(other.global));
    }
}

