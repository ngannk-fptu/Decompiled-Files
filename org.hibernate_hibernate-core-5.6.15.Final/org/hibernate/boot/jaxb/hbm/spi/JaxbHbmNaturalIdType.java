/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlElements
 *  javax.xml.bind.annotation.XmlType
 */
package org.hibernate.boot.jaxb.hbm.spi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmAnyAssociationType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmBasicAttributeType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCompositeAttributeType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmDynamicComponentType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmManyToOneType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="natural-id-type", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"attributes"})
public class JaxbHbmNaturalIdType
implements Serializable {
    @XmlElements(value={@XmlElement(name="property", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmBasicAttributeType.class), @XmlElement(name="many-to-one", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmManyToOneType.class), @XmlElement(name="component", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmCompositeAttributeType.class), @XmlElement(name="dynamic-component", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmDynamicComponentType.class), @XmlElement(name="any", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmAnyAssociationType.class)})
    protected List<Serializable> attributes;
    @XmlAttribute(name="mutable")
    protected Boolean mutable;

    public List<Serializable> getAttributes() {
        if (this.attributes == null) {
            this.attributes = new ArrayList<Serializable>();
        }
        return this.attributes;
    }

    public boolean isMutable() {
        if (this.mutable == null) {
            return false;
        }
        return this.mutable;
    }

    public void setMutable(Boolean value) {
        this.mutable = value;
    }
}

