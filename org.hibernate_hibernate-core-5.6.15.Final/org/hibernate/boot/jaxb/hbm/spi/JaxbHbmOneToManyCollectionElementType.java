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
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNotFoundEnum;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="OneToManyCollectionElementType", namespace="http://www.hibernate.org/xsd/orm/hbm")
public class JaxbHbmOneToManyCollectionElementType
implements Serializable {
    @XmlAttribute(name="class")
    protected String clazz;
    @XmlAttribute(name="embed-xml")
    protected Boolean embedXml;
    @XmlAttribute(name="entity-name")
    protected String entityName;
    @XmlAttribute(name="node")
    protected String node;
    @XmlAttribute(name="not-found")
    protected JaxbHbmNotFoundEnum notFound;

    public String getClazz() {
        return this.clazz;
    }

    public void setClazz(String value) {
        this.clazz = value;
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

    public String getNode() {
        return this.node;
    }

    public void setNode(String value) {
        this.node = value;
    }

    public JaxbHbmNotFoundEnum getNotFound() {
        if (this.notFound == null) {
            return JaxbHbmNotFoundEnum.EXCEPTION;
        }
        return this.notFound;
    }

    public void setNotFound(JaxbHbmNotFoundEnum value) {
        this.notFound = value;
    }
}

