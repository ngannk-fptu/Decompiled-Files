/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.message;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.xml.namespace.QName;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.description.FaultDesc;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.message.Detail;
import org.apache.axis.message.NodeImpl;
import org.apache.axis.message.PrefixedQName;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.soap.SOAP11Constants;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Messages;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public class SOAPFault
extends SOAPBodyElement
implements javax.xml.soap.SOAPFault {
    protected AxisFault fault;
    protected String prefix;
    private Locale locale;
    protected Detail detail = null;
    static /* synthetic */ Class class$org$apache$axis$AxisFault;

    public SOAPFault(String namespace, String localName, String prefix, Attributes attrs, DeserializationContext context) throws AxisFault {
        super(namespace, localName, prefix, attrs, context);
    }

    public SOAPFault(AxisFault fault) {
        this.fault = fault;
    }

    public void outputImpl(SerializationContext context) throws Exception {
        SOAP11Constants soapConstants = context.getMessageContext() == null ? SOAPConstants.SOAP11_CONSTANTS : context.getMessageContext().getSOAPConstants();
        this.namespaceURI = soapConstants.getEnvelopeURI();
        this.name = "Fault";
        context.registerPrefixForURI(this.prefix, soapConstants.getEnvelopeURI());
        context.startElement(new QName(this.getNamespaceURI(), this.getName()), this.attributes);
        if (this.fault instanceof AxisFault) {
            Element[] faultDetails;
            QName qname;
            int i;
            AxisFault axisFault = this.fault;
            if (axisFault.getFaultCode() != null) {
                String faultCode;
                if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
                    faultCode = context.qName2String(axisFault.getFaultCode());
                    context.startElement(Constants.QNAME_FAULTCODE_SOAP12, null);
                    context.startElement(Constants.QNAME_FAULTVALUE_SOAP12, null);
                    context.writeSafeString(faultCode);
                    context.endElement();
                    QName[] subcodes = axisFault.getFaultSubCodes();
                    if (subcodes != null) {
                        for (i = 0; i < subcodes.length; ++i) {
                            faultCode = context.qName2String(subcodes[i]);
                            context.startElement(Constants.QNAME_FAULTSUBCODE_SOAP12, null);
                            context.startElement(Constants.QNAME_FAULTVALUE_SOAP12, null);
                            context.writeSafeString(faultCode);
                            context.endElement();
                        }
                        for (i = 0; i < subcodes.length; ++i) {
                            context.endElement();
                        }
                    }
                    context.endElement();
                } else {
                    faultCode = context.qName2String(axisFault.getFaultCode());
                    context.startElement(Constants.QNAME_FAULTCODE, null);
                    context.writeSafeString(faultCode);
                    context.endElement();
                }
            }
            if (axisFault.getFaultString() != null) {
                if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
                    context.startElement(Constants.QNAME_FAULTREASON_SOAP12, null);
                    AttributesImpl attrs = new AttributesImpl();
                    attrs.addAttribute("http://www.w3.org/XML/1998/namespace", "lang", "xml:lang", "CDATA", "en");
                    context.startElement(Constants.QNAME_TEXT_SOAP12, attrs);
                } else {
                    context.startElement(Constants.QNAME_FAULTSTRING, null);
                }
                context.writeSafeString(axisFault.getFaultString());
                context.endElement();
                if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
                    context.endElement();
                }
            }
            if (axisFault.getFaultActor() != null) {
                if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
                    context.startElement(Constants.QNAME_FAULTROLE_SOAP12, null);
                } else {
                    context.startElement(Constants.QNAME_FAULTACTOR, null);
                }
                context.writeSafeString(axisFault.getFaultActor());
                context.endElement();
            }
            if (axisFault.getFaultNode() != null && soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
                context.startElement(Constants.QNAME_FAULTNODE_SOAP12, null);
                context.writeSafeString(axisFault.getFaultNode());
                context.endElement();
            }
            if ((qname = this.getFaultQName(this.fault.getClass(), context)) == null && this.fault.detail != null) {
                qname = this.getFaultQName(this.fault.detail.getClass(), context);
            }
            if (qname == null) {
                qname = new QName("", "faultData");
            }
            if ((faultDetails = axisFault.getFaultDetails()) != null) {
                if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
                    context.startElement(Constants.QNAME_FAULTDETAIL_SOAP12, null);
                } else {
                    context.startElement(Constants.QNAME_FAULTDETAILS, null);
                }
                axisFault.writeDetails(qname, context);
                for (i = 0; i < faultDetails.length; ++i) {
                    context.writeDOMElement(faultDetails[i]);
                }
                if (this.detail != null) {
                    Iterator it = this.detail.getChildren().iterator();
                    while (it.hasNext()) {
                        ((NodeImpl)it.next()).output(context);
                    }
                }
                context.endElement();
            }
        }
        context.endElement();
    }

    private QName getFaultQName(Class cls, SerializationContext context) {
        QName qname = null;
        if (!cls.equals(class$org$apache$axis$AxisFault == null ? (class$org$apache$axis$AxisFault = SOAPFault.class$("org.apache.axis.AxisFault")) : class$org$apache$axis$AxisFault)) {
            OperationDesc op;
            FaultDesc faultDesc = null;
            if (context.getMessageContext() != null && (op = context.getMessageContext().getOperation()) != null) {
                faultDesc = op.getFaultByClass(cls);
            }
            if (faultDesc != null) {
                qname = faultDesc.getQName();
            }
        }
        return qname;
    }

    public AxisFault getFault() {
        return this.fault;
    }

    public void setFault(AxisFault fault) {
        this.fault = fault;
    }

    public void setFaultCode(String faultCode) throws SOAPException {
        this.fault.setFaultCodeAsString(faultCode);
    }

    public String getFaultCode() {
        return this.fault.getFaultCode().getLocalPart();
    }

    public void setFaultActor(String faultActor) throws SOAPException {
        this.fault.setFaultActor(faultActor);
    }

    public String getFaultActor() {
        return this.fault.getFaultActor();
    }

    public void setFaultString(String faultString) throws SOAPException {
        this.fault.setFaultString(faultString);
    }

    public String getFaultString() {
        return this.fault.getFaultString();
    }

    public javax.xml.soap.Detail getDetail() {
        List children = this.getChildren();
        if (children == null || children.size() <= 0) {
            return null;
        }
        for (int i = 0; i < children.size(); ++i) {
            Object obj = children.get(i);
            if (!(obj instanceof javax.xml.soap.Detail)) continue;
            return (javax.xml.soap.Detail)obj;
        }
        return null;
    }

    public javax.xml.soap.Detail addDetail() throws SOAPException {
        if (this.getDetail() != null) {
            throw new SOAPException(Messages.getMessage("valuePresent"));
        }
        Detail detail = this.convertToDetail(this.fault);
        this.addChildElement(detail);
        return detail;
    }

    public void setFaultCode(Name faultCodeQName) throws SOAPException {
        String prefix;
        String uri = faultCodeQName.getURI();
        String local = faultCodeQName.getLocalName();
        this.prefix = prefix = faultCodeQName.getPrefix();
        QName qname = new QName(uri, local);
        this.fault.setFaultCode(qname);
    }

    public Name getFaultCodeAsName() {
        QName qname = this.fault.getFaultCode();
        String uri = qname.getNamespaceURI();
        String local = qname.getLocalPart();
        return new PrefixedQName(uri, local, this.prefix);
    }

    public void setFaultString(String faultString, Locale locale) throws SOAPException {
        this.fault.setFaultString(faultString);
        this.locale = locale;
    }

    public Locale getFaultStringLocale() {
        return this.locale;
    }

    private Detail convertToDetail(AxisFault fault) throws SOAPException {
        this.detail = new Detail();
        Element[] darray = fault.getFaultDetails();
        fault.setFaultDetail(new Element[0]);
        for (int i = 0; i < darray.length; ++i) {
            Element detailtEntryElem = darray[i];
            DetailEntry detailEntry = this.detail.addDetailEntry(new PrefixedQName(detailtEntryElem.getNamespaceURI(), detailtEntryElem.getLocalName(), detailtEntryElem.getPrefix()));
            SOAPFault.copyChildren(detailEntry, detailtEntryElem);
        }
        return this.detail;
    }

    private static void copyChildren(SOAPElement soapElement, Element domElement) throws SOAPException {
        NodeList nl = domElement.getChildNodes();
        for (int j = 0; j < nl.getLength(); ++j) {
            Node childNode = nl.item(j);
            if (childNode.getNodeType() == 3) {
                soapElement.addTextNode(childNode.getNodeValue());
                break;
            }
            if (childNode.getNodeType() != 1) continue;
            String uri = childNode.getNamespaceURI();
            SOAPElement childSoapElement = null;
            childSoapElement = uri == null ? soapElement.addChildElement(childNode.getLocalName()) : soapElement.addChildElement(childNode.getLocalName(), childNode.getPrefix(), uri);
            SOAPFault.copyChildren(childSoapElement, (Element)childNode);
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

