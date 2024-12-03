/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlType
 */
package org.hibernate.boot.jaxb.hbm.spi;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmEntityBaseDefinition;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="SubclassEntityBaseDefinition", namespace="http://www.hibernate.org/xsd/orm/hbm")
public abstract class JaxbHbmSubclassEntityBaseDefinition
extends JaxbHbmEntityBaseDefinition
implements Serializable {
    @XmlAttribute(name="extends")
    protected String _extends;

    public String getExtends() {
        return this._extends;
    }

    public void setExtends(String value) {
        this._extends = value;
    }
}

