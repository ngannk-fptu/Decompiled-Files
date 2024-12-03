/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.ws.WebServiceClient
 */
package com.oracle.xmlns.webservices.jaxws_databinding;

import com.oracle.xmlns.webservices.jaxws_databinding.Util;
import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.WebServiceClient;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="web-service-client")
public class XmlWebServiceClient
implements WebServiceClient {
    @XmlAttribute(name="name")
    protected String name;
    @XmlAttribute(name="targetNamespace")
    protected String targetNamespace;
    @XmlAttribute(name="wsdlLocation")
    protected String wsdlLocation;

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

    public String getWsdlLocation() {
        return this.wsdlLocation;
    }

    public void setWsdlLocation(String value) {
        this.wsdlLocation = value;
    }

    public String name() {
        return Util.nullSafe(this.name);
    }

    public String targetNamespace() {
        return Util.nullSafe(this.targetNamespace);
    }

    public String wsdlLocation() {
        return Util.nullSafe(this.wsdlLocation);
    }

    public Class<? extends Annotation> annotationType() {
        return WebServiceClient.class;
    }
}

