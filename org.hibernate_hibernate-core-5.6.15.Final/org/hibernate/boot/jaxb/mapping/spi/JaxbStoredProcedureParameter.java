/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.ParameterMode
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package org.hibernate.boot.jaxb.mapping.spi;

import java.io.Serializable;
import javax.persistence.ParameterMode;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.boot.jaxb.mapping.spi.Adapter9;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="stored-procedure-parameter", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"description"})
public class JaxbStoredProcedureParameter
implements Serializable {
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected String description;
    @XmlAttribute(name="name")
    protected String name;
    @XmlAttribute(name="class", required=true)
    protected String clazz;
    @XmlAttribute(name="mode")
    @XmlJavaTypeAdapter(value=Adapter9.class)
    protected ParameterMode mode;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
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

    public ParameterMode getMode() {
        return this.mode;
    }

    public void setMode(ParameterMode value) {
        this.mode = value;
    }
}

