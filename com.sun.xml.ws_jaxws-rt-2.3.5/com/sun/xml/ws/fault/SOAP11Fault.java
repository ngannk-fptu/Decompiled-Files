/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.soap.Detail
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPFault
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.fault;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.fault.DetailType;
import com.sun.xml.ws.fault.SOAPFaultBuilder;
import com.sun.xml.ws.fault.ServerSOAPFaultException;
import com.sun.xml.ws.util.DOMUtil;
import java.util.Iterator;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="", propOrder={"faultcode", "faultstring", "faultactor", "detail"})
@XmlRootElement(name="Fault", namespace="http://schemas.xmlsoap.org/soap/envelope/")
class SOAP11Fault
extends SOAPFaultBuilder {
    @XmlElement(namespace="")
    private QName faultcode;
    @XmlElement(namespace="")
    private String faultstring;
    @XmlElement(namespace="")
    private String faultactor;
    @XmlElement(namespace="")
    private DetailType detail;

    SOAP11Fault() {
    }

    SOAP11Fault(QName code, String reason, String actor, Element detailObject) {
        this.faultcode = code;
        this.faultstring = reason;
        this.faultactor = actor;
        if (detailObject != null) {
            if ((detailObject.getNamespaceURI() == null || "".equals(detailObject.getNamespaceURI())) && "detail".equals(detailObject.getLocalName())) {
                this.detail = new DetailType();
                for (Element detailEntry : DOMUtil.getChildElements(detailObject)) {
                    this.detail.getDetails().add(detailEntry);
                }
            } else {
                this.detail = new DetailType(detailObject);
            }
        }
    }

    SOAP11Fault(SOAPFault fault) {
        this.faultcode = fault.getFaultCodeAsQName();
        this.faultstring = fault.getFaultString();
        this.faultactor = fault.getFaultActor();
        if (fault.getDetail() != null) {
            this.detail = new DetailType();
            Iterator iter = fault.getDetail().getDetailEntries();
            while (iter.hasNext()) {
                Element fd = (Element)iter.next();
                this.detail.getDetails().add(fd);
            }
        }
    }

    QName getFaultcode() {
        return this.faultcode;
    }

    void setFaultcode(QName faultcode) {
        this.faultcode = faultcode;
    }

    @Override
    String getFaultString() {
        return this.faultstring;
    }

    void setFaultstring(String faultstring) {
        this.faultstring = faultstring;
    }

    String getFaultactor() {
        return this.faultactor;
    }

    void setFaultactor(String faultactor) {
        this.faultactor = faultactor;
    }

    @Override
    DetailType getDetail() {
        return this.detail;
    }

    @Override
    void setDetail(DetailType detail) {
        this.detail = detail;
    }

    @Override
    protected Throwable getProtocolException() {
        try {
            SOAPFault fault = SOAPVersion.SOAP_11.getSOAPFactory().createFault(this.faultstring, this.faultcode);
            fault.setFaultActor(this.faultactor);
            if (this.detail != null) {
                Detail d = fault.addDetail();
                for (Element det : this.detail.getDetails()) {
                    Node n = fault.getOwnerDocument().importNode(det, true);
                    d.appendChild(n);
                }
            }
            return new ServerSOAPFaultException(fault);
        }
        catch (SOAPException e) {
            throw new WebServiceException((Throwable)e);
        }
    }
}

