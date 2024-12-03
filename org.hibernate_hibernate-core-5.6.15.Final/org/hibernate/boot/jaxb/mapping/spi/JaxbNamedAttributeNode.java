/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlType
 */
package org.hibernate.boot.jaxb.mapping.spi;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="named-attribute-node", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
public class JaxbNamedAttributeNode
implements Serializable {
    @XmlAttribute(name="name", required=true)
    protected String name;
    @XmlAttribute(name="subgraph")
    protected String subgraph;
    @XmlAttribute(name="key-subgraph")
    protected String keySubgraph;

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getSubgraph() {
        return this.subgraph;
    }

    public void setSubgraph(String value) {
        this.subgraph = value;
    }

    public String getKeySubgraph() {
        return this.keySubgraph;
    }

    public void setKeySubgraph(String value) {
        this.keySubgraph = value;
    }
}

