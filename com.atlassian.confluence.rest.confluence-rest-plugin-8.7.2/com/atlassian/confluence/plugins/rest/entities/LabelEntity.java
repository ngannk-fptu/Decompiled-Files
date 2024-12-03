/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.rest.entities;

import java.util.StringJoiner;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.StringUtils;

@XmlRootElement(name="label")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class LabelEntity {
    @XmlAttribute
    String namespace;
    @XmlAttribute
    String name;
    @XmlAttribute
    String owner;

    public String getNamespace() {
        return this.namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int hashCode() {
        int prime = 47;
        int result = 1;
        result = 47 * result + (this.namespace == null ? 0 : this.namespace.hashCode());
        result = 47 * result + (this.name == null ? 0 : this.name.hashCode());
        result = 47 * result + (this.owner == null ? 0 : this.owner.hashCode());
        return result;
    }

    public String toString() {
        return new StringJoiner(", ", LabelEntity.class.getSimpleName() + "[", "]").add("namespace='" + this.namespace + "'").add("name='" + this.name + "'").add("owner='" + this.owner + "'").toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LabelEntity)) {
            return false;
        }
        LabelEntity other = (LabelEntity)obj;
        return StringUtils.equals((CharSequence)this.namespace, (CharSequence)other.namespace) && StringUtils.equals((CharSequence)this.name, (CharSequence)other.name) && StringUtils.equals((CharSequence)this.owner, (CharSequence)other.owner);
    }
}

