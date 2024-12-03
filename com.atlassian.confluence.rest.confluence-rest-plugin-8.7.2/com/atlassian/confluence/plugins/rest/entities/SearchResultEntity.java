/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlSeeAlso
 *  javax.xml.bind.annotation.XmlTransient
 */
package com.atlassian.confluence.plugins.rest.entities;

import com.atlassian.confluence.plugins.rest.entities.AttachmentEntity;
import com.atlassian.confluence.plugins.rest.entities.ContentEntity;
import com.atlassian.confluence.plugins.rest.entities.SearchResultGroupEntity;
import com.atlassian.confluence.plugins.rest.entities.SpaceEntity;
import java.util.Objects;
import java.util.StringJoiner;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

@XmlSeeAlso(value={AttachmentEntity.class, ContentEntity.class, SpaceEntity.class, SearchResultGroupEntity.class})
@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlTransient
public abstract class SearchResultEntity {
    @XmlAttribute
    protected String id;

    public String getId() {
        return this.id;
    }

    @XmlTransient
    public Long getIdLong() {
        return Long.parseLong(this.id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String toString() {
        return new StringJoiner(", ", SearchResultEntity.class.getSimpleName() + "[", "]").add("id='" + this.id + "'").toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SearchResultEntity)) {
            return false;
        }
        SearchResultEntity that = (SearchResultEntity)o;
        return Objects.equals(this.getId(), that.getId());
    }

    public int hashCode() {
        return Objects.hash(this.getId());
    }
}

