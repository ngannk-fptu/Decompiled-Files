/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAnyAttribute
 *  javax.xml.bind.annotation.XmlAnyElement
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlElementRef
 *  javax.xml.bind.annotation.XmlElementRefs
 *  javax.xml.bind.annotation.XmlType
 */
package com.oracle.xmlns.webservices.jaxws_databinding;

import com.oracle.xmlns.webservices.jaxws_databinding.ExistingAnnotationsType;
import com.oracle.xmlns.webservices.jaxws_databinding.JavaMethod;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlBindingType;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlHandlerChain;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlMTOM;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlSOAPBinding;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlServiceMode;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlWebFault;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlWebService;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlWebServiceClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="java-wsdl-mapping-type", propOrder={"xmlSchemaMapping", "classAnnotation", "javaMethods"})
public class JavaWsdlMappingType {
    @XmlElement(name="xml-schema-mapping")
    protected XmlSchemaMapping xmlSchemaMapping;
    @XmlElementRefs(value={@XmlElementRef(name="web-service-client", namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", type=XmlWebServiceClient.class, required=false), @XmlElementRef(name="binding-type", namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", type=XmlBindingType.class, required=false), @XmlElementRef(name="web-service", namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", type=XmlWebService.class, required=false), @XmlElementRef(name="web-fault", namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", type=XmlWebFault.class, required=false), @XmlElementRef(name="service-mode", namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", type=XmlServiceMode.class, required=false), @XmlElementRef(name="mtom", namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", type=XmlMTOM.class, required=false), @XmlElementRef(name="handler-chain", namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", type=XmlHandlerChain.class, required=false), @XmlElementRef(name="soap-binding", namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", type=XmlSOAPBinding.class, required=false)})
    @XmlAnyElement
    protected List<Object> classAnnotation;
    @XmlElement(name="java-methods")
    protected JavaMethods javaMethods;
    @XmlAttribute(name="name")
    protected String name;
    @XmlAttribute(name="java-type-name")
    protected String javaTypeName;
    @XmlAttribute(name="existing-annotations")
    protected ExistingAnnotationsType existingAnnotations;
    @XmlAttribute(name="databinding")
    protected String databinding;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    public XmlSchemaMapping getXmlSchemaMapping() {
        return this.xmlSchemaMapping;
    }

    public void setXmlSchemaMapping(XmlSchemaMapping value) {
        this.xmlSchemaMapping = value;
    }

    public List<Object> getClassAnnotation() {
        if (this.classAnnotation == null) {
            this.classAnnotation = new ArrayList<Object>();
        }
        return this.classAnnotation;
    }

    public JavaMethods getJavaMethods() {
        return this.javaMethods;
    }

    public void setJavaMethods(JavaMethods value) {
        this.javaMethods = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getJavaTypeName() {
        return this.javaTypeName;
    }

    public void setJavaTypeName(String value) {
        this.javaTypeName = value;
    }

    public ExistingAnnotationsType getExistingAnnotations() {
        return this.existingAnnotations;
    }

    public void setExistingAnnotations(ExistingAnnotationsType value) {
        this.existingAnnotations = value;
    }

    public String getDatabinding() {
        return this.databinding;
    }

    public void setDatabinding(String value) {
        this.databinding = value;
    }

    public Map<QName, String> getOtherAttributes() {
        return this.otherAttributes;
    }

    @XmlAccessorType(value=XmlAccessType.FIELD)
    @XmlType(name="", propOrder={"any"})
    public static class XmlSchemaMapping {
        @XmlAnyElement(lax=true)
        protected List<Object> any;

        public List<Object> getAny() {
            if (this.any == null) {
                this.any = new ArrayList<Object>();
            }
            return this.any;
        }
    }

    @XmlAccessorType(value=XmlAccessType.FIELD)
    @XmlType(name="", propOrder={"javaMethod"})
    public static class JavaMethods {
        @XmlElement(name="java-method")
        protected List<JavaMethod> javaMethod;

        public List<JavaMethod> getJavaMethod() {
            if (this.javaMethod == null) {
                this.javaMethod = new ArrayList<JavaMethod>();
            }
            return this.javaMethod;
        }
    }
}

