/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlType
 */
package org.apache.xml.security.binding.xmldsig11;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.xml.security.binding.xmldsig11.CurveType;
import org.apache.xml.security.binding.xmldsig11.ECValidationDataType;
import org.apache.xml.security.binding.xmldsig11.FieldIDType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ECParametersType", namespace="http://www.w3.org/2009/xmldsig11#", propOrder={"fieldID", "curve", "base", "order", "coFactor", "validationData"})
public class ECParametersType {
    @XmlElement(name="FieldID", namespace="http://www.w3.org/2009/xmldsig11#", required=true)
    protected FieldIDType fieldID;
    @XmlElement(name="Curve", namespace="http://www.w3.org/2009/xmldsig11#", required=true)
    protected CurveType curve;
    @XmlElement(name="Base", namespace="http://www.w3.org/2009/xmldsig11#", required=true)
    protected byte[] base;
    @XmlElement(name="Order", namespace="http://www.w3.org/2009/xmldsig11#", required=true)
    protected byte[] order;
    @XmlElement(name="CoFactor", namespace="http://www.w3.org/2009/xmldsig11#")
    protected BigInteger coFactor;
    @XmlElement(name="ValidationData", namespace="http://www.w3.org/2009/xmldsig11#")
    protected ECValidationDataType validationData;

    public FieldIDType getFieldID() {
        return this.fieldID;
    }

    public void setFieldID(FieldIDType value) {
        this.fieldID = value;
    }

    public CurveType getCurve() {
        return this.curve;
    }

    public void setCurve(CurveType value) {
        this.curve = value;
    }

    public byte[] getBase() {
        return this.base;
    }

    public void setBase(byte[] value) {
        this.base = value;
    }

    public byte[] getOrder() {
        return this.order;
    }

    public void setOrder(byte[] value) {
        this.order = value;
    }

    public BigInteger getCoFactor() {
        return this.coFactor;
    }

    public void setCoFactor(BigInteger value) {
        this.coFactor = value;
    }

    public ECValidationDataType getValidationData() {
        return this.validationData;
    }

    public void setValidationData(ECValidationDataType value) {
        this.validationData = value;
    }
}

