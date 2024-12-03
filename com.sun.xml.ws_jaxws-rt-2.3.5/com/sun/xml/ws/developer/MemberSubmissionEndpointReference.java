/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Marshaller
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.bind.annotation.XmlAnyAttribute
 *  javax.xml.bind.annotation.XmlAnyElement
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.XmlValue
 *  javax.xml.ws.EndpointReference
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.developer;

import com.sun.istack.NotNull;
import com.sun.xml.ws.addressing.v200408.MemberSubmissionAddressingConstants;
import com.sun.xml.ws.developer.ContextClassloaderLocal;
import com.sun.xml.ws.wsdl.parser.WSDLConstants;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;

@XmlRootElement(name="EndpointReference", namespace="http://schemas.xmlsoap.org/ws/2004/08/addressing")
@XmlType(name="EndpointReferenceType", namespace="http://schemas.xmlsoap.org/ws/2004/08/addressing")
public final class MemberSubmissionEndpointReference
extends EndpointReference
implements MemberSubmissionAddressingConstants {
    private static final ContextClassloaderLocal<JAXBContext> msjc = new ContextClassloaderLocal<JAXBContext>(){

        @Override
        protected JAXBContext initialValue() throws Exception {
            return MemberSubmissionEndpointReference.getMSJaxbContext();
        }
    };
    @XmlElement(name="Address", namespace="http://schemas.xmlsoap.org/ws/2004/08/addressing")
    public Address addr;
    @XmlElement(name="ReferenceProperties", namespace="http://schemas.xmlsoap.org/ws/2004/08/addressing")
    public Elements referenceProperties;
    @XmlElement(name="ReferenceParameters", namespace="http://schemas.xmlsoap.org/ws/2004/08/addressing")
    public Elements referenceParameters;
    @XmlElement(name="PortType", namespace="http://schemas.xmlsoap.org/ws/2004/08/addressing")
    public AttributedQName portTypeName;
    @XmlElement(name="ServiceName", namespace="http://schemas.xmlsoap.org/ws/2004/08/addressing")
    public ServiceNameType serviceName;
    @XmlAnyAttribute
    public Map<QName, String> attributes;
    @XmlAnyElement
    public List<Element> elements;
    protected static final String MSNS = "http://schemas.xmlsoap.org/ws/2004/08/addressing";

    public MemberSubmissionEndpointReference() {
    }

    public MemberSubmissionEndpointReference(@NotNull Source source) {
        if (source == null) {
            throw new WebServiceException("Source parameter can not be null on constructor");
        }
        try {
            Unmarshaller unmarshaller = msjc.get().createUnmarshaller();
            MemberSubmissionEndpointReference epr = (MemberSubmissionEndpointReference)unmarshaller.unmarshal(source, MemberSubmissionEndpointReference.class).getValue();
            this.addr = epr.addr;
            this.referenceProperties = epr.referenceProperties;
            this.referenceParameters = epr.referenceParameters;
            this.portTypeName = epr.portTypeName;
            this.serviceName = epr.serviceName;
            this.attributes = epr.attributes;
            this.elements = epr.elements;
        }
        catch (JAXBException e) {
            throw new WebServiceException("Error unmarshalling MemberSubmissionEndpointReference ", (Throwable)e);
        }
        catch (ClassCastException e) {
            throw new WebServiceException("Source did not contain MemberSubmissionEndpointReference", (Throwable)e);
        }
    }

    public void writeTo(Result result) {
        try {
            Marshaller marshaller = msjc.get().createMarshaller();
            marshaller.marshal((Object)this, result);
        }
        catch (JAXBException e) {
            throw new WebServiceException("Error marshalling W3CEndpointReference. ", (Throwable)e);
        }
    }

    public Source toWSDLSource() {
        Element wsdlElement = null;
        for (Element elem : this.elements) {
            if (!elem.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/") || !elem.getLocalName().equals(WSDLConstants.QNAME_DEFINITIONS.getLocalPart())) continue;
            wsdlElement = elem;
        }
        return new DOMSource(wsdlElement);
    }

    private static JAXBContext getMSJaxbContext() {
        try {
            return JAXBContext.newInstance((Class[])new Class[]{MemberSubmissionEndpointReference.class});
        }
        catch (JAXBException e) {
            throw new WebServiceException("Error creating JAXBContext for MemberSubmissionEndpointReference. ", (Throwable)e);
        }
    }

    public static class ServiceNameType
    extends AttributedQName {
        @XmlAttribute(name="PortName")
        public String portName;
    }

    public static class AttributedQName {
        @XmlValue
        public QName name;
        @XmlAnyAttribute
        public Map<QName, String> attributes;
    }

    @XmlType(name="elements", namespace="http://schemas.xmlsoap.org/ws/2004/08/addressing")
    public static class Elements {
        @XmlAnyElement
        public List<Element> elements;
    }

    @XmlType(name="address", namespace="http://schemas.xmlsoap.org/ws/2004/08/addressing")
    public static class Address {
        @XmlValue
        public String uri;
        @XmlAnyAttribute
        public Map<QName, String> attributes;
    }
}

