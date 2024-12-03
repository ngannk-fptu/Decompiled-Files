/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlType
 */
package org.hibernate.boot.jaxb.hbm.spi;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.boot.jaxb.hbm.spi.EntityInfo;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmToolingHintContainer;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="EntityBaseDefinition", namespace="http://www.hibernate.org/xsd/orm/hbm")
public abstract class JaxbHbmEntityBaseDefinition
extends JaxbHbmToolingHintContainer
implements Serializable,
EntityInfo {
    @XmlAttribute(name="name")
    protected String name;
    @XmlAttribute(name="entity-name")
    protected String entityName;
    @XmlAttribute(name="abstract")
    protected Boolean _abstract;
    @XmlAttribute(name="lazy")
    protected Boolean lazy;
    @XmlAttribute(name="proxy")
    protected String proxy;
    @XmlAttribute(name="batch-size")
    protected Integer batchSize;
    @XmlAttribute(name="dynamic-insert")
    protected Boolean dynamicInsert;
    @XmlAttribute(name="dynamic-update")
    protected Boolean dynamicUpdate;
    @XmlAttribute(name="select-before-update")
    protected Boolean selectBeforeUpdate;
    @XmlAttribute(name="node")
    protected String node;
    @XmlAttribute(name="persister")
    protected String persister;

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    @Override
    public String getEntityName() {
        return this.entityName;
    }

    public void setEntityName(String value) {
        this.entityName = value;
    }

    @Override
    public Boolean isAbstract() {
        return this._abstract;
    }

    public void setAbstract(Boolean value) {
        this._abstract = value;
    }

    @Override
    public Boolean isLazy() {
        return this.lazy;
    }

    public void setLazy(Boolean value) {
        this.lazy = value;
    }

    @Override
    public String getProxy() {
        return this.proxy;
    }

    public void setProxy(String value) {
        this.proxy = value;
    }

    @Override
    public int getBatchSize() {
        if (this.batchSize == null) {
            return -1;
        }
        return this.batchSize;
    }

    public void setBatchSize(Integer value) {
        this.batchSize = value;
    }

    @Override
    public boolean isDynamicInsert() {
        if (this.dynamicInsert == null) {
            return false;
        }
        return this.dynamicInsert;
    }

    public void setDynamicInsert(Boolean value) {
        this.dynamicInsert = value;
    }

    @Override
    public boolean isDynamicUpdate() {
        if (this.dynamicUpdate == null) {
            return false;
        }
        return this.dynamicUpdate;
    }

    public void setDynamicUpdate(Boolean value) {
        this.dynamicUpdate = value;
    }

    @Override
    public boolean isSelectBeforeUpdate() {
        if (this.selectBeforeUpdate == null) {
            return false;
        }
        return this.selectBeforeUpdate;
    }

    public void setSelectBeforeUpdate(Boolean value) {
        this.selectBeforeUpdate = value;
    }

    public String getNode() {
        return this.node;
    }

    public void setNode(String value) {
        this.node = value;
    }

    @Override
    public String getPersister() {
        return this.persister;
    }

    public void setPersister(String value) {
        this.persister = value;
    }
}

