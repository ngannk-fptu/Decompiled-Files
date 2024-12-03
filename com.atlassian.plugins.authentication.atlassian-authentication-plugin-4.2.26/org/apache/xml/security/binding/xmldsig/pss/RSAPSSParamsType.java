/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlSeeAlso
 *  javax.xml.bind.annotation.XmlType
 */
package org.apache.xml.security.binding.xmldsig.pss;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.apache.xml.security.binding.xmldsig.DigestMethodType;
import org.apache.xml.security.binding.xmldsig.pss.MaskGenerationFunctionType;
import org.apache.xml.security.binding.xmldsig.pss.RSAPSSParams;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="RSAPSSParamsType", namespace="http://www.w3.org/2007/05/xmldsig-more#", propOrder={"digestMethod", "maskGenerationFunction", "saltLength", "trailerField"})
@XmlSeeAlso(value={RSAPSSParams.class})
public class RSAPSSParamsType {
    @XmlElement(name="DigestMethod", namespace="http://www.w3.org/2000/09/xmldsig#")
    protected DigestMethodType digestMethod;
    @XmlElement(name="MaskGenerationFunction", namespace="http://www.w3.org/2007/05/xmldsig-more#")
    protected MaskGenerationFunctionType maskGenerationFunction;
    @XmlElement(name="SaltLength", namespace="http://www.w3.org/2007/05/xmldsig-more#")
    protected Integer saltLength;
    @XmlElement(name="TrailerField", namespace="http://www.w3.org/2007/05/xmldsig-more#")
    protected Integer trailerField;

    public DigestMethodType getDigestMethod() {
        return this.digestMethod;
    }

    public void setDigestMethod(DigestMethodType value) {
        this.digestMethod = value;
    }

    public MaskGenerationFunctionType getMaskGenerationFunction() {
        return this.maskGenerationFunction;
    }

    public void setMaskGenerationFunction(MaskGenerationFunctionType value) {
        this.maskGenerationFunction = value;
    }

    public Integer getSaltLength() {
        return this.saltLength;
    }

    public void setSaltLength(Integer value) {
        this.saltLength = value;
    }

    public Integer getTrailerField() {
        return this.trailerField;
    }

    public void setTrailerField(Integer value) {
        this.trailerField = value;
    }
}

