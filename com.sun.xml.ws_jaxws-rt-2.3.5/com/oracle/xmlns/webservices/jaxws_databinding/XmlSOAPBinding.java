/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.jws.soap.SOAPBinding
 *  javax.jws.soap.SOAPBinding$ParameterStyle
 *  javax.jws.soap.SOAPBinding$Style
 *  javax.jws.soap.SOAPBinding$Use
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlType
 */
package com.oracle.xmlns.webservices.jaxws_databinding;

import com.oracle.xmlns.webservices.jaxws_databinding.SoapBindingParameterStyle;
import com.oracle.xmlns.webservices.jaxws_databinding.SoapBindingStyle;
import com.oracle.xmlns.webservices.jaxws_databinding.SoapBindingUse;
import com.oracle.xmlns.webservices.jaxws_databinding.Util;
import java.lang.annotation.Annotation;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="soap-binding")
public class XmlSOAPBinding
implements SOAPBinding {
    @XmlAttribute(name="style")
    protected SoapBindingStyle style;
    @XmlAttribute(name="use")
    protected SoapBindingUse use;
    @XmlAttribute(name="parameter-style")
    protected SoapBindingParameterStyle parameterStyle;

    public SoapBindingStyle getStyle() {
        if (this.style == null) {
            return SoapBindingStyle.DOCUMENT;
        }
        return this.style;
    }

    public void setStyle(SoapBindingStyle value) {
        this.style = value;
    }

    public SoapBindingUse getUse() {
        if (this.use == null) {
            return SoapBindingUse.LITERAL;
        }
        return this.use;
    }

    public void setUse(SoapBindingUse value) {
        this.use = value;
    }

    public SoapBindingParameterStyle getParameterStyle() {
        if (this.parameterStyle == null) {
            return SoapBindingParameterStyle.WRAPPED;
        }
        return this.parameterStyle;
    }

    public void setParameterStyle(SoapBindingParameterStyle value) {
        this.parameterStyle = value;
    }

    public SOAPBinding.Style style() {
        return Util.nullSafe(this.style, SOAPBinding.Style.DOCUMENT);
    }

    public SOAPBinding.Use use() {
        return Util.nullSafe(this.use, SOAPBinding.Use.LITERAL);
    }

    public SOAPBinding.ParameterStyle parameterStyle() {
        return Util.nullSafe(this.parameterStyle, SOAPBinding.ParameterStyle.WRAPPED);
    }

    public Class<? extends Annotation> annotationType() {
        return SOAPBinding.class;
    }
}

