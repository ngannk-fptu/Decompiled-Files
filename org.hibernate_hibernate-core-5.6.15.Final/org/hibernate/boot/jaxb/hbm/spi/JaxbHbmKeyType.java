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
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmOnDeleteEnum;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="key-type", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"column"})
public class JaxbHbmKeyType
implements Serializable {
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmColumnType> column;
    @XmlAttribute(name="column")
    protected String columnAttribute;
    @XmlAttribute(name="foreign-key")
    protected String foreignKey;
    @XmlAttribute(name="not-null")
    protected Boolean notNull;
    @XmlAttribute(name="on-delete")
    protected JaxbHbmOnDeleteEnum onDelete;
    @XmlAttribute(name="property-ref")
    protected String propertyRef;
    @XmlAttribute(name="unique")
    protected Boolean unique;
    @XmlAttribute(name="update")
    protected Boolean update;

    public List<JaxbHbmColumnType> getColumn() {
        if (this.column == null) {
            this.column = new ArrayList<JaxbHbmColumnType>();
        }
        return this.column;
    }

    public String getColumnAttribute() {
        return this.columnAttribute;
    }

    public void setColumnAttribute(String value) {
        this.columnAttribute = value;
    }

    public String getForeignKey() {
        return this.foreignKey;
    }

    public void setForeignKey(String value) {
        this.foreignKey = value;
    }

    public Boolean isNotNull() {
        return this.notNull;
    }

    public void setNotNull(Boolean value) {
        this.notNull = value;
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

    public String getPropertyRef() {
        return this.propertyRef;
    }

    public void setPropertyRef(String value) {
        this.propertyRef = value;
    }

    public Boolean isUnique() {
        return this.unique;
    }

    public void setUnique(Boolean value) {
        this.unique = value;
    }

    public Boolean isUpdate() {
        return this.update;
    }

    public void setUpdate(Boolean value) {
        this.update = value;
    }
}

