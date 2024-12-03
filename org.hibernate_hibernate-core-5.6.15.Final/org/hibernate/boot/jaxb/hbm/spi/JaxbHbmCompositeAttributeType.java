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
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmAnyAssociationType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmArrayType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmBagCollectionType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmBasicAttributeType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmDynamicComponentType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmListType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmManyToOneType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmMapType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmOneToOneType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmParentType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmPrimitiveArrayType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmPropertiesType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmSetType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmToolingHintContainer;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmTuplizerType;
import org.hibernate.boot.jaxb.hbm.spi.ToolingHintContainer;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="CompositeAttributeType", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"tuplizer", "parent", "attributes"})
public class JaxbHbmCompositeAttributeType
extends JaxbHbmToolingHintContainer
implements Serializable,
ToolingHintContainer {
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmTuplizerType> tuplizer;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmParentType parent;
    @XmlElements(value={@XmlElement(name="property", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmBasicAttributeType.class), @XmlElement(name="many-to-one", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmManyToOneType.class), @XmlElement(name="one-to-one", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmOneToOneType.class), @XmlElement(name="component", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmCompositeAttributeType.class), @XmlElement(name="dynamic-component", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmDynamicComponentType.class), @XmlElement(name="properties", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmPropertiesType.class), @XmlElement(name="any", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmAnyAssociationType.class), @XmlElement(name="map", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmMapType.class), @XmlElement(name="set", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmSetType.class), @XmlElement(name="list", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmListType.class), @XmlElement(name="bag", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmBagCollectionType.class), @XmlElement(name="array", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmArrayType.class), @XmlElement(name="primitive-array", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmPrimitiveArrayType.class)})
    protected List<Serializable> attributes;
    @XmlAttribute(name="access")
    protected String access;
    @XmlAttribute(name="class")
    protected String clazz;
    @XmlAttribute(name="insert")
    protected Boolean insert;
    @XmlAttribute(name="lazy")
    protected Boolean lazy;
    @XmlAttribute(name="name", required=true)
    protected String name;
    @XmlAttribute(name="node")
    protected String node;
    @XmlAttribute(name="optimistic-lock")
    protected Boolean optimisticLock;
    @XmlAttribute(name="unique")
    protected Boolean unique;
    @XmlAttribute(name="update")
    protected Boolean update;

    public List<JaxbHbmTuplizerType> getTuplizer() {
        if (this.tuplizer == null) {
            this.tuplizer = new ArrayList<JaxbHbmTuplizerType>();
        }
        return this.tuplizer;
    }

    public JaxbHbmParentType getParent() {
        return this.parent;
    }

    public void setParent(JaxbHbmParentType value) {
        this.parent = value;
    }

    public List<Serializable> getAttributes() {
        if (this.attributes == null) {
            this.attributes = new ArrayList<Serializable>();
        }
        return this.attributes;
    }

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

    public boolean isUnique() {
        if (this.unique == null) {
            return false;
        }
        return this.unique;
    }

    public void setUnique(Boolean value) {
        this.unique = value;
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

