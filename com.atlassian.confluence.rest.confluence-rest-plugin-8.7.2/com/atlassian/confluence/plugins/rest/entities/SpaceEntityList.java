/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.expand.Expandable
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.rest.entities;

import com.atlassian.confluence.plugins.rest.entities.SpaceEntity;
import com.atlassian.plugins.rest.common.expand.Expandable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="spaces")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class SpaceEntityList {
    @XmlAttribute
    private String expand;
    @XmlElement(name="space")
    @Expandable(value="space")
    private List<SpaceEntity> spaces = new ArrayList<SpaceEntity>();

    public List<SpaceEntity> getSpaces() {
        return this.spaces;
    }

    public void setSpaces(List<SpaceEntity> spaces) {
        this.spaces = spaces;
    }

    public String toString() {
        return new StringJoiner(", ", SpaceEntityList.class.getSimpleName() + "[", "]").add("expand='" + this.expand + "'").add("spaces=" + this.spaces).toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpaceEntityList)) {
            return false;
        }
        SpaceEntityList that = (SpaceEntityList)o;
        return Objects.equals(this.expand, that.expand) && Objects.equals(this.spaces, that.spaces);
    }

    public int hashCode() {
        return Objects.hash(this.expand, this.spaces);
    }
}

