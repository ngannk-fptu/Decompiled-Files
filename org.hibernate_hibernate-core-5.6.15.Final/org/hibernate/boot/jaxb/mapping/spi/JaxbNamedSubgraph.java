/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlType
 */
package org.hibernate.boot.jaxb.mapping.spi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.boot.jaxb.mapping.spi.JaxbNamedAttributeNode;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="named-subgraph", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"namedAttributeNode"})
public class JaxbNamedSubgraph
implements Serializable {
    @XmlElement(name="named-attribute-node", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbNamedAttributeNode> namedAttributeNode;
    @XmlAttribute(name="name", required=true)
    protected String name;
    @XmlAttribute(name="class")
    protected String clazz;

    public List<JaxbNamedAttributeNode> getNamedAttributeNode() {
        if (this.namedAttributeNode == null) {
            this.namedAttributeNode = new ArrayList<JaxbNamedAttributeNode>();
        }
        return this.namedAttributeNode;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getClazz() {
        return this.clazz;
    }

    public void setClazz(String value) {
        this.clazz = value;
    }
}

