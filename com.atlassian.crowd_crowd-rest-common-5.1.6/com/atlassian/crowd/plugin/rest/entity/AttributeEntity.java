/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.Link
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.plugin.rest.entity;

import com.atlassian.plugins.rest.common.Link;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="attribute")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class AttributeEntity {
    @XmlElement(name="link")
    private final Link link;
    @XmlAttribute(name="name")
    private final String name;
    @XmlElement(name="value")
    private final String value;

    private AttributeEntity() {
        this.name = null;
        this.value = null;
        this.link = null;
    }

    public AttributeEntity(String name, String value, Link link) {
        this.name = name;
        this.value = value;
        this.link = link;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public Link getLink() {
        return this.link;
    }
}

