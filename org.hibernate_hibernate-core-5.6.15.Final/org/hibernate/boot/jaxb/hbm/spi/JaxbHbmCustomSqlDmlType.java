/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.XmlValue
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package org.hibernate.boot.jaxb.hbm.spi;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.boot.jaxb.hbm.spi.Adapter3;
import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="CustomSqlDmlType", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"value"})
public class JaxbHbmCustomSqlDmlType
implements Serializable {
    @XmlValue
    protected String value;
    @XmlAttribute(name="callable")
    protected Boolean callable;
    @XmlAttribute(name="check")
    @XmlJavaTypeAdapter(value=Adapter3.class)
    protected ExecuteUpdateResultCheckStyle check;

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isCallable() {
        if (this.callable == null) {
            return false;
        }
        return this.callable;
    }

    public void setCallable(Boolean value) {
        this.callable = value;
    }

    public ExecuteUpdateResultCheckStyle getCheck() {
        return this.check;
    }

    public void setCheck(ExecuteUpdateResultCheckStyle value) {
        this.check = value;
    }
}

