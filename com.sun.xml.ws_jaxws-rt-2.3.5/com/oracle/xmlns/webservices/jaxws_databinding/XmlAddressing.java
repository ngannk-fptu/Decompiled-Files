/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.ws.soap.Addressing
 *  javax.xml.ws.soap.AddressingFeature$Responses
 */
package com.oracle.xmlns.webservices.jaxws_databinding;

import com.oracle.xmlns.webservices.jaxws_databinding.Util;
import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.soap.Addressing;
import javax.xml.ws.soap.AddressingFeature;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="addressing")
public class XmlAddressing
implements Addressing {
    @XmlAttribute(name="enabled")
    protected Boolean enabled;
    @XmlAttribute(name="required")
    protected Boolean required;

    public Boolean getEnabled() {
        return this.enabled();
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getRequired() {
        return this.required();
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public boolean enabled() {
        return Util.nullSafe(this.enabled, Boolean.valueOf(true));
    }

    public boolean required() {
        return Util.nullSafe(this.required, Boolean.valueOf(false));
    }

    public AddressingFeature.Responses responses() {
        return AddressingFeature.Responses.ALL;
    }

    public Class<? extends Annotation> annotationType() {
        return Addressing.class;
    }
}

