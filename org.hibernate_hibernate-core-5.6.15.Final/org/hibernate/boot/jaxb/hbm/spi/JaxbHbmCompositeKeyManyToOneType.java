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
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmColumnType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmLazyEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmOnDeleteEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmToolingHintContainer;
import org.hibernate.boot.jaxb.hbm.spi.SingularAttributeInfo;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="CompositeKeyManyToOneType", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"column"})
public class JaxbHbmCompositeKeyManyToOneType
extends JaxbHbmToolingHintContainer
implements Serializable,
SingularAttributeInfo {
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmColumnType> column;
    @XmlAttribute(name="access")
    protected String access;
    @XmlAttribute(name="class")
    protected String clazz;
    @XmlAttribute(name="column")
    protected String columnAttribute;
    @XmlAttribute(name="entity-name")
    protected String entityName;
    @XmlAttribute(name="foreign-key")
    protected String foreignKey;
    @XmlAttribute(name="lazy")
    protected JaxbHbmLazyEnum lazy;
    @XmlAttribute(name="name", required=true)
    protected String name;
    @XmlAttribute(name="on-delete")
    protected JaxbHbmOnDeleteEnum onDelete;

    public List<JaxbHbmColumnType> getColumn() {
        if (this.column == null) {
            this.column = new ArrayList<JaxbHbmColumnType>();
        }
        return this.column;
    }

    @Override
    public String getAccess() {
        return this.access;
    }

    public void setAccess(String value) {
        this.access = value;
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

    public JaxbHbmLazyEnum getLazy() {
        return this.lazy;
    }

    public void setLazy(JaxbHbmLazyEnum value) {
        this.lazy = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public JaxbHbmOnDeleteEnum getOnDelete() {
        if (this.onDelete == null) {
            return JaxbHbmOnDeleteEnum.NOACTION;
        }
        return this.onDelete;
    }

    public void setOnDelete(JaxbHbmOnDeleteEnum value) {
        this.onDelete = value;
    }
}

