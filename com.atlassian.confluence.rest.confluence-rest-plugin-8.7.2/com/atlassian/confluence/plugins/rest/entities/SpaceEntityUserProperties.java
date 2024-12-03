/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.Link
 *  com.atlassian.plugins.rest.common.expand.Expandable
 *  com.atlassian.plugins.rest.common.expand.Expander
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlTransient
 */
package com.atlassian.confluence.plugins.rest.entities;

import com.atlassian.confluence.plugins.rest.entities.LabelEntityList;
import com.atlassian.confluence.plugins.rest.entities.SpaceEntityUserPermissions;
import com.atlassian.confluence.plugins.rest.entities.SpaceEntityUserPropertiesExpander;
import com.atlassian.plugins.rest.common.Link;
import com.atlassian.plugins.rest.common.expand.Expandable;
import com.atlassian.plugins.rest.common.expand.Expander;
import java.util.Objects;
import java.util.StringJoiner;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name="userproperties")
@XmlAccessorType(value=XmlAccessType.FIELD)
@Expander(value=SpaceEntityUserPropertiesExpander.class)
public class SpaceEntityUserProperties {
    @XmlElement(name="permissions")
    @Expandable(value="permissions")
    private SpaceEntityUserPermissions permissions;
    @XmlAttribute(name="favourite")
    private Boolean favourite;
    @XmlAttribute(name="effective-user")
    private String effectiveUser;
    @XmlTransient
    private String spaceKey;
    @XmlElement(name="labels")
    private LabelEntityList labels;
    @XmlElement(name="logo")
    private Link logo;

    public SpaceEntityUserProperties() {
    }

    public SpaceEntityUserProperties(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public SpaceEntityUserPermissions getPermissions() {
        return this.permissions;
    }

    public void setPermissions(SpaceEntityUserPermissions permissions) {
        this.permissions = permissions;
    }

    public Boolean isFavourite() {
        return this.favourite;
    }

    public void setFavourite(Boolean favourite) {
        this.favourite = favourite;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public String getEffectiveUser() {
        return this.effectiveUser;
    }

    public void setEffectiveUser(String effectiveUser) {
        this.effectiveUser = effectiveUser;
    }

    public LabelEntityList getLabels() {
        return this.labels;
    }

    public void setLabels(LabelEntityList labels) {
        this.labels = labels;
    }

    public Link getLogo() {
        return this.logo;
    }

    public void setLogo(Link logo) {
        this.logo = logo;
    }

    public String toString() {
        return new StringJoiner(", ", SpaceEntityUserProperties.class.getSimpleName() + "[", "]").add("permissions=" + this.permissions).add("favourite=" + this.favourite).add("effectiveUser='" + this.effectiveUser + "'").add("spaceKey='" + this.spaceKey + "'").add("labels=" + this.labels).add("logo=" + this.logo).toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpaceEntityUserProperties)) {
            return false;
        }
        SpaceEntityUserProperties that = (SpaceEntityUserProperties)o;
        return Objects.equals(this.permissions, that.permissions) && Objects.equals(this.favourite, that.favourite) && Objects.equals(this.effectiveUser, that.effectiveUser) && Objects.equals(this.spaceKey, that.spaceKey) && Objects.equals(this.labels, that.labels) && Objects.equals(this.logo, that.logo);
    }

    public int hashCode() {
        return Objects.hash(this.permissions, this.favourite, this.effectiveUser, this.spaceKey, this.labels, this.logo);
    }
}

