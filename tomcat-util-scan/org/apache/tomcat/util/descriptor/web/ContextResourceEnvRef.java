/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.descriptor.web.ResourceBase;

public class ContextResourceEnvRef
extends ResourceBase {
    private static final long serialVersionUID = 1L;
    private boolean override = true;

    public boolean getOverride() {
        return this.override;
    }

    public void setOverride(boolean override) {
        this.override = override;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ContextResourceEnvRef[");
        sb.append("name=");
        sb.append(this.getName());
        if (this.getType() != null) {
            sb.append(", type=");
            sb.append(this.getType());
        }
        sb.append(", override=");
        sb.append(this.override);
        sb.append(']');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.override ? 1231 : 1237);
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
        ContextResourceEnvRef other = (ContextResourceEnvRef)obj;
        return this.override == other.override;
    }
}

