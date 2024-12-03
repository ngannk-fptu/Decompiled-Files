/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.annotation.XmlElementDecl
 *  javax.xml.bind.annotation.XmlRegistry
 */
package com.oracle.xmlns.webservices.jaxws_databinding;

import com.oracle.xmlns.webservices.jaxws_databinding.JavaMethod;
import com.oracle.xmlns.webservices.jaxws_databinding.JavaParam;
import com.oracle.xmlns.webservices.jaxws_databinding.JavaWsdlMappingType;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlAction;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlBindingType;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlFaultAction;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlHandlerChain;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlMTOM;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlOneway;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlRequestWrapper;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlResponseWrapper;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlSOAPBinding;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlServiceMode;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlWebEndpoint;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlWebFault;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlWebMethod;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlWebParam;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlWebResult;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlWebService;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlWebServiceClient;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlWebServiceProvider;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlWebServiceRef;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
    private static final QName _JavaWsdlMapping_QNAME = new QName("http://xmlns.oracle.com/webservices/jaxws-databinding", "java-wsdl-mapping");

    public JavaMethod createJavaMethod() {
        return new JavaMethod();
    }

    public JavaWsdlMappingType createJavaWsdlMappingType() {
        return new JavaWsdlMappingType();
    }

    public XmlWebEndpoint createWebEndpoint() {
        return new XmlWebEndpoint();
    }

    public XmlMTOM createMtom() {
        return new XmlMTOM();
    }

    public XmlWebServiceClient createWebServiceClient() {
        return new XmlWebServiceClient();
    }

    public XmlServiceMode createServiceMode() {
        return new XmlServiceMode();
    }

    public XmlBindingType createBindingType() {
        return new XmlBindingType();
    }

    public XmlWebServiceRef createWebServiceRef() {
        return new XmlWebServiceRef();
    }

    public JavaParam createJavaParam() {
        return new JavaParam();
    }

    public XmlWebParam createWebParam() {
        return new XmlWebParam();
    }

    public XmlWebMethod createWebMethod() {
        return new XmlWebMethod();
    }

    public XmlWebResult createWebResult() {
        return new XmlWebResult();
    }

    public XmlOneway createOneway() {
        return new XmlOneway();
    }

    public XmlSOAPBinding createSoapBinding() {
        return new XmlSOAPBinding();
    }

    public XmlAction createAction() {
        return new XmlAction();
    }

    public XmlFaultAction createFaultAction() {
        return new XmlFaultAction();
    }

    public JavaMethod.JavaParams createJavaMethodJavaParams() {
        return new JavaMethod.JavaParams();
    }

    public XmlHandlerChain createHandlerChain() {
        return new XmlHandlerChain();
    }

    public XmlWebServiceProvider createWebServiceProvider() {
        return new XmlWebServiceProvider();
    }

    public XmlWebFault createWebFault() {
        return new XmlWebFault();
    }

    public XmlResponseWrapper createResponseWrapper() {
        return new XmlResponseWrapper();
    }

    public XmlWebService createWebService() {
        return new XmlWebService();
    }

    public XmlRequestWrapper createRequestWrapper() {
        return new XmlRequestWrapper();
    }

    public JavaWsdlMappingType.XmlSchemaMapping createJavaWsdlMappingTypeXmlSchemaMapping() {
        return new JavaWsdlMappingType.XmlSchemaMapping();
    }

    public JavaWsdlMappingType.JavaMethods createJavaWsdlMappingTypeJavaMethods() {
        return new JavaWsdlMappingType.JavaMethods();
    }

    @XmlElementDecl(namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", name="java-wsdl-mapping")
    public JAXBElement<JavaWsdlMappingType> createJavaWsdlMapping(JavaWsdlMappingType value) {
        return new JAXBElement(_JavaWsdlMapping_QNAME, JavaWsdlMappingType.class, null, (Object)value);
    }
}

