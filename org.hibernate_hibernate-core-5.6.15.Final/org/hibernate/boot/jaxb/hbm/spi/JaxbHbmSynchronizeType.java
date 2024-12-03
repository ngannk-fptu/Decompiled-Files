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
@XmlType(name="synchronize-type", namespace="http://www.hibernate.org/xsd/orm/hbm")
public class JaxbHbmSynchronizeType
implements Serializable {
    @XmlAttribute(name="table", required=true)
    protected String table;

    public String getTable() {
        return this.table;
    }

    public void setTable(String value) {
        this.table = value;
    }
}

