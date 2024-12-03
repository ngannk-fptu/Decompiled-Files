/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package org.hibernate.boot.jaxb.hbm.spi;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.EntityMode;
import org.hibernate.boot.jaxb.hbm.spi.Adapter4;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="tuplizer-type", namespace="http://www.hibernate.org/xsd/orm/hbm")
public class JaxbHbmTuplizerType
implements Serializable {
    @XmlAttribute(name="class", required=true)
    protected String clazz;
    @XmlAttribute(name="entity-mode")
    @XmlJavaTypeAdapter(value=Adapter4.class)
    protected EntityMode entityMode;

    public String getClazz() {
        return this.clazz;
    }

    public void setClazz(String value) {
        this.clazz = value;
    }

    public EntityMode getEntityMode() {
        return this.entityMode;
    }

    public void setEntityMode(EntityMode value) {
        this.entityMode = value;
    }
}

