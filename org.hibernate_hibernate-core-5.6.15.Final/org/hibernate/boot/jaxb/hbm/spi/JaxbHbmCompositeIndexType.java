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
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCompositeKeyBasicAttributeType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCompositeKeyManyToOneType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmToolingHintContainer;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="composite-index-type", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"attributes"})
public class JaxbHbmCompositeIndexType
implements Serializable {
    @XmlElements(value={@XmlElement(name="key-property", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmCompositeKeyBasicAttributeType.class), @XmlElement(name="key-many-to-one", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmCompositeKeyManyToOneType.class)})
    protected List<JaxbHbmToolingHintContainer> attributes;
    @XmlAttribute(name="class", required=true)
    protected String clazz;

    public List<JaxbHbmToolingHintContainer> getAttributes() {
        if (this.attributes == null) {
            this.attributes = new ArrayList<JaxbHbmToolingHintContainer>();
        }
        return this.attributes;
    }

    public String getClazz() {
        return this.clazz;
    }

    public void setClazz(String value) {
        this.clazz = value;
    }
}

