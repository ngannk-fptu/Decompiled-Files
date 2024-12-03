/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.jws.WebService
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlType
 */
package com.oracle.xmlns.webservices.jaxws_databinding;

import com.oracle.xmlns.webservices.jaxws_databinding.Util;
import java.lang.annotation.Annotation;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="web-service")
public class XmlWebService
implements WebService {
    @XmlAttribute(name="endpoint-interface")
    protected String endpointInterface;
    @XmlAttribute(name="name")
    protected String name;
    @XmlAttribute(name="port-name")
    protected String portName;
    @XmlAttribute(name="service-name")
    protected String serviceName;
    @XmlAttribute(name="target-namespace")
    protected String targetNamespace;
    @XmlAttribute(name="wsdl-location")
    protected String wsdlLocation;

    public String getEndpointInterface() {
        if (this.endpointInterface == null) {
            return "";
        }
        return this.endpointInterface;
    }

    public void setEndpointInterface(String value) {
        this.endpointInterface = value;
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

    public String getPortName() {
        if (this.portName == null) {
            return "";
        }
        return this.portName;
    }

    public void setPortName(String value) {
        this.portName = value;
    }

    public String getServiceName() {
        if (this.serviceName == null) {
            return "";
        }
        return this.serviceName;
    }

    public void setServiceName(String value) {
        this.serviceName = value;
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

    public String getWsdlLocation() {
        if (this.wsdlLocation == null) {
            return "";
        }
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

    public String serviceName() {
        return Util.nullSafe(this.serviceName);
    }

    public String portName() {
        return Util.nullSafe(this.portName);
    }

    public String wsdlLocation() {
        return Util.nullSafe(this.wsdlLocation);
    }

    public String endpointInterface() {
        return Util.nullSafe(this.endpointInterface);
    }

    public Class<? extends Annotation> annotationType() {
        return WebService.class;
    }
}

