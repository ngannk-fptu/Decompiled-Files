/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlElementWrapper
 *  javax.xml.bind.annotation.XmlElements
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.integration.rest.entity;

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
    @XmlAttribute(name="name")
    private final String name;
    @XmlElementWrapper(name="values")
    @XmlElements(value={@XmlElement(name="value")})
    private final Collection<String> values;

    private MultiValuedAttributeEntity() {
        this.name = null;
        this.values = null;
    }

    public MultiValuedAttributeEntity(String name, Collection<String> values) {
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return this.name;
    }

    public Collection<String> getValues() {
        return this.values;
    }

    public String toString() {
        return "MultiValuedAttributeEntity{name='" + this.name + '\'' + ", values=" + this.values + '}';
    }
}

