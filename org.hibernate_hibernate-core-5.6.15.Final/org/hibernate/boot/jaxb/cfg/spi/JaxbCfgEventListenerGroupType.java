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
package org.hibernate.boot.jaxb.cfg.spi;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgEventListenerType;
import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgEventTypeEnum;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="EventListenerGroupType", namespace="http://www.hibernate.org/xsd/orm/cfg", propOrder={"listener"})
public class JaxbCfgEventListenerGroupType {
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/cfg")
    protected List<JaxbCfgEventListenerType> listener;
    @XmlAttribute(name="type", required=true)
    protected JaxbCfgEventTypeEnum type;

    public List<JaxbCfgEventListenerType> getListener() {
        if (this.listener == null) {
            this.listener = new ArrayList<JaxbCfgEventListenerType>();
        }
        return this.listener;
    }

    public JaxbCfgEventTypeEnum getType() {
        return this.type;
    }

    public void setType(JaxbCfgEventTypeEnum value) {
        this.type = value;
    }
}

