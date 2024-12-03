/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlTransient
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.soap.Detail
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPFault
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.fault;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.fault.CodeType;
import com.sun.xml.ws.fault.DetailType;
import com.sun.xml.ws.fault.ReasonType;
import com.sun.xml.ws.fault.SOAPFaultBuilder;
import com.sun.xml.ws.fault.ServerSOAPFaultException;
import com.sun.xml.ws.fault.SubcodeType;
import com.sun.xml.ws.fault.TextType;
import com.sun.xml.ws.util.DOMUtil;
import java.util.Iterator;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@XmlRootElement(name="Fault", namespace="http://www.w3.org/2003/05/soap-envelope")
@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="", propOrder={"code", "reason", "node", "role", "detail"})
class SOAP12Fault
extends SOAPFaultBuilder {
    @XmlTransient
    private static final String ns = "http://www.w3.org/2003/05/soap-envelope";
    @XmlElement(namespace="http://www.w3.org/2003/05/soap-envelope", name="Code")
    private CodeType code;
    @XmlElement(namespace="http://www.w3.org/2003/05/soap-envelope", name="Reason")
    private ReasonType reason;
    @XmlElement(namespace="http://www.w3.org/2003/05/soap-envelope", name="Node")
    private String node;
    @XmlElement(namespace="http://www.w3.org/2003/05/soap-envelope", name="Role")
    private String role;
    @XmlElement(namespace="http://www.w3.org/2003/05/soap-envelope", name="Detail")
    private DetailType detail;

    SOAP12Fault() {
    }

    SOAP12Fault(CodeType code, ReasonType reason, String node, String role, DetailType detail) {
        this.code = code;
        this.reason = reason;
        this.node = node;
        this.role = role;
        this.detail = detail;
    }

    SOAP12Fault(CodeType code, ReasonType reason, String node, String role, Element detailObject) {
        this.code = code;
        this.reason = reason;
        this.node = node;
        this.role = role;
        if (detailObject != null) {
            if (detailObject.getNamespaceURI().equals(ns) && detailObject.getLocalName().equals("Detail")) {
                this.detail = new DetailType();
                for (Element detailEntry : DOMUtil.getChildElements(detailObject)) {
                    this.detail.getDetails().add(detailEntry);
                }
            } else {
                this.detail = new DetailType(detailObject);
            }
        }
    }

    SOAP12Fault(SOAPFault fault) {
        this.code = new CodeType(fault.getFaultCodeAsQName());
        try {
            this.fillFaultSubCodes(fault);
        }
        catch (SOAPException e) {
            throw new WebServiceException((Throwable)e);
        }
        this.reason = new ReasonType(fault.getFaultString());
        this.role = fault.getFaultRole();
        this.node = fault.getFaultNode();
        if (fault.getDetail() != null) {
            this.detail = new DetailType();
            Iterator iter = fault.getDetail().getDetailEntries();
            while (iter.hasNext()) {
                Element fd = (Element)iter.next();
                this.detail.getDetails().add(fd);
            }
        }
    }

    SOAP12Fault(QName code, String reason, Element detailObject) {
        this(new CodeType(code), new ReasonType(reason), null, null, detailObject);
    }

    CodeType getCode() {
        return this.code;
    }

    ReasonType getReason() {
        return this.reason;
    }

    String getNode() {
        return this.node;
    }

    String getRole() {
        return this.role;
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
    String getFaultString() {
        return this.reason.texts().get(0).getText();
    }

    @Override
    protected Throwable getProtocolException() {
        try {
            SOAPFault fault = SOAPVersion.SOAP_12.getSOAPFactory().createFault();
            if (this.reason != null) {
                for (TextType tt : this.reason.texts()) {
                    fault.setFaultString(tt.getText());
                }
            }
            if (this.code != null) {
                fault.setFaultCode(this.code.getValue());
                this.fillFaultSubCodes(fault, this.code.getSubcode());
            }
            if (this.detail != null && this.detail.getDetail(0) != null) {
                Detail detail = fault.addDetail();
                for (Node node : this.detail.getDetails()) {
                    Node n = fault.getOwnerDocument().importNode(node, true);
                    detail.appendChild(n);
                }
            }
            if (this.node != null) {
                fault.setFaultNode(this.node);
            }
            return new ServerSOAPFaultException(fault);
        }
        catch (SOAPException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    private void fillFaultSubCodes(SOAPFault fault, SubcodeType subcode) throws SOAPException {
        if (subcode != null) {
            fault.appendFaultSubcode(subcode.getValue());
            this.fillFaultSubCodes(fault, subcode.getSubcode());
        }
    }

    private void fillFaultSubCodes(SOAPFault fault) throws SOAPException {
        Iterator subcodes = fault.getFaultSubcodes();
        SubcodeType firstSct = null;
        while (subcodes.hasNext()) {
            QName subcode = (QName)subcodes.next();
            if (firstSct == null) {
                firstSct = new SubcodeType(subcode);
                this.code.setSubcode(firstSct);
                continue;
            }
            SubcodeType nextSct = new SubcodeType(subcode);
            firstSct.setSubcode(nextSct);
            firstSct = nextSct;
        }
    }
}

