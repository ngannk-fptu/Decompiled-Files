/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.jws.WebParam
 *  javax.jws.WebParam$Mode
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlType
 */
package com.oracle.xmlns.webservices.jaxws_databinding;

import com.oracle.xmlns.webservices.jaxws_databinding.Util;
import com.oracle.xmlns.webservices.jaxws_databinding.WebParamMode;
import java.lang.annotation.Annotation;
import javax.jws.WebParam;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="web-param")
public class XmlWebParam
implements WebParam {
    @XmlAttribute(name="header")
    protected Boolean header;
    @XmlAttribute(name="mode")
    protected WebParamMode mode;
    @XmlAttribute(name="name")
    protected String name;
    @XmlAttribute(name="part-name")
    protected String partName;
    @XmlAttribute(name="target-namespace")
    protected String targetNamespace;

    public boolean isHeader() {
        if (this.header == null) {
            return false;
        }
        return this.header;
    }

    public void setHeader(Boolean value) {
        this.header = value;
    }

    public WebParamMode getMode() {
        if (this.mode == null) {
            return WebParamMode.IN;
        }
        return this.mode;
    }

    public void setMode(WebParamMode value) {
        this.mode = value;
    }

    public String getName() {
        if (this.name == null) {
            return "";
        }
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getPartName() {
        if (this.partName == null) {
            return "";
        }
        return this.partName;
    }

    public void setPartName(String value) {
        this.partName = value;
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

    public String name() {
        return Util.nullSafe(this.name);
    }

    public String partName() {
        return Util.nullSafe(this.partName);
    }

    public String targetNamespace() {
        return Util.nullSafe(this.targetNamespace);
    }

    public WebParam.Mode mode() {
        return Util.nullSafe(this.mode, WebParam.Mode.IN);
    }

    public boolean header() {
        return Util.nullSafe(this.header, Boolean.valueOf(false));
    }

    public Class<? extends Annotation> annotationType() {
        return WebParam.class;
    }
}

