/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AccessType
 *  javax.persistence.TemporalType
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlSchemaType
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package org.hibernate.boot.jaxb.mapping.spi;

import java.io.Serializable;
import javax.persistence.AccessType;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.boot.jaxb.mapping.spi.Adapter1;
import org.hibernate.boot.jaxb.mapping.spi.Adapter10;
import org.hibernate.boot.jaxb.mapping.spi.JaxbColumn;
import org.hibernate.boot.jaxb.mapping.spi.JaxbGeneratedValue;
import org.hibernate.boot.jaxb.mapping.spi.JaxbSequenceGenerator;
import org.hibernate.boot.jaxb.mapping.spi.JaxbTableGenerator;
import org.hibernate.boot.jaxb.mapping.spi.PersistentAttribute;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="id", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"column", "generatedValue", "temporal", "tableGenerator", "sequenceGenerator"})
public class JaxbId
implements Serializable,
PersistentAttribute {
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbColumn column;
    @XmlElement(name="generated-value", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbGeneratedValue generatedValue;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", type=String.class)
    @XmlJavaTypeAdapter(value=Adapter10.class)
    @XmlSchemaType(name="token")
    protected TemporalType temporal;
    @XmlElement(name="table-generator", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbTableGenerator tableGenerator;
    @XmlElement(name="sequence-generator", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbSequenceGenerator sequenceGenerator;
    @XmlAttribute(name="name", required=true)
    protected String name;
    @XmlAttribute(name="access")
    @XmlJavaTypeAdapter(value=Adapter1.class)
    protected AccessType access;

    public JaxbColumn getColumn() {
        return this.column;
    }

    public void setColumn(JaxbColumn value) {
        this.column = value;
    }

    public JaxbGeneratedValue getGeneratedValue() {
        return this.generatedValue;
    }

    public void setGeneratedValue(JaxbGeneratedValue value) {
        this.generatedValue = value;
    }

    public TemporalType getTemporal() {
        return this.temporal;
    }

    public void setTemporal(TemporalType value) {
        this.temporal = value;
    }

    public JaxbTableGenerator getTableGenerator() {
        return this.tableGenerator;
    }

    public void setTableGenerator(JaxbTableGenerator value) {
        this.tableGenerator = value;
    }

    public JaxbSequenceGenerator getSequenceGenerator() {
        return this.sequenceGenerator;
    }

    public void setSequenceGenerator(JaxbSequenceGenerator value) {
        this.sequenceGenerator = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    @Override
    public AccessType getAccess() {
        return this.access;
    }

    @Override
    public void setAccess(AccessType value) {
        this.access = value;
    }
}

