/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.Link
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlElementWrapper
 *  javax.xml.bind.annotation.XmlElements
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.plugin.rest.entity;

import com.atlassian.plugins.rest.common.Link;
import java.util.Collection;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="attribute")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class MultiValuedAttributeEntity {
    @XmlElement(name="link")
    private final Link link;
    @XmlAttribute(name="name")
    private final String name;
    @XmlElementWrapper(name="values")
    @XmlElements(value={@XmlElement(name="value")})
    private final Collection<String> values;

    private MultiValuedAttributeEntity() {
        this.name = null;
        this.values = null;
        this.link = null;
    }

    public MultiValuedAttributeEntity(String name, Collection<String> values, Link link) {
        this.name = name;
        this.values = values;
        this.link = link;
    }

    public String getName() {
        return this.name;
    }

    public Collection<String> getValues() {
        return this.values;
    }

    public String getValue() {
        return this.values.iterator().next();
    }

    public Link getLink() {
        return this.link;
    }
}

