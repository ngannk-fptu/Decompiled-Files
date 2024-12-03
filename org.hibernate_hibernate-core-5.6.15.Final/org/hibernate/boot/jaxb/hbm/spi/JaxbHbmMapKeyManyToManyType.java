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
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmColumnType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="MapKeyManyToManyType", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"columnOrFormula"})
public class JaxbHbmMapKeyManyToManyType
implements Serializable {
    @XmlElements(value={@XmlElement(name="column", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmColumnType.class), @XmlElement(name="formula", namespace="http://www.hibernate.org/xsd/orm/hbm", type=String.class)})
    protected List<Serializable> columnOrFormula;
    @XmlAttribute(name="class")
    protected String clazz;
    @XmlAttribute(name="column")
    protected String columnAttribute;
    @XmlAttribute(name="entity-name")
    protected String entityName;
    @XmlAttribute(name="foreign-key")
    protected String foreignKey;
    @XmlAttribute(name="formula")
    protected String formulaAttribute;

    public List<Serializable> getColumnOrFormula() {
        if (this.columnOrFormula == null) {
            this.columnOrFormula = new ArrayList<Serializable>();
        }
        return this.columnOrFormula;
    }

    public String getClazz() {
        return this.clazz;
    }

    public void setClazz(String value) {
        this.clazz = value;
    }

    public String getColumnAttribute() {
        return this.columnAttribute;
    }

    public void setColumnAttribute(String value) {
        this.columnAttribute = value;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public void setEntityName(String value) {
        this.entityName = value;
    }

    public String getForeignKey() {
        return this.foreignKey;
    }

    public void setForeignKey(String value) {
        this.foreignKey = value;
    }

    public String getFormulaAttribute() {
        return this.formulaAttribute;
    }

    public void setFormulaAttribute(String value) {
        this.formulaAttribute = value;
    }
}

