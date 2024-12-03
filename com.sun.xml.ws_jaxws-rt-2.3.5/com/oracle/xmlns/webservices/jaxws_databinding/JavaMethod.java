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
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlType
 */
package com.oracle.xmlns.webservices.jaxws_databinding;

import com.oracle.xmlns.webservices.jaxws_databinding.JavaParam;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlAction;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlOneway;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlSOAPBinding;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlWebEndpoint;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlWebMethod;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlWebResult;
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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="", propOrder={"methodAnnotation", "javaParams"})
@XmlRootElement(name="java-method")
public class JavaMethod {
    @XmlElementRefs(value={@XmlElementRef(name="web-endpoint", namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", type=XmlWebEndpoint.class, required=false), @XmlElementRef(name="oneway", namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", type=XmlOneway.class, required=false), @XmlElementRef(name="action", namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", type=XmlAction.class, required=false), @XmlElementRef(name="soap-binding", namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", type=XmlSOAPBinding.class, required=false), @XmlElementRef(name="web-result", namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", type=XmlWebResult.class, required=false), @XmlElementRef(name="web-method", namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", type=XmlWebMethod.class, required=false)})
    @XmlAnyElement
    protected List<Object> methodAnnotation;
    @XmlElement(name="java-params")
    protected JavaParams javaParams;
    @XmlAttribute(name="name", required=true)
    protected String name;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    public List<Object> getMethodAnnotation() {
        if (this.methodAnnotation == null) {
            this.methodAnnotation = new ArrayList<Object>();
        }
        return this.methodAnnotation;
    }

    public JavaParams getJavaParams() {
        return this.javaParams;
    }

    public void setJavaParams(JavaParams value) {
        this.javaParams = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public Map<QName, String> getOtherAttributes() {
        return this.otherAttributes;
    }

    @XmlAccessorType(value=XmlAccessType.FIELD)
    @XmlType(name="", propOrder={"javaParam"})
    public static class JavaParams {
        @XmlElement(name="java-param", required=true)
        protected List<JavaParam> javaParam;

        public List<JavaParam> getJavaParam() {
            if (this.javaParam == null) {
                this.javaParam = new ArrayList<JavaParam>();
            }
            return this.javaParam;
        }
    }
}

