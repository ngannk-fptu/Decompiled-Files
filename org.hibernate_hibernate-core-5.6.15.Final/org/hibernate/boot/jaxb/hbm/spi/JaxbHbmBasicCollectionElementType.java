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
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmTypeSpecificationType;
import org.hibernate.boot.jaxb.hbm.spi.TypeContainer;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="BasicCollectionElementType", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"columnOrFormula", "type"})
public class JaxbHbmBasicCollectionElementType
implements Serializable,
TypeContainer {
    @XmlElements(value={@XmlElement(name="column", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmColumnType.class), @XmlElement(name="formula", namespace="http://www.hibernate.org/xsd/orm/hbm", type=String.class)})
    protected List<Serializable> columnOrFormula;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmTypeSpecificationType type;
    @XmlAttribute(name="column")
    protected String columnAttribute;
    @XmlAttribute(name="formula")
    protected String formulaAttribute;
    @XmlAttribute(name="length")
    protected Integer length;
    @XmlAttribute(name="node")
    protected String node;
    @XmlAttribute(name="not-null")
    protected Boolean notNull;
    @XmlAttribute(name="precision")
    protected Integer precision;
    @XmlAttribute(name="scale")
    protected Integer scale;
    @XmlAttribute(name="type")
    protected String typeAttribute;
    @XmlAttribute(name="unique")
    protected Boolean unique;

    public List<Serializable> getColumnOrFormula() {
        if (this.columnOrFormula == null) {
            this.columnOrFormula = new ArrayList<Serializable>();
        }
        return this.columnOrFormula;
    }

    @Override
    public JaxbHbmTypeSpecificationType getType() {
        return this.type;
    }

    public void setType(JaxbHbmTypeSpecificationType value) {
        this.type = value;
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

    public Integer getLength() {
        return this.length;
    }

    public void setLength(Integer value) {
        this.length = value;
    }

    public String getNode() {
        return this.node;
    }

    public void setNode(String value) {
        this.node = value;
    }

    public boolean isNotNull() {
        if (this.notNull == null) {
            return false;
        }
        return this.notNull;
    }

    public void setNotNull(Boolean value) {
        this.notNull = value;
    }

    public Integer getPrecision() {
        return this.precision;
    }

    public void setPrecision(Integer value) {
        this.precision = value;
    }

    public Integer getScale() {
        return this.scale;
    }

    public void setScale(Integer value) {
        this.scale = value;
    }

    @Override
    public String getTypeAttribute() {
        return this.typeAttribute;
    }

    public void setTypeAttribute(String value) {
        this.typeAttribute = value;
    }

    public boolean isUnique() {
        if (this.unique == null) {
            return false;
        }
        return this.unique;
    }

    public void setUnique(Boolean value) {
        this.unique = value;
    }
}

