/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.InheritanceType
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package org.hibernate.boot.jaxb.mapping.spi;

import java.io.Serializable;
import javax.persistence.InheritanceType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.boot.jaxb.mapping.spi.Adapter7;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="inheritance", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
public class JaxbInheritance
implements Serializable {
    @XmlAttribute(name="strategy")
    @XmlJavaTypeAdapter(value=Adapter7.class)
    protected InheritanceType strategy;

    public InheritanceType getStrategy() {
        return this.strategy;
    }

    public void setStrategy(InheritanceType value) {
        this.strategy = value;
    }
}

