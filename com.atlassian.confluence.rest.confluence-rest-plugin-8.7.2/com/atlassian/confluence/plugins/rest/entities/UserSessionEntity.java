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

import com.atlassian.confluence.plugins.rest.entities.ContentEntityList;
import com.atlassian.plugins.rest.common.expand.Expandable;
import java.util.Objects;
import java.util.StringJoiner;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="session")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class UserSessionEntity {
    @XmlElement(name="history")
    @Expandable(value="history")
    private ContentEntityList history;
    @XmlAttribute
    private String expand;

    public UserSessionEntity() {
    }

    public UserSessionEntity(ContentEntityList history) {
        this.history = history;
    }

    public ContentEntityList getHistory() {
        return this.history;
    }

    public String toString() {
        return new StringJoiner(", ", UserSessionEntity.class.getSimpleName() + "[", "]").add("history=" + this.history).add("expand='" + this.expand + "'").toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserSessionEntity)) {
            return false;
        }
        UserSessionEntity that = (UserSessionEntity)o;
        return Objects.equals(this.history, that.history) && Objects.equals(this.expand, that.expand);
    }

    public int hashCode() {
        return Objects.hash(this.expand, this.history);
    }
}

