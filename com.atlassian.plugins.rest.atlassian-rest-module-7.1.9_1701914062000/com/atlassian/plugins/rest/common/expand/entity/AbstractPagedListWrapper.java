/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.plugins.rest.common.expand.entity;

import com.atlassian.plugins.rest.common.expand.entity.ListWrapper;
import com.atlassian.plugins.rest.common.expand.entity.ListWrapperCallback;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(value=XmlAccessType.FIELD)
public abstract class AbstractPagedListWrapper<T>
implements ListWrapper<T> {
    @XmlAttribute
    private final int size;
    @XmlAttribute(name="max-results")
    private final int maxResults;
    @XmlAttribute(name="start-index")
    private Integer startIndex;

    private AbstractPagedListWrapper() {
        this.size = 0;
        this.maxResults = 0;
    }

    protected AbstractPagedListWrapper(int size, int maxResults) {
        this.size = size;
        this.maxResults = maxResults;
    }

    public Integer getStartIndex() {
        return this.startIndex;
    }

    public int getSize() {
        return this.size;
    }

    public int getMaxResults() {
        return this.maxResults;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    @Override
    public final ListWrapperCallback<T> getCallback() {
        return indexes -> {
            int minIndex = indexes.getMinIndex(this.size);
            if (minIndex != -1) {
                this.setStartIndex(minIndex);
            }
            return this.getPagingCallback().getItems(indexes);
        };
    }

    public abstract ListWrapperCallback<T> getPagingCallback();
}

