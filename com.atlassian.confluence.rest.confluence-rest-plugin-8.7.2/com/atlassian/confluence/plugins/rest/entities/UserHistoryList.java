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

import com.atlassian.confluence.plugins.rest.entities.ContentEntity;
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

@XmlRootElement(name="history")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class UserHistoryList {
    @XmlAttribute
    private String expand;
    @XmlElement(name="content")
    @Expandable(value="content")
    private final List<ContentEntity> contents = new ArrayList<ContentEntity>();

    public List<ContentEntity> getContents() {
        return this.contents;
    }

    public String toString() {
        return new StringJoiner(", ", UserHistoryList.class.getSimpleName() + "[", "]").add("expand='" + this.expand + "'").add("contents=" + this.contents).toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserHistoryList)) {
            return false;
        }
        UserHistoryList that = (UserHistoryList)o;
        return Objects.equals(this.expand, that.expand) && Objects.equals(this.contents, that.contents);
    }

    public int hashCode() {
        return Objects.hash(this.expand, this.contents);
    }
}

