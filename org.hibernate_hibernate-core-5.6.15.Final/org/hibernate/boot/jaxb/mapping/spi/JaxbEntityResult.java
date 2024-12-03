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
import org.hibernate.boot.jaxb.mapping.spi.JaxbFieldResult;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="entity-result", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"fieldResult"})
public class JaxbEntityResult
implements Serializable {
    @XmlElement(name="field-result", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbFieldResult> fieldResult;
    @XmlAttribute(name="entity-class", required=true)
    protected String entityClass;
    @XmlAttribute(name="discriminator-column")
    protected String discriminatorColumn;

    public List<JaxbFieldResult> getFieldResult() {
        if (this.fieldResult == null) {
            this.fieldResult = new ArrayList<JaxbFieldResult>();
        }
        return this.fieldResult;
    }

    public String getEntityClass() {
        return this.entityClass;
    }

    public void setEntityClass(String value) {
        this.entityClass = value;
    }

    public String getDiscriminatorColumn() {
        return this.discriminatorColumn;
    }

    public void setDiscriminatorColumn(String value) {
        this.discriminatorColumn = value;
    }
}

