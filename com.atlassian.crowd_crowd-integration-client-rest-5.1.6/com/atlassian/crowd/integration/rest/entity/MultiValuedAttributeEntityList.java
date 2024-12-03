/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Attributes
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlElements
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.integration.rest.entity;

import com.atlassian.crowd.embedded.api.Attributes;
import com.atlassian.crowd.integration.rest.entity.MultiValuedAttributeEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="attributes")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class MultiValuedAttributeEntityList
implements Attributes,
Iterable<MultiValuedAttributeEntity> {
    @XmlElements(value={@XmlElement(name="attribute")})
    private final List<MultiValuedAttributeEntity> attributes;

    private MultiValuedAttributeEntityList() {
        this.attributes = new ArrayList<MultiValuedAttributeEntity>();
    }

    public MultiValuedAttributeEntityList(List<MultiValuedAttributeEntity> attributes) {
        this.attributes = new ArrayList<MultiValuedAttributeEntity>(attributes);
    }

    public int size() {
        return this.attributes.size();
    }

    public Set<String> getValues(String key) {
        return this.asMap().get(key);
    }

    public String getValue(String key) {
        MultiValuedAttributeEntity attribute = null;
        for (MultiValuedAttributeEntity attr : this.attributes) {
            if (!attr.getName().equals(key)) continue;
            attribute = attr;
            break;
        }
        if (attribute == null || attribute.getValues() == null || attribute.getValues().isEmpty()) {
            return null;
        }
        return attribute.getValues().iterator().next();
    }

    public Set<String> getKeys() {
        return this.asMap().keySet();
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

    private Map<String, Set<String>> asMap() {
        HashMap<String, Set<String>> map = new HashMap<String, Set<String>>(this.attributes.size());
        for (MultiValuedAttributeEntity attributeEntity : this.attributes) {
            map.put(attributeEntity.getName(), new HashSet<String>(attributeEntity.getValues()));
        }
        return map;
    }

    public String toString() {
        return this.attributes.toString();
    }
}

