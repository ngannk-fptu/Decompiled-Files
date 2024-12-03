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
@XmlType(name="NativeQueryScalarReturnType", namespace="http://www.hibernate.org/xsd/orm/hbm")
public class JaxbHbmNativeQueryScalarReturnType
implements Serializable {
    @XmlAttribute(name="column", required=true)
    protected String column;
    @XmlAttribute(name="type")
    protected String type;

    public String getColumn() {
        return this.column;
    }

    public void setColumn(String value) {
        this.column = value;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String value) {
        this.type = value;
    }
}

