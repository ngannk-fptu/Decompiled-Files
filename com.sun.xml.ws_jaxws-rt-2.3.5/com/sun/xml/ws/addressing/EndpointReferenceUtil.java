/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  com.sun.xml.stream.buffer.XMLStreamBuffer
 *  com.sun.xml.stream.buffer.XMLStreamBufferSource
 *  com.sun.xml.stream.buffer.stax.StreamWriterBufferCreator
 *  javax.xml.ws.EndpointReference
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.wsaddressing.W3CEndpointReference
 */
package com.sun.xml.ws.addressing;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.stream.buffer.XMLStreamBufferSource;
import com.sun.xml.stream.buffer.stax.StreamWriterBufferCreator;
import com.sun.xml.ws.addressing.v200408.MemberSubmissionAddressingConstants;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.developer.MemberSubmissionEndpointReference;
import com.sun.xml.ws.util.DOMUtil;
import com.sun.xml.ws.util.xml.XmlUtil;
import com.sun.xml.ws.wsdl.parser.WSDLConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class EndpointReferenceUtil {
    private static boolean w3cMetadataWritten = false;

    public static <T extends EndpointReference> T transform(Class<T> clazz, @NotNull EndpointReference epr) {
        assert (epr != null);
        if (clazz.isAssignableFrom(W3CEndpointReference.class)) {
            if (epr instanceof W3CEndpointReference) {
                return (T)epr;
            }
            if (epr instanceof MemberSubmissionEndpointReference) {
                return (T)EndpointReferenceUtil.toW3CEpr((MemberSubmissionEndpointReference)epr);
            }
        } else if (clazz.isAssignableFrom(MemberSubmissionEndpointReference.class)) {
            if (epr instanceof W3CEndpointReference) {
                return (T)EndpointReferenceUtil.toMSEpr((W3CEndpointReference)epr);
            }
            if (epr instanceof MemberSubmissionEndpointReference) {
                return (T)epr;
            }
        }
        throw new WebServiceException("Unknwon EndpointReference: " + epr.getClass());
    }

    private static W3CEndpointReference toW3CEpr(MemberSubmissionEndpointReference msEpr) {
        StreamWriterBufferCreator writer = new StreamWriterBufferCreator();
        w3cMetadataWritten = false;
        try {
            writer.writeStartDocument();
            writer.writeStartElement(AddressingVersion.W3C.getPrefix(), "EndpointReference", AddressingVersion.W3C.nsUri);
            writer.writeNamespace(AddressingVersion.W3C.getPrefix(), AddressingVersion.W3C.nsUri);
            writer.writeStartElement(AddressingVersion.W3C.getPrefix(), AddressingVersion.W3C.eprType.address, AddressingVersion.W3C.nsUri);
            writer.writeCharacters(msEpr.addr.uri);
            writer.writeEndElement();
            if (msEpr.referenceProperties != null && msEpr.referenceProperties.elements.size() > 0 || msEpr.referenceParameters != null && msEpr.referenceParameters.elements.size() > 0) {
                writer.writeStartElement(AddressingVersion.W3C.getPrefix(), "ReferenceParameters", AddressingVersion.W3C.nsUri);
                if (msEpr.referenceProperties != null) {
                    for (Element e : msEpr.referenceProperties.elements) {
                        DOMUtil.serializeNode(e, (XMLStreamWriter)writer);
                    }
                }
                if (msEpr.referenceParameters != null) {
                    for (Element e : msEpr.referenceParameters.elements) {
                        DOMUtil.serializeNode(e, (XMLStreamWriter)writer);
                    }
                }
                writer.writeEndElement();
            }
            Element wsdlElement = null;
            if (msEpr.elements != null && msEpr.elements.size() > 0) {
                for (Element e : msEpr.elements) {
                    NodeList nl;
                    if (!e.getNamespaceURI().equals(MemberSubmissionAddressingConstants.MEX_METADATA.getNamespaceURI()) || !e.getLocalName().equals(MemberSubmissionAddressingConstants.MEX_METADATA.getLocalPart()) || (nl = e.getElementsByTagNameNS("http://schemas.xmlsoap.org/wsdl/", WSDLConstants.QNAME_DEFINITIONS.getLocalPart())) == null) continue;
                    wsdlElement = (Element)nl.item(0);
                }
            }
            if (wsdlElement != null) {
                DOMUtil.serializeNode(wsdlElement, (XMLStreamWriter)writer);
            }
            if (w3cMetadataWritten) {
                writer.writeEndElement();
            }
            if (msEpr.elements != null && msEpr.elements.size() > 0) {
                for (Element e : msEpr.elements) {
                    if (!e.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/") || e.getLocalName().equals(WSDLConstants.QNAME_DEFINITIONS.getLocalPart())) {
                        // empty if block
                    }
                    DOMUtil.serializeNode(e, (XMLStreamWriter)writer);
                }
            }
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();
        }
        catch (XMLStreamException e) {
            throw new WebServiceException((Throwable)e);
        }
        return new W3CEndpointReference((Source)new XMLStreamBufferSource((XMLStreamBuffer)writer.getXMLStreamBuffer()));
    }

    private static MemberSubmissionEndpointReference toMSEpr(W3CEndpointReference w3cEpr) {
        DOMResult result = new DOMResult();
        w3cEpr.writeTo((Result)result);
        Node eprNode = result.getNode();
        Element e = DOMUtil.getFirstElementChild(eprNode);
        if (e == null) {
            return null;
        }
        MemberSubmissionEndpointReference msEpr = new MemberSubmissionEndpointReference();
        NodeList nodes = e.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            if (nodes.item(i).getNodeType() == 1) {
                Element child = (Element)nodes.item(i);
                if (child.getNamespaceURI().equals(AddressingVersion.W3C.nsUri) && child.getLocalName().equals(AddressingVersion.W3C.eprType.address)) {
                    if (msEpr.addr == null) {
                        msEpr.addr = new MemberSubmissionEndpointReference.Address();
                    }
                    msEpr.addr.uri = XmlUtil.getTextForNode(child);
                    continue;
                }
                if (child.getNamespaceURI().equals(AddressingVersion.W3C.nsUri) && child.getLocalName().equals("ReferenceParameters")) {
                    NodeList refParams = child.getChildNodes();
                    for (int j = 0; j < refParams.getLength(); ++j) {
                        if (refParams.item(j).getNodeType() != 1) continue;
                        if (msEpr.referenceParameters == null) {
                            msEpr.referenceParameters = new MemberSubmissionEndpointReference.Elements();
                            msEpr.referenceParameters.elements = new ArrayList<Element>();
                        }
                        msEpr.referenceParameters.elements.add((Element)refParams.item(j));
                    }
                    continue;
                }
                if (child.getNamespaceURI().equals(AddressingVersion.W3C.nsUri) && child.getLocalName().equals(AddressingVersion.W3C.eprType.wsdlMetadata.getLocalPart())) {
                    NodeList metadata = child.getChildNodes();
                    String wsdlLocation = child.getAttributeNS("http://www.w3.org/ns/wsdl-instance", "wsdlLocation");
                    Element wsdlDefinitions = null;
                    for (int j = 0; j < metadata.getLength(); ++j) {
                        String ns;
                        String name;
                        String prefix;
                        Node node = metadata.item(j);
                        if (node.getNodeType() != 1) continue;
                        Element elm = (Element)node;
                        if ((elm.getNamespaceURI().equals(AddressingVersion.W3C.wsdlNsUri) || elm.getNamespaceURI().equals("http://www.w3.org/2007/05/addressing/metadata")) && elm.getLocalName().equals(AddressingVersion.W3C.eprType.serviceName)) {
                            msEpr.serviceName = new MemberSubmissionEndpointReference.ServiceNameType();
                            msEpr.serviceName.portName = elm.getAttribute(AddressingVersion.W3C.eprType.portName);
                            String service = elm.getTextContent();
                            prefix = XmlUtil.getPrefix(service);
                            name = XmlUtil.getLocalPart(service);
                            if (name == null) continue;
                            if (prefix != null) {
                                ns = elm.lookupNamespaceURI(prefix);
                                if (ns != null) {
                                    msEpr.serviceName.name = new QName(ns, name, prefix);
                                }
                            } else {
                                msEpr.serviceName.name = new QName(null, name);
                            }
                            msEpr.serviceName.attributes = EndpointReferenceUtil.getAttributes(elm);
                            continue;
                        }
                        if ((elm.getNamespaceURI().equals(AddressingVersion.W3C.wsdlNsUri) || elm.getNamespaceURI().equals("http://www.w3.org/2007/05/addressing/metadata")) && elm.getLocalName().equals(AddressingVersion.W3C.eprType.portTypeName)) {
                            msEpr.portTypeName = new MemberSubmissionEndpointReference.AttributedQName();
                            String portType = elm.getTextContent();
                            prefix = XmlUtil.getPrefix(portType);
                            name = XmlUtil.getLocalPart(portType);
                            if (name == null) continue;
                            if (prefix != null) {
                                ns = elm.lookupNamespaceURI(prefix);
                                if (ns != null) {
                                    msEpr.portTypeName.name = new QName(ns, name, prefix);
                                }
                            } else {
                                msEpr.portTypeName.name = new QName(null, name);
                            }
                            msEpr.portTypeName.attributes = EndpointReferenceUtil.getAttributes(elm);
                            continue;
                        }
                        if (elm.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/") && elm.getLocalName().equals(WSDLConstants.QNAME_DEFINITIONS.getLocalPart())) {
                            wsdlDefinitions = elm;
                            continue;
                        }
                        if (msEpr.elements == null) {
                            msEpr.elements = new ArrayList<Element>();
                        }
                        msEpr.elements.add(elm);
                    }
                    Document doc = DOMUtil.createDom();
                    Element mexEl = doc.createElementNS(MemberSubmissionAddressingConstants.MEX_METADATA.getNamespaceURI(), MemberSubmissionAddressingConstants.MEX_METADATA.getPrefix() + ":" + MemberSubmissionAddressingConstants.MEX_METADATA.getLocalPart());
                    Element metadataEl = doc.createElementNS(MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getNamespaceURI(), MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getPrefix() + ":" + MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getLocalPart());
                    metadataEl.setAttribute("Dialect", "http://schemas.xmlsoap.org/wsdl/");
                    if (wsdlDefinitions == null && wsdlLocation != null && !wsdlLocation.equals("")) {
                        wsdlLocation = wsdlLocation.trim();
                        String wsdlTns = wsdlLocation.substring(0, wsdlLocation.indexOf(32));
                        wsdlLocation = wsdlLocation.substring(wsdlLocation.indexOf(32) + 1);
                        Element wsdlEl = doc.createElementNS("http://schemas.xmlsoap.org/wsdl/", "wsdl:" + WSDLConstants.QNAME_DEFINITIONS.getLocalPart());
                        Element wsdlImportEl = doc.createElementNS("http://schemas.xmlsoap.org/wsdl/", "wsdl:" + WSDLConstants.QNAME_IMPORT.getLocalPart());
                        wsdlImportEl.setAttribute("namespace", wsdlTns);
                        wsdlImportEl.setAttribute("location", wsdlLocation);
                        wsdlEl.appendChild(wsdlImportEl);
                        metadataEl.appendChild(wsdlEl);
                    } else if (wsdlDefinitions != null) {
                        metadataEl.appendChild(wsdlDefinitions);
                    }
                    mexEl.appendChild(metadataEl);
                    if (msEpr.elements == null) {
                        msEpr.elements = new ArrayList<Element>();
                    }
                    msEpr.elements.add(mexEl);
                    continue;
                }
                if (msEpr.elements == null) {
                    msEpr.elements = new ArrayList<Element>();
                }
                msEpr.elements.add(child);
                continue;
            }
            if (nodes.item(i).getNodeType() != 2) continue;
            Node n = nodes.item(i);
            if (msEpr.attributes != null) continue;
            msEpr.attributes = new HashMap<QName, String>();
            String prefix = EndpointReferenceUtil.fixNull(n.getPrefix());
            String ns = EndpointReferenceUtil.fixNull(n.getNamespaceURI());
            String localName = n.getLocalName();
            msEpr.attributes.put(new QName(ns, localName, prefix), n.getNodeValue());
        }
        return msEpr;
    }

    private static Map<QName, String> getAttributes(Node node) {
        HashMap<QName, String> attribs = null;
        NamedNodeMap nm = node.getAttributes();
        for (int i = 0; i < nm.getLength(); ++i) {
            if (attribs == null) {
                attribs = new HashMap<QName, String>();
            }
            Node n = nm.item(i);
            String prefix = EndpointReferenceUtil.fixNull(n.getPrefix());
            String ns = EndpointReferenceUtil.fixNull(n.getNamespaceURI());
            String localName = n.getLocalName();
            if (prefix.equals("xmlns") || prefix.length() == 0 && localName.equals("xmlns") || localName.equals(AddressingVersion.W3C.eprType.portName)) continue;
            attribs.put(new QName(ns, localName, prefix), n.getNodeValue());
        }
        return attribs;
    }

    @NotNull
    private static String fixNull(@Nullable String s) {
        if (s == null) {
            return "";
        }
        return s;
    }
}

