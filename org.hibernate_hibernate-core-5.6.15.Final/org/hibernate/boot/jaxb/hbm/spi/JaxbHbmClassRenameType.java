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

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ClassRenameType", namespace="http://www.hibernate.org/xsd/orm/hbm")
public class JaxbHbmClassRenameType
implements Serializable {
    @XmlAttribute(name="class", required=true)
    protected String clazz;
    @XmlAttribute(name="rename")
    protected String rename;

    public String getClazz() {
        return this.clazz;
    }

    public void setClazz(String value) {
        this.clazz = value;
    }

    public String getRename() {
        return this.rename;
    }

    public void setRename(String value) {
        this.rename = value;
    }
}

