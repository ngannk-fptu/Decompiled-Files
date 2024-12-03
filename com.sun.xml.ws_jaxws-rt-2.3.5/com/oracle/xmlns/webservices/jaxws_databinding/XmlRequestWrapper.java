/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.ws.RequestWrapper
 */
package com.oracle.xmlns.webservices.jaxws_databinding;

import com.oracle.xmlns.webservices.jaxws_databinding.Util;
import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.RequestWrapper;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="request-wrapper")
public class XmlRequestWrapper
implements RequestWrapper {
    @XmlAttribute(name="local-name")
    protected String localName;
    @XmlAttribute(name="target-namespace")
    protected String targetNamespace;
    @XmlAttribute(name="class-name")
    protected String className;
    @XmlAttribute(name="part-name")
    protected String partName;

    public String getLocalName() {
        if (this.localName == null) {
            return "";
        }
        return this.localName;
    }

    public void setLocalName(String value) {
        this.localName = value;
    }

    public String getTargetNamespace() {
        if (this.targetNamespace == null) {
            return "";
        }
        return this.targetNamespace;
    }

    public void setTargetNamespace(String value) {
        this.targetNamespace = value;
    }

    public String getClassName() {
        if (this.className == null) {
            return "";
        }
        return this.className;
    }

    public void setClassName(String value) {
        this.className = value;
    }

    public String getPartName() {
        return this.partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String localName() {
        return Util.nullSafe(this.localName);
    }

    public String targetNamespace() {
        return Util.nullSafe(this.targetNamespace);
    }

    public String className() {
        return Util.nullSafe(this.className);
    }

    public String partName() {
        return Util.nullSafe(this.partName);
    }

    public Class<? extends Annotation> annotationType() {
        return RequestWrapper.class;
    }
}

