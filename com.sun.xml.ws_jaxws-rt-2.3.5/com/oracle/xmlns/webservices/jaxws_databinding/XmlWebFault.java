/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.ws.WebFault
 */
package com.oracle.xmlns.webservices.jaxws_databinding;

import com.oracle.xmlns.webservices.jaxws_databinding.Util;
import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.WebFault;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="web-fault")
public class XmlWebFault
implements WebFault {
    @XmlAttribute(name="name")
    protected String name;
    @XmlAttribute(name="targetNamespace")
    protected String targetNamespace;
    @XmlAttribute(name="faultBean")
    protected String faultBean;
    @XmlAttribute(name="messageName")
    protected String messageName;

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getTargetNamespace() {
        return this.targetNamespace;
    }

    public void setTargetNamespace(String value) {
        this.targetNamespace = value;
    }

    public String getFaultBean() {
        return this.faultBean;
    }

    public void setFaultBean(String value) {
        this.faultBean = value;
    }

    public String name() {
        return Util.nullSafe(this.name);
    }

    public String targetNamespace() {
        return Util.nullSafe(this.targetNamespace);
    }

    public String faultBean() {
        return Util.nullSafe(this.faultBean);
    }

    public String messageName() {
        return Util.nullSafe(this.messageName);
    }

    public Class<? extends Annotation> annotationType() {
        return WebFault.class;
    }
}

