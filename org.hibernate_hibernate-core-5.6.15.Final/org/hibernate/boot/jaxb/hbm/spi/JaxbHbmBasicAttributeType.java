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
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.boot.jaxb.hbm.spi.Adapter6;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmColumnType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmToolingHintContainer;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmTypeSpecificationType;
import org.hibernate.boot.jaxb.hbm.spi.SimpleValueTypeInfo;
import org.hibernate.boot.jaxb.hbm.spi.SingularAttributeInfo;
import org.hibernate.boot.jaxb.hbm.spi.ToolingHintContainer;
import org.hibernate.boot.jaxb.hbm.spi.TypeContainer;
import org.hibernate.tuple.GenerationTiming;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="BasicAttributeType", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"columnOrFormula", "type"})
public class JaxbHbmBasicAttributeType
extends JaxbHbmToolingHintContainer
implements Serializable,
SimpleValueTypeInfo,
SingularAttributeInfo,
ToolingHintContainer,
TypeContainer {
    @XmlElements(value={@XmlElement(name="column", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmColumnType.class), @XmlElement(name="formula", namespace="http://www.hibernate.org/xsd/orm/hbm", type=String.class)})
    protected List<Serializable> columnOrFormula;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmTypeSpecificationType type;
    @XmlAttribute(name="access")
    protected String access;
    @XmlAttribute(name="column")
    protected String columnAttribute;
    @XmlAttribute(name="formula")
    protected String formulaAttribute;
    @XmlAttribute(name="generated")
    @XmlJavaTypeAdapter(value=Adapter6.class)
    protected GenerationTiming generated;
    @XmlAttribute(name="index")
    protected String index;
    @XmlAttribute(name="insert")
    protected Boolean insert;
    @XmlAttribute(name="lazy")
    protected Boolean lazy;
    @XmlAttribute(name="length")
    protected Integer length;
    @XmlAttribute(name="name", required=true)
    protected String name;
    @XmlAttribute(name="node")
    protected String node;
    @XmlAttribute(name="not-null")
    protected Boolean notNull;
    @XmlAttribute(name="optimistic-lock")
    protected Boolean optimisticLock;
    @XmlAttribute(name="precision")
    protected String precision;
    @XmlAttribute(name="scale")
    protected String scale;
    @XmlAttribute(name="type")
    protected String typeAttribute;
    @XmlAttribute(name="unique")
    protected Boolean unique;
    @XmlAttribute(name="unique-key")
    protected String uniqueKey;
    @XmlAttribute(name="update")
    protected Boolean update;

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

    @Override
    public String getAccess() {
        return this.access;
    }

    public void setAccess(String value) {
        this.access = value;
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

    public GenerationTiming getGenerated() {
        if (this.generated == null) {
            return new Adapter6().unmarshal("never");
        }
        return this.generated;
    }

    public void setGenerated(GenerationTiming value) {
        this.generated = value;
    }

    public String getIndex() {
        return this.index;
    }

    public void setIndex(String value) {
        this.index = value;
    }

    public Boolean isInsert() {
        return this.insert;
    }

    public void setInsert(Boolean value) {
        this.insert = value;
    }

    public boolean isLazy() {
        if (this.lazy == null) {
            return false;
        }
        return this.lazy;
    }

    public void setLazy(Boolean value) {
        this.lazy = value;
    }

    public Integer getLength() {
        return this.length;
    }

    public void setLength(Integer value) {
        this.length = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getNode() {
        return this.node;
    }

    public void setNode(String value) {
        this.node = value;
    }

    public Boolean isNotNull() {
        return this.notNull;
    }

    public void setNotNull(Boolean value) {
        this.notNull = value;
    }

    public boolean isOptimisticLock() {
        if (this.optimisticLock == null) {
            return true;
        }
        return this.optimisticLock;
    }

    public void setOptimisticLock(Boolean value) {
        this.optimisticLock = value;
    }

    public String getPrecision() {
        return this.precision;
    }

    public void setPrecision(String value) {
        this.precision = value;
    }

    public String getScale() {
        return this.scale;
    }

    public void setScale(String value) {
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

    public String getUniqueKey() {
        return this.uniqueKey;
    }

    public void setUniqueKey(String value) {
        this.uniqueKey = value;
    }

    public Boolean isUpdate() {
        return this.update;
    }

    public void setUpdate(Boolean value) {
        this.update = value;
    }
}

