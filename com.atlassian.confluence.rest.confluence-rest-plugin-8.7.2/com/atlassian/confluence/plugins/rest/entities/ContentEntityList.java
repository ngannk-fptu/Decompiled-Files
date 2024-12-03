/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.expand.Expandable
 *  com.atlassian.plugins.rest.common.expand.entity.ListWrapper
 *  com.atlassian.plugins.rest.common.expand.entity.ListWrapperCallback
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlTransient
 */
package com.atlassian.confluence.plugins.rest.entities;

import com.atlassian.confluence.plugins.rest.entities.ContentEntity;
import com.atlassian.plugins.rest.common.expand.Expandable;
import com.atlassian.plugins.rest.common.expand.entity.ListWrapper;
import com.atlassian.plugins.rest.common.expand.entity.ListWrapperCallback;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name="contents")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class ContentEntityList
implements ListWrapper<ContentEntity> {
    @XmlAttribute
    private String expand;
    @XmlAttribute
    private int size;
    @XmlElement(name="content")
    @Expandable
    private List<ContentEntity> contents;
    @XmlTransient
    private final ListWrapperCallback<ContentEntity> callback;

    public ContentEntityList() {
        this.size = 0;
        this.callback = null;
    }

    public ContentEntityList(int size, ListWrapperCallback<ContentEntity> callback) {
        this.size = size;
        this.callback = callback;
    }

    public List<ContentEntity> getContents() {
        return this.contents;
    }

    public void setContents(List<ContentEntity> contents) {
        this.contents = contents;
    }

    public ListWrapperCallback<ContentEntity> getCallback() {
        return this.callback;
    }

    public int getSize() {
        return this.size;
    }

    public String toString() {
        return new StringJoiner(", ", ContentEntityList.class.getSimpleName() + "[", "]").add("expand='" + this.expand + "'").add("size=" + this.size).add("contents=" + this.contents).toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContentEntityList)) {
            return false;
        }
        ContentEntityList that = (ContentEntityList)o;
        return this.size == that.size && Objects.equals(this.expand, that.expand) && Objects.equals(this.contents, that.contents);
    }

    public int hashCode() {
        return Objects.hash(this.size, this.expand, this.contents);
    }
}

