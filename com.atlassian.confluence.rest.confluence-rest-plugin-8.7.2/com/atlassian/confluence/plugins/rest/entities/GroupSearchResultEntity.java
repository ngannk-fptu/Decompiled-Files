/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.rest.entities;

import com.atlassian.confluence.plugins.rest.entities.SearchResultEntity;
import java.util.Objects;
import java.util.StringJoiner;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="group")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class GroupSearchResultEntity
extends SearchResultEntity {
    @XmlElement(name="name")
    public String name;
    @XmlElement(name="type")
    public final String type = "group";

    public GroupSearchResultEntity(String name) {
        this.name = name;
    }

    public GroupSearchResultEntity() {
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", GroupSearchResultEntity.class.getSimpleName() + "[", "]").add("name='" + this.name + "'").add("type='group'").toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GroupSearchResultEntity)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        GroupSearchResultEntity that = (GroupSearchResultEntity)o;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.name, "group");
    }
}

