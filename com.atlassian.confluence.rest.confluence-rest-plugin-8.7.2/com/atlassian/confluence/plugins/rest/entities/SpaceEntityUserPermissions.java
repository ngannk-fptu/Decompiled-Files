/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.expand.Expander
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlTransient
 */
package com.atlassian.confluence.plugins.rest.entities;

import com.atlassian.confluence.plugins.rest.entities.SpaceEntityUserPermissionsExpander;
import com.atlassian.plugins.rest.common.expand.Expander;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name="userpermissions")
@XmlAccessorType(value=XmlAccessType.FIELD)
@Expander(value=SpaceEntityUserPermissionsExpander.class)
public class SpaceEntityUserPermissions {
    @XmlElement(name="permission")
    private List<String> permissions = new ArrayList<String>();
    @XmlTransient
    private String spaceKey;
    @XmlTransient
    private String effectiveUser;

    public SpaceEntityUserPermissions() {
    }

    public SpaceEntityUserPermissions(String spaceKey, String effectiveUser) {
        this.spaceKey = spaceKey;
        this.effectiveUser = effectiveUser;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public String getEffectiveUser() {
        return this.effectiveUser;
    }

    public List<String> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public void addPermission(String permission) {
        this.permissions.add(permission);
    }

    public String toString() {
        return new StringJoiner(", ", SpaceEntityUserPermissions.class.getSimpleName() + "[", "]").add("permissions=" + this.permissions).add("spaceKey='" + this.spaceKey + "'").add("effectiveUser='" + this.effectiveUser + "'").toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpaceEntityUserPermissions)) {
            return false;
        }
        SpaceEntityUserPermissions that = (SpaceEntityUserPermissions)o;
        return Objects.equals(this.permissions, that.permissions) && Objects.equals(this.spaceKey, that.spaceKey) && Objects.equals(this.effectiveUser, that.effectiveUser);
    }

    public int hashCode() {
        return Objects.hash(this.permissions, this.spaceKey, this.effectiveUser);
    }
}

