/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.XmlValue
 */
package org.hibernate.boot.jaxb.hbm.spi;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ToolingHintType", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"value"})
public class JaxbHbmToolingHintType
implements Serializable {
    @XmlValue
    protected String value;
    @XmlAttribute(name="attribute", required=true)
    protected String name;
    @XmlAttribute(name="inherit")
    protected Boolean inheritable;

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public boolean isInheritable() {
        if (this.inheritable == null) {
            return true;
        }
        return this.inheritable;
    }

    public void setInheritable(Boolean value) {
        this.inheritable = value;
    }
}

