/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.jws.WebMethod
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlType
 */
package com.oracle.xmlns.webservices.jaxws_databinding;

import com.oracle.xmlns.webservices.jaxws_databinding.Util;
import java.lang.annotation.Annotation;
import javax.jws.WebMethod;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="web-method")
public class XmlWebMethod
implements WebMethod {
    @XmlAttribute(name="action")
    protected String action;
    @XmlAttribute(name="exclude")
    protected Boolean exclude;
    @XmlAttribute(name="operation-name")
    protected String operationName;

    public String getAction() {
        if (this.action == null) {
            return "";
        }
        return this.action;
    }

    public void setAction(String value) {
        this.action = value;
    }

    public boolean isExclude() {
        if (this.exclude == null) {
            return false;
        }
        return this.exclude;
    }

    public void setExclude(Boolean value) {
        this.exclude = value;
    }

    public String getOperationName() {
        if (this.operationName == null) {
            return "";
        }
        return this.operationName;
    }

    public void setOperationName(String value) {
        this.operationName = value;
    }

    public String operationName() {
        return Util.nullSafe(this.operationName);
    }

    public String action() {
        return Util.nullSafe(this.action);
    }

    public boolean exclude() {
        return Util.nullSafe(this.exclude, Boolean.valueOf(false));
    }

    public Class<? extends Annotation> annotationType() {
        return WebMethod.class;
    }
}

