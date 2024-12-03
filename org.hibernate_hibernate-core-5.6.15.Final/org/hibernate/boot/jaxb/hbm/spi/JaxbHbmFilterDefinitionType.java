/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElementRef
 *  javax.xml.bind.annotation.XmlMixed
 *  javax.xml.bind.annotation.XmlType
 */
package org.hibernate.boot.jaxb.hbm.spi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="FilterDefinitionType", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"content"})
public class JaxbHbmFilterDefinitionType
implements Serializable {
    @XmlElementRef(name="filter-param", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JAXBElement.class)
    @XmlMixed
    protected List<Serializable> content;
    @XmlAttribute(name="condition")
    protected String condition;
    @XmlAttribute(name="name", required=true)
    protected String name;

    public List<Serializable> getContent() {
        if (this.content == null) {
            this.content = new ArrayList<Serializable>();
        }
        return this.content;
    }

    public String getCondition() {
        return this.condition;
    }

    public void setCondition(String value) {
        this.condition = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }
}

