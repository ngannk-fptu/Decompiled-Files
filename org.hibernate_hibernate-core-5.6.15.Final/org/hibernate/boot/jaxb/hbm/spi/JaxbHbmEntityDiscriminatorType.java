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
@XmlType(name="EntityDiscriminatorType", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"column", "formula"})
public class JaxbHbmEntityDiscriminatorType
implements Serializable {
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmColumnType column;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected String formula;
    @XmlAttribute(name="column")
    protected String columnAttribute;
    @XmlAttribute(name="force")
    protected Boolean force;
    @XmlAttribute(name="formula")
    protected String formulaAttribute;
    @XmlAttribute(name="insert")
    protected Boolean insert;
    @XmlAttribute(name="length")
    protected Integer length;
    @XmlAttribute(name="not-null")
    protected Boolean notNull;
    @XmlAttribute(name="type")
    protected String type;

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

    public boolean isForce() {
        if (this.force == null) {
            return false;
        }
        return this.force;
    }

    public void setForce(Boolean value) {
        this.force = value;
    }

    public String getFormulaAttribute() {
        return this.formulaAttribute;
    }

    public void setFormulaAttribute(String value) {
        this.formulaAttribute = value;
    }

    public boolean isInsert() {
        if (this.insert == null) {
            return true;
        }
        return this.insert;
    }

    public void setInsert(Boolean value) {
        this.insert = value;
    }

    public Integer getLength() {
        return this.length;
    }

    public void setLength(Integer value) {
        this.length = value;
    }

    public boolean isNotNull() {
        if (this.notNull == null) {
            return true;
        }
        return this.notNull;
    }

    public void setNotNull(Boolean value) {
        this.notNull = value;
    }

    public String getType() {
        if (this.type == null) {
            return "string";
        }
        return this.type;
    }

    public void setType(String value) {
        this.type = value;
    }
}

