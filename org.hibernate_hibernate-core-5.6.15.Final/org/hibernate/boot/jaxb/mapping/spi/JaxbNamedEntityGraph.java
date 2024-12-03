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
import org.hibernate.boot.jaxb.mapping.spi.JaxbNamedSubgraph;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="named-entity-graph", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"namedAttributeNode", "subgraph", "subclassSubgraph"})
public class JaxbNamedEntityGraph
implements Serializable {
    @XmlElement(name="named-attribute-node", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbNamedAttributeNode> namedAttributeNode;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbNamedSubgraph> subgraph;
    @XmlElement(name="subclass-subgraph", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbNamedSubgraph> subclassSubgraph;
    @XmlAttribute(name="name")
    protected String name;
    @XmlAttribute(name="include-all-attributes")
    protected Boolean includeAllAttributes;

    public List<JaxbNamedAttributeNode> getNamedAttributeNode() {
        if (this.namedAttributeNode == null) {
            this.namedAttributeNode = new ArrayList<JaxbNamedAttributeNode>();
        }
        return this.namedAttributeNode;
    }

    public List<JaxbNamedSubgraph> getSubgraph() {
        if (this.subgraph == null) {
            this.subgraph = new ArrayList<JaxbNamedSubgraph>();
        }
        return this.subgraph;
    }

    public List<JaxbNamedSubgraph> getSubclassSubgraph() {
        if (this.subclassSubgraph == null) {
            this.subclassSubgraph = new ArrayList<JaxbNamedSubgraph>();
        }
        return this.subclassSubgraph;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public Boolean isIncludeAllAttributes() {
        return this.includeAllAttributes;
    }

    public void setIncludeAllAttributes(Boolean value) {
        this.includeAllAttributes = value;
    }
}

