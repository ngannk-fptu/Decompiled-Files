/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlSchemaType
 *  javax.xml.bind.annotation.XmlType
 */
package org.apache.xml.security.binding.xmldsig.pss;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.apache.xml.security.binding.xmldsig.DigestMethodType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="MaskGenerationFunctionType", namespace="http://www.w3.org/2007/05/xmldsig-more#", propOrder={"digestMethod"})
public class MaskGenerationFunctionType {
    @XmlElement(name="DigestMethod", namespace="http://www.w3.org/2000/09/xmldsig#")
    protected DigestMethodType digestMethod;
    @XmlAttribute(name="Algorithm")
    @XmlSchemaType(name="anyURI")
    protected String algorithm;

    public DigestMethodType getDigestMethod() {
        return this.digestMethod;
    }

    public void setDigestMethod(DigestMethodType value) {
        this.digestMethod = value;
    }

    public String getAlgorithm() {
        if (this.algorithm == null) {
            return "http://www.w3.org/2007/05/xmldsig-more#MGF1";
        }
        return this.algorithm;
    }

    public void setAlgorithm(String value) {
        this.algorithm = value;
    }
}

