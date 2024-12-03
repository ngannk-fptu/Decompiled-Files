/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.Link
 *  com.atlassian.plugins.rest.common.expand.entity.ListWrapper
 *  com.atlassian.plugins.rest.common.expand.entity.ListWrapperCallback
 *  com.atlassian.plugins.rest.common.expand.parameter.Indexes
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlElements
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.plugin.rest.entity;

import com.atlassian.crowd.plugin.rest.entity.AttributeEntity;
import com.atlassian.plugins.rest.common.Link;
import com.atlassian.plugins.rest.common.expand.entity.ListWrapper;
import com.atlassian.plugins.rest.common.expand.entity.ListWrapperCallback;
import com.atlassian.plugins.rest.common.expand.parameter.Indexes;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="attributes")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class AttributeEntityList
implements ListWrapper<AttributeEntity>,
Iterable<AttributeEntity> {
    @XmlElements(value={@XmlElement(name="attribute")})
    private final List<AttributeEntity> attributes;
    @XmlElement(name="link")
    private final Link link;

    private AttributeEntityList() {
        this.attributes = new ArrayList<AttributeEntity>();
        this.link = null;
    }

    public AttributeEntityList(List<AttributeEntity> attributes, Link link) {
        this.attributes = ImmutableList.copyOf((Collection)((Collection)Preconditions.checkNotNull(attributes)));
        this.link = link;
    }

    public int size() {
        return this.attributes.size();
    }

    public boolean isEmpty() {
        return this.attributes.isEmpty();
    }

    public AttributeEntity get(int index) {
        return this.attributes.get(index);
    }

    @Override
    public Iterator<AttributeEntity> iterator() {
        return this.attributes.iterator();
    }

    public Link getLink() {
        return this.link;
    }

    public ListWrapperCallback<AttributeEntity> getCallback() {
        return new ListWrapperCallback<AttributeEntity>(){

            public List<AttributeEntity> getItems(Indexes indexes) {
                return AttributeEntityList.this.attributes;
            }
        };
    }
}

