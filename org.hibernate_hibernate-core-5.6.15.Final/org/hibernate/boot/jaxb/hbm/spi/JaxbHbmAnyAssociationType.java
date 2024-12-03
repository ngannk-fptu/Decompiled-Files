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
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmAnyValueMappingType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmColumnType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmToolingHintContainer;
import org.hibernate.boot.jaxb.hbm.spi.ToolingHintContainer;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="AnyAssociationType", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"metaValue", "column"})
public class JaxbHbmAnyAssociationType
extends JaxbHbmToolingHintContainer
implements Serializable,
ToolingHintContainer {
    @XmlElement(name="meta-value", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmAnyValueMappingType> metaValue;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm", required=true)
    protected List<JaxbHbmColumnType> column;
    @XmlAttribute(name="access")
    protected String access;
    @XmlAttribute(name="cascade")
    protected String cascade;
    @XmlAttribute(name="id-type", required=true)
    protected String idType;
    @XmlAttribute(name="index")
    protected String index;
    @XmlAttribute(name="insert")
    protected Boolean insert;
    @XmlAttribute(name="lazy")
    protected Boolean lazy;
    @XmlAttribute(name="meta-type")
    protected String metaType;
    @XmlAttribute(name="name", required=true)
    protected String name;
    @XmlAttribute(name="node")
    protected String node;
    @XmlAttribute(name="optimistic-lock")
    protected Boolean optimisticLock;
    @XmlAttribute(name="update")
    protected Boolean update;

    public List<JaxbHbmAnyValueMappingType> getMetaValue() {
        if (this.metaValue == null) {
            this.metaValue = new ArrayList<JaxbHbmAnyValueMappingType>();
        }
        return this.metaValue;
    }

    public List<JaxbHbmColumnType> getColumn() {
        if (this.column == null) {
            this.column = new ArrayList<JaxbHbmColumnType>();
        }
        return this.column;
    }

    public String getAccess() {
        return this.access;
    }

    public void setAccess(String value) {
        this.access = value;
    }

    public String getCascade() {
        return this.cascade;
    }

    public void setCascade(String value) {
        this.cascade = value;
    }

    public String getIdType() {
        return this.idType;
    }

    public void setIdType(String value) {
        this.idType = value;
    }

    public String getIndex() {
        return this.index;
    }

    public void setIndex(String value) {
        this.index = value;
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

    public boolean isLazy() {
        if (this.lazy == null) {
            return false;
        }
        return this.lazy;
    }

    public void setLazy(Boolean value) {
        this.lazy = value;
    }

    public String getMetaType() {
        return this.metaType;
    }

    public void setMetaType(String value) {
        this.metaType = value;
    }

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

    public boolean isOptimisticLock() {
        if (this.optimisticLock == null) {
            return true;
        }
        return this.optimisticLock;
    }

    public void setOptimisticLock(Boolean value) {
        this.optimisticLock = value;
    }

    public boolean isUpdate() {
        if (this.update == null) {
            return true;
        }
        return this.update;
    }

    public void setUpdate(Boolean value) {
        this.update = value;
    }
}

