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
@XmlType(name="filter-alias-mapping-type", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"value"})
public class JaxbHbmFilterAliasMappingType
implements Serializable {
    @XmlValue
    protected String value;
    @XmlAttribute(name="alias", required=true)
    protected String alias;
    @XmlAttribute(name="table")
    protected String table;
    @XmlAttribute(name="entity")
    protected String entity;

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String value) {
        this.alias = value;
    }

    public String getTable() {
        return this.table;
    }

    public void setTable(String value) {
        this.table = value;
    }

    public String getEntity() {
        return this.entity;
    }

    public void setEntity(String value) {
        this.entity = value;
    }
}

