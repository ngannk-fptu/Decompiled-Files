/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.ws.Service
 *  javax.xml.ws.WebServiceRef
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
import javax.xml.ws.WebServiceRef;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="web-service-ref")
public class XmlWebServiceRef
implements WebServiceRef {
    @XmlAttribute(name="name")
    protected String name;
    @XmlAttribute(name="type")
    protected String type;
    @XmlAttribute(name="mappedName")
    protected String mappedName;
    @XmlAttribute(name="value")
    protected String value;
    @XmlAttribute(name="wsdlLocation")
    protected String wsdlLocation;
    @XmlAttribute(name="lookup")
    protected String lookup;

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String value) {
        this.type = value;
    }

    public String getMappedName() {
        return this.mappedName;
    }

    public void setMappedName(String value) {
        this.mappedName = value;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getWsdlLocation() {
        return this.wsdlLocation;
    }

    public void setWsdlLocation(String value) {
        this.wsdlLocation = value;
    }

    public String getLookup() {
        return this.lookup;
    }

    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

    public String name() {
        return Util.nullSafe(this.name);
    }

    public Class<?> type() {
        if (this.type == null) {
            return Object.class;
        }
        return Util.findClass(this.type);
    }

    public String mappedName() {
        return Util.nullSafe(this.mappedName);
    }

    public Class<? extends Service> value() {
        if (this.value == null) {
            return Service.class;
        }
        return Util.findClass(this.value);
    }

    public String wsdlLocation() {
        return Util.nullSafe(this.wsdlLocation);
    }

    public String lookup() {
        return Util.nullSafe(this.lookup);
    }

    public Class<? extends Annotation> annotationType() {
        return WebServiceRef.class;
    }
}

