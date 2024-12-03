/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.ws.Service$Mode
 *  javax.xml.ws.ServiceMode
 */
package com.oracle.xmlns.webservices.jaxws_databinding;

import com.oracle.xmlns.webservices.jaxws_databinding.Util;
import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="service-mode")
public class XmlServiceMode
implements ServiceMode {
    @XmlAttribute(name="value")
    protected String value;

    public String getValue() {
        if (this.value == null) {
            return "PAYLOAD";
        }
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Service.Mode value() {
        return Service.Mode.valueOf((String)Util.nullSafe(this.value, "PAYLOAD"));
    }

    public Class<? extends Annotation> annotationType() {
        return ServiceMode.class;
    }
}

