/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlType
 */
package org.hibernate.boot.jaxb.cfg.spi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgEventTypeEnum;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="EventListenerType", namespace="http://www.hibernate.org/xsd/orm/cfg")
public class JaxbCfgEventListenerType {
    @XmlAttribute(name="class", required=true)
    protected String clazz;
    @XmlAttribute(name="type")
    protected JaxbCfgEventTypeEnum type;

    public String getClazz() {
        return this.clazz;
    }

    public void setClazz(String value) {
        this.clazz = value;
    }

    public JaxbCfgEventTypeEnum getType() {
        return this.type;
    }

    public void setType(JaxbCfgEventTypeEnum value) {
        this.type = value;
    }
}

