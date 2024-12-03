/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.ws.soap.MTOM
 */
package com.oracle.xmlns.webservices.jaxws_databinding;

import com.oracle.xmlns.webservices.jaxws_databinding.Util;
import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.soap.MTOM;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="mtom")
public class XmlMTOM
implements MTOM {
    @XmlAttribute(name="enabled")
    protected Boolean enabled;
    @XmlAttribute(name="threshold")
    protected Integer threshold;

    public boolean isEnabled() {
        if (this.enabled == null) {
            return true;
        }
        return this.enabled;
    }

    public void setEnabled(Boolean value) {
        this.enabled = value;
    }

    public int getThreshold() {
        if (this.threshold == null) {
            return 0;
        }
        return this.threshold;
    }

    public void setThreshold(Integer value) {
        this.threshold = value;
    }

    public boolean enabled() {
        return Util.nullSafe(this.enabled, Boolean.TRUE);
    }

    public int threshold() {
        return Util.nullSafe(this.threshold, Integer.valueOf(0));
    }

    public Class<? extends Annotation> annotationType() {
        return MTOM.class;
    }
}

