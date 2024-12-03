/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.ws.FaultAction
 */
package com.oracle.xmlns.webservices.jaxws_databinding;

import com.oracle.xmlns.webservices.jaxws_databinding.Util;
import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.FaultAction;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="fault-action")
public class XmlFaultAction
implements FaultAction {
    @XmlAttribute(name="className", required=true)
    protected String className;
    @XmlAttribute(name="value")
    protected String value;

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String value) {
        this.className = value;
    }

    public String getValue() {
        return Util.nullSafe(this.value);
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Class<? extends Exception> className() {
        return Util.findClass(this.className);
    }

    public String value() {
        return Util.nullSafe(this.value);
    }

    public Class<? extends Annotation> annotationType() {
        return FaultAction.class;
    }
}

