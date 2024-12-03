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
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFetchStyleEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmLazyWithNoProxyEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmOnDeleteEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmOuterJoinEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmToolingHintContainer;
import org.hibernate.boot.jaxb.hbm.spi.ToolingHintContainer;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="OneToOneType", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"formula"})
public class JaxbHbmOneToOneType
extends JaxbHbmToolingHintContainer
implements Serializable,
ToolingHintContainer {
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<String> formula;
    @XmlAttribute(name="access")
    protected String access;
    @XmlAttribute(name="cascade")
    protected String cascade;
    @XmlAttribute(name="class")
    protected String clazz;
    @XmlAttribute(name="constrained")
    protected Boolean constrained;
    @XmlAttribute(name="embed-xml")
    protected Boolean embedXml;
    @XmlAttribute(name="entity-name")
    protected String entityName;
    @XmlAttribute(name="fetch")
    protected JaxbHbmFetchStyleEnum fetch;
    @XmlAttribute(name="foreign-key")
    protected String foreignKey;
    @XmlAttribute(name="formula")
    protected String formulaAttribute;
    @XmlAttribute(name="lazy")
    protected JaxbHbmLazyWithNoProxyEnum lazy;
    @XmlAttribute(name="name", required=true)
    protected String name;
    @XmlAttribute(name="node")
    protected String node;
    @XmlAttribute(name="outer-join")
    protected JaxbHbmOuterJoinEnum outerJoin;
    @XmlAttribute(name="property-ref")
    protected String propertyRef;
    @XmlAttribute(name="on-delete")
    protected JaxbHbmOnDeleteEnum onDelete;

    public List<String> getFormula() {
        if (this.formula == null) {
            this.formula = new ArrayList<String>();
        }
        return this.formula;
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

    public String getClazz() {
        return this.clazz;
    }

    public void setClazz(String value) {
        this.clazz = value;
    }

    public boolean isConstrained() {
        if (this.constrained == null) {
            return false;
        }
        return this.constrained;
    }

    public void setConstrained(Boolean value) {
        this.constrained = value;
    }

    public Boolean isEmbedXml() {
        return this.embedXml;
    }

    public void setEmbedXml(Boolean value) {
        this.embedXml = value;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public void setEntityName(String value) {
        this.entityName = value;
    }

    public JaxbHbmFetchStyleEnum getFetch() {
        return this.fetch;
    }

    public void setFetch(JaxbHbmFetchStyleEnum value) {
        this.fetch = value;
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

    public JaxbHbmLazyWithNoProxyEnum getLazy() {
        return this.lazy;
    }

    public void setLazy(JaxbHbmLazyWithNoProxyEnum value) {
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

    public JaxbHbmOuterJoinEnum getOuterJoin() {
        return this.outerJoin;
    }

    public void setOuterJoin(JaxbHbmOuterJoinEnum value) {
        this.outerJoin = value;
    }

    public String getPropertyRef() {
        return this.propertyRef;
    }

    public void setPropertyRef(String value) {
        this.propertyRef = value;
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

