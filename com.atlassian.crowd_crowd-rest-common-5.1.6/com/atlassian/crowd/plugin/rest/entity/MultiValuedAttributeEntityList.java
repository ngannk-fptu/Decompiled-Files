/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.Link
 *  com.atlassian.plugins.rest.common.expand.Expander
 *  com.atlassian.plugins.rest.common.expand.entity.ListWrapper
 *  com.atlassian.plugins.rest.common.expand.entity.ListWrapperCallback
 *  com.atlassian.plugins.rest.common.expand.parameter.Indexes
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.plugin.rest.entity;

import com.atlassian.crowd.plugin.rest.entity.MultiValuedAttributeEntity;
import com.atlassian.crowd.plugin.rest.entity.MultiValuedAttributeEntityListExpander;
import com.atlassian.plugins.rest.common.Link;
import com.atlassian.plugins.rest.common.expand.Expander;
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
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonProperty;

@XmlRootElement(name="attributes")
@XmlAccessorType(value=XmlAccessType.FIELD)
@Expander(value=MultiValuedAttributeEntityListExpander.class)
public class MultiValuedAttributeEntityList
implements ListWrapper<MultiValuedAttributeEntity>,
Iterable<MultiValuedAttributeEntity> {
    @XmlElement(name="attribute")
    @JsonProperty(value="attributes")
    private final List<MultiValuedAttributeEntity> attributes;
    @XmlElement(name="link")
    private final Link link;

    private MultiValuedAttributeEntityList() {
        this.attributes = new ArrayList<MultiValuedAttributeEntity>();
        this.link = null;
    }

    public MultiValuedAttributeEntityList(List<MultiValuedAttributeEntity> attributes, Link link) {
        this.attributes = ImmutableList.copyOf((Collection)((Collection)Preconditions.checkNotNull(attributes)));
        this.link = link;
    }

    public int size() {
        return this.attributes.size();
    }

    public boolean isEmpty() {
        return this.attributes.isEmpty();
    }

    public MultiValuedAttributeEntity get(int index) {
        return this.attributes.get(index);
    }

    @Override
    public Iterator<MultiValuedAttributeEntity> iterator() {
        return this.attributes.iterator();
    }

    public Link getLink() {
        return this.link;
    }

    public ListWrapperCallback<MultiValuedAttributeEntity> getCallback() {
        return new ListWrapperCallback<MultiValuedAttributeEntity>(){

            public List<MultiValuedAttributeEntity> getItems(Indexes indexes) {
                return MultiValuedAttributeEntityList.this.attributes;
            }
        };
    }
}

