/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.descriptor.web.ResourceBase;

public class MessageDestination
extends ResourceBase {
    private static final long serialVersionUID = 1L;
    private String displayName = null;
    private String largeIcon = null;
    private String smallIcon = null;

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLargeIcon() {
        return this.largeIcon;
    }

    public void setLargeIcon(String largeIcon) {
        this.largeIcon = largeIcon;
    }

    public String getSmallIcon() {
        return this.smallIcon;
    }

    public void setSmallIcon(String smallIcon) {
        this.smallIcon = smallIcon;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("MessageDestination[");
        sb.append("name=");
        sb.append(this.getName());
        if (this.displayName != null) {
            sb.append(", displayName=");
            sb.append(this.displayName);
        }
        if (this.largeIcon != null) {
            sb.append(", largeIcon=");
            sb.append(this.largeIcon);
        }
        if (this.smallIcon != null) {
            sb.append(", smallIcon=");
            sb.append(this.smallIcon);
        }
        if (this.getDescription() != null) {
            sb.append(", description=");
            sb.append(this.getDescription());
        }
        sb.append(']');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.displayName == null ? 0 : this.displayName.hashCode());
        result = 31 * result + (this.largeIcon == null ? 0 : this.largeIcon.hashCode());
        result = 31 * result + (this.smallIcon == null ? 0 : this.smallIcon.hashCode());
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
        MessageDestination other = (MessageDestination)obj;
        if (this.displayName == null ? other.displayName != null : !this.displayName.equals(other.displayName)) {
            return false;
        }
        if (this.largeIcon == null ? other.largeIcon != null : !this.largeIcon.equals(other.largeIcon)) {
            return false;
        }
        return !(this.smallIcon == null ? other.smallIcon != null : !this.smallIcon.equals(other.smallIcon));
    }
}

