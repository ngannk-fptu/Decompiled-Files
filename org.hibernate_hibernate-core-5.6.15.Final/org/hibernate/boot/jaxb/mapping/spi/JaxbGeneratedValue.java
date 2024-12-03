/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.GenerationType
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package org.hibernate.boot.jaxb.mapping.spi;

import java.io.Serializable;
import javax.persistence.GenerationType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.boot.jaxb.mapping.spi.Adapter6;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="generated-value", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
public class JaxbGeneratedValue
implements Serializable {
    @XmlAttribute(name="strategy")
    @XmlJavaTypeAdapter(value=Adapter6.class)
    protected GenerationType strategy;
    @XmlAttribute(name="generator")
    protected String generator;

    public GenerationType getStrategy() {
        return this.strategy;
    }

    public void setStrategy(GenerationType value) {
        this.strategy = value;
    }

    public String getGenerator() {
        return this.generator;
    }

    public void setGenerator(String value) {
        this.generator = value;
    }
}

