/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.Nullable
 *  javax.xml.soap.Detail
 *  javax.xml.soap.SOAPConstants
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPFactory
 *  javax.xml.soap.SOAPFault
 *  javax.xml.soap.SOAPMessage
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.addressing;

import com.sun.istack.Nullable;
import com.sun.xml.ws.addressing.model.InvalidAddressingHeaderException;
import com.sun.xml.ws.addressing.model.MissingAddressingHeaderException;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.message.AddressingUtils;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.JavaMethod;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.WSDLOperationMapping;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLFault;
import com.sun.xml.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLOutput;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.model.CheckedExceptionImpl;
import com.sun.xml.ws.model.JavaMethodImpl;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;

public abstract class WsaTubeHelper {
    protected SEIModel seiModel;
    protected WSDLPort wsdlPort;
    protected WSBinding binding;
    protected final SOAPVersion soapVer;
    protected final AddressingVersion addVer;

    public WsaTubeHelper(WSBinding binding, SEIModel seiModel, WSDLPort wsdlPort) {
        this.binding = binding;
        this.wsdlPort = wsdlPort;
        this.seiModel = seiModel;
        this.soapVer = binding.getSOAPVersion();
        this.addVer = binding.getAddressingVersion();
    }

    public String getFaultAction(Packet requestPacket, Packet responsePacket) {
        WSDLOperationMapping wsdlOp;
        String action = null;
        if (this.seiModel != null) {
            action = this.getFaultActionFromSEIModel(requestPacket, responsePacket);
        }
        if (action != null) {
            return action;
        }
        action = this.addVer.getDefaultFaultAction();
        if (this.wsdlPort != null && (wsdlOp = requestPacket.getWSDLOperationMapping()) != null) {
            WSDLBoundOperation wbo = wsdlOp.getWSDLBoundOperation();
            return this.getFaultAction(wbo, responsePacket);
        }
        return action;
    }

    String getFaultActionFromSEIModel(Packet requestPacket, Packet responsePacket) {
        String action = null;
        if (this.seiModel == null || this.wsdlPort == null) {
            return action;
        }
        try {
            JavaMethodImpl jm;
            SOAPMessage sm = responsePacket.getMessage().copy().readAsSOAPMessage();
            if (sm == null) {
                return action;
            }
            if (sm.getSOAPBody() == null) {
                return action;
            }
            if (sm.getSOAPBody().getFault() == null) {
                return action;
            }
            Detail detail = sm.getSOAPBody().getFault().getDetail();
            if (detail == null) {
                return action;
            }
            String ns = detail.getFirstChild().getNamespaceURI();
            String name = detail.getFirstChild().getLocalName();
            WSDLOperationMapping wsdlOp = requestPacket.getWSDLOperationMapping();
            JavaMethodImpl javaMethodImpl = jm = wsdlOp != null ? (JavaMethodImpl)wsdlOp.getJavaMethod() : null;
            if (jm != null) {
                for (CheckedExceptionImpl ce : jm.getCheckedExceptions()) {
                    if (!ce.getDetailType().tagName.getLocalPart().equals(name) || !ce.getDetailType().tagName.getNamespaceURI().equals(ns)) continue;
                    return ce.getFaultAction();
                }
            }
            return action;
        }
        catch (SOAPException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    String getFaultAction(@Nullable WSDLBoundOperation wbo, Packet responsePacket) {
        String action = AddressingUtils.getAction(responsePacket.getMessage().getHeaders(), this.addVer, this.soapVer);
        if (action != null) {
            return action;
        }
        action = this.addVer.getDefaultFaultAction();
        if (wbo == null) {
            return action;
        }
        try {
            SOAPMessage sm = responsePacket.getMessage().copy().readAsSOAPMessage();
            if (sm == null) {
                return action;
            }
            if (sm.getSOAPBody() == null) {
                return action;
            }
            if (sm.getSOAPBody().getFault() == null) {
                return action;
            }
            Detail detail = sm.getSOAPBody().getFault().getDetail();
            if (detail == null) {
                return action;
            }
            String ns = detail.getFirstChild().getNamespaceURI();
            String name = detail.getFirstChild().getLocalName();
            WSDLOperation o = wbo.getOperation();
            WSDLFault fault = o.getFault(new QName(ns, name));
            if (fault == null) {
                return action;
            }
            action = fault.getAction();
            return action;
        }
        catch (SOAPException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    public String getInputAction(Packet packet) {
        WSDLOperationMapping wsdlOp;
        String action = null;
        if (this.wsdlPort != null && (wsdlOp = packet.getWSDLOperationMapping()) != null) {
            WSDLBoundOperation wbo = wsdlOp.getWSDLBoundOperation();
            WSDLOperation op = wbo.getOperation();
            action = op.getInput().getAction();
        }
        return action;
    }

    public String getEffectiveInputAction(Packet packet) {
        String action;
        if (packet.soapAction != null && !packet.soapAction.equals("")) {
            return packet.soapAction;
        }
        if (this.wsdlPort != null) {
            WSDLOperationMapping wsdlOp = packet.getWSDLOperationMapping();
            if (wsdlOp != null) {
                WSDLBoundOperation wbo = wsdlOp.getWSDLBoundOperation();
                WSDLOperation op = wbo.getOperation();
                action = op.getInput().getAction();
            } else {
                action = packet.soapAction;
            }
        } else {
            action = packet.soapAction;
        }
        return action;
    }

    public boolean isInputActionDefault(Packet packet) {
        if (this.wsdlPort == null) {
            return false;
        }
        WSDLOperationMapping wsdlOp = packet.getWSDLOperationMapping();
        if (wsdlOp == null) {
            return false;
        }
        WSDLBoundOperation wbo = wsdlOp.getWSDLBoundOperation();
        WSDLOperation op = wbo.getOperation();
        return op.getInput().isDefaultAction();
    }

    public String getSOAPAction(Packet packet) {
        String action = "";
        if (packet == null || packet.getMessage() == null) {
            return action;
        }
        if (this.wsdlPort == null) {
            return action;
        }
        WSDLOperationMapping wsdlOp = packet.getWSDLOperationMapping();
        if (wsdlOp == null) {
            return action;
        }
        WSDLBoundOperation op = wsdlOp.getWSDLBoundOperation();
        action = op.getSOAPAction();
        return action;
    }

    public String getOutputAction(Packet packet) {
        String action = null;
        WSDLOperationMapping wsdlOp = packet.getWSDLOperationMapping();
        if (wsdlOp != null) {
            JavaMethodImpl jm;
            JavaMethod javaMethod = wsdlOp.getJavaMethod();
            if (javaMethod != null && (jm = (JavaMethodImpl)javaMethod) != null && jm.getOutputAction() != null && !jm.getOutputAction().equals("")) {
                return jm.getOutputAction();
            }
            WSDLBoundOperation wbo = wsdlOp.getWSDLBoundOperation();
            if (wbo != null) {
                return this.getOutputAction(wbo);
            }
        }
        return action;
    }

    String getOutputAction(@Nullable WSDLBoundOperation wbo) {
        WSDLOutput op;
        String action = "http://jax-ws.dev.java.net/addressing/output-action-not-set";
        if (wbo != null && (op = wbo.getOperation().getOutput()) != null) {
            action = op.getAction();
        }
        return action;
    }

    public SOAPFault createInvalidAddressingHeaderFault(InvalidAddressingHeaderException e, AddressingVersion av) {
        QName name = e.getProblemHeader();
        QName subsubcode = e.getSubsubcode();
        QName subcode = av.invalidMapTag;
        String faultstring = String.format(av.getInvalidMapText(), name, subsubcode);
        try {
            SOAPFault fault;
            if (this.soapVer == SOAPVersion.SOAP_12) {
                SOAPFactory factory = SOAPVersion.SOAP_12.getSOAPFactory();
                fault = factory.createFault();
                fault.setFaultCode(SOAPConstants.SOAP_SENDER_FAULT);
                fault.appendFaultSubcode(subcode);
                fault.appendFaultSubcode(subsubcode);
                this.getInvalidMapDetail(name, (Element)fault.addDetail());
            } else {
                SOAPFactory factory = SOAPVersion.SOAP_11.getSOAPFactory();
                fault = factory.createFault();
                fault.setFaultCode(subsubcode);
            }
            fault.setFaultString(faultstring);
            return fault;
        }
        catch (SOAPException se) {
            throw new WebServiceException((Throwable)se);
        }
    }

    public SOAPFault newMapRequiredFault(MissingAddressingHeaderException e) {
        QName subcode = this.addVer.mapRequiredTag;
        QName subsubcode = this.addVer.mapRequiredTag;
        String faultstring = this.addVer.getMapRequiredText();
        try {
            SOAPFault fault;
            if (this.soapVer == SOAPVersion.SOAP_12) {
                SOAPFactory factory = SOAPVersion.SOAP_12.getSOAPFactory();
                fault = factory.createFault();
                fault.setFaultCode(SOAPConstants.SOAP_SENDER_FAULT);
                fault.appendFaultSubcode(subcode);
                fault.appendFaultSubcode(subsubcode);
                this.getMapRequiredDetail(e.getMissingHeaderQName(), (Element)fault.addDetail());
            } else {
                SOAPFactory factory = SOAPVersion.SOAP_11.getSOAPFactory();
                fault = factory.createFault();
                fault.setFaultCode(subsubcode);
            }
            fault.setFaultString(faultstring);
            return fault;
        }
        catch (SOAPException se) {
            throw new WebServiceException((Throwable)se);
        }
    }

    public abstract void getProblemActionDetail(String var1, Element var2);

    public abstract void getInvalidMapDetail(QName var1, Element var2);

    public abstract void getMapRequiredDetail(QName var1, Element var2);
}

