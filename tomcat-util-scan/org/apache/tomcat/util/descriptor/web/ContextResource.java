/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.descriptor.web.ResourceBase;

public class ContextResource
extends ResourceBase {
    private static final long serialVersionUID = 1L;
    private String auth = null;
    private String scope = "Shareable";
    private boolean singleton = true;
    private String closeMethod = null;
    private boolean closeMethodConfigured = false;

    public String getAuth() {
        return this.auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getScope() {
        return this.scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean getSingleton() {
        return this.singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public String getCloseMethod() {
        return this.closeMethod;
    }

    public void setCloseMethod(String closeMethod) {
        this.closeMethodConfigured = true;
        this.closeMethod = closeMethod;
    }

    public boolean getCloseMethodConfigured() {
        return this.closeMethodConfigured;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ContextResource[");
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
        if (this.auth != null) {
            sb.append(", auth=");
            sb.append(this.auth);
        }
        if (this.scope != null) {
            sb.append(", scope=");
            sb.append(this.scope);
        }
        sb.append(']');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.auth == null ? 0 : this.auth.hashCode());
        result = 31 * result + (this.closeMethod == null ? 0 : this.closeMethod.hashCode());
        result = 31 * result + (this.scope == null ? 0 : this.scope.hashCode());
        result = 31 * result + (this.singleton ? 1231 : 1237);
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
        ContextResource other = (ContextResource)obj;
        if (this.auth == null ? other.auth != null : !this.auth.equals(other.auth)) {
            return false;
        }
        if (this.closeMethod == null ? other.closeMethod != null : !this.closeMethod.equals(other.closeMethod)) {
            return false;
        }
        if (this.scope == null ? other.scope != null : !this.scope.equals(other.scope)) {
            return false;
        }
        return this.singleton == other.singleton;
    }
}

