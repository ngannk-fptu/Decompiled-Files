/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlType
 */
package org.hibernate.boot.jaxb.hbm.spi;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmColumnType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="multi-tenancy-type", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"column", "formula"})
public class JaxbHbmMultiTenancyType
implements Serializable {
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmColumnType column;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected String formula;
    @XmlAttribute(name="column")
    protected String columnAttribute;
    @XmlAttribute(name="formula")
    protected String formulaAttribute;
    @XmlAttribute(name="shared")
    protected Boolean shared;
    @XmlAttribute(name="bind-as-param")
    protected Boolean bindAsParam;

    public JaxbHbmColumnType getColumn() {
        return this.column;
    }

    public void setColumn(JaxbHbmColumnType value) {
        this.column = value;
    }

    public String getFormula() {
        return this.formula;
    }

    public void setFormula(String value) {
        this.formula = value;
    }

    public String getColumnAttribute() {
        return this.columnAttribute;
    }

    public void setColumnAttribute(String value) {
        this.columnAttribute = value;
    }

    public String getFormulaAttribute() {
        return this.formulaAttribute;
    }

    public void setFormulaAttribute(String value) {
        this.formulaAttribute = value;
    }

    public boolean isShared() {
        if (this.shared == null) {
            return true;
        }
        return this.shared;
    }

    public void setShared(Boolean value) {
        this.shared = value;
    }

    public boolean isBindAsParam() {
        if (this.bindAsParam == null) {
            return true;
        }
        return this.bindAsParam;
    }

    public void setBindAsParam(Boolean value) {
        this.bindAsParam = value;
    }
}

