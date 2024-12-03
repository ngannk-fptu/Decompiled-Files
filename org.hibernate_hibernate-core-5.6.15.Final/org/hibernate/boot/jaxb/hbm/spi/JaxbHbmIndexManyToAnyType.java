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

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="index-many-to-any-type", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"column"})
public class JaxbHbmIndexManyToAnyType
implements Serializable {
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm", required=true)
    protected List<JaxbHbmColumnType> column;
    @XmlAttribute(name="id-type", required=true)
    protected String idType;
    @XmlAttribute(name="meta-type")
    protected String metaType;

    public List<JaxbHbmColumnType> getColumn() {
        if (this.column == null) {
            this.column = new ArrayList<JaxbHbmColumnType>();
        }
        return this.column;
    }

    public String getIdType() {
        return this.idType;
    }

    public void setIdType(String value) {
        this.idType = value;
    }

    public String getMetaType() {
        return this.metaType;
    }

    public void setMetaType(String value) {
        this.metaType = value;
    }
}

