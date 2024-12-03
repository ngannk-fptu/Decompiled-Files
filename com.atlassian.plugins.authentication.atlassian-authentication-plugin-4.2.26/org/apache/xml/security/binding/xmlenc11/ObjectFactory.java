/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.annotation.XmlElementDecl
 *  javax.xml.bind.annotation.XmlRegistry
 */
package org.apache.xml.security.binding.xmlenc11;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.apache.xml.security.binding.xmlenc11.AlgorithmIdentifierType;
import org.apache.xml.security.binding.xmlenc11.ConcatKDFParamsType;
import org.apache.xml.security.binding.xmlenc11.DerivedKeyType;
import org.apache.xml.security.binding.xmlenc11.KeyDerivationMethodType;
import org.apache.xml.security.binding.xmlenc11.MGFType;
import org.apache.xml.security.binding.xmlenc11.PBKDF2ParameterType;
import org.apache.xml.security.binding.xmlenc11.PRFAlgorithmIdentifierType;

@XmlRegistry
public class ObjectFactory {
    private static final QName _ConcatKDFParams_QNAME = new QName("http://www.w3.org/2009/xmlenc11#", "ConcatKDFParams");
    private static final QName _DerivedKey_QNAME = new QName("http://www.w3.org/2009/xmlenc11#", "DerivedKey");
    private static final QName _KeyDerivationMethod_QNAME = new QName("http://www.w3.org/2009/xmlenc11#", "KeyDerivationMethod");
    private static final QName _PBKDF2Params_QNAME = new QName("http://www.w3.org/2009/xmlenc11#", "PBKDF2-params");
    private static final QName _MGF_QNAME = new QName("http://www.w3.org/2009/xmlenc11#", "MGF");

    public PBKDF2ParameterType createPBKDF2ParameterType() {
        return new PBKDF2ParameterType();
    }

    public ConcatKDFParamsType createConcatKDFParamsType() {
        return new ConcatKDFParamsType();
    }

    public DerivedKeyType createDerivedKeyType() {
        return new DerivedKeyType();
    }

    public KeyDerivationMethodType createKeyDerivationMethodType() {
        return new KeyDerivationMethodType();
    }

    public MGFType createMGFType() {
        return new MGFType();
    }

    public AlgorithmIdentifierType createAlgorithmIdentifierType() {
        return new AlgorithmIdentifierType();
    }

    public PRFAlgorithmIdentifierType createPRFAlgorithmIdentifierType() {
        return new PRFAlgorithmIdentifierType();
    }

    public PBKDF2ParameterType.Salt createPBKDF2ParameterTypeSalt() {
        return new PBKDF2ParameterType.Salt();
    }

    @XmlElementDecl(namespace="http://www.w3.org/2009/xmlenc11#", name="ConcatKDFParams")
    public JAXBElement<ConcatKDFParamsType> createConcatKDFParams(ConcatKDFParamsType value) {
        return new JAXBElement(_ConcatKDFParams_QNAME, ConcatKDFParamsType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.w3.org/2009/xmlenc11#", name="DerivedKey")
    public JAXBElement<DerivedKeyType> createDerivedKey(DerivedKeyType value) {
        return new JAXBElement(_DerivedKey_QNAME, DerivedKeyType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.w3.org/2009/xmlenc11#", name="KeyDerivationMethod")
    public JAXBElement<KeyDerivationMethodType> createKeyDerivationMethod(KeyDerivationMethodType value) {
        return new JAXBElement(_KeyDerivationMethod_QNAME, KeyDerivationMethodType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.w3.org/2009/xmlenc11#", name="PBKDF2-params")
    public JAXBElement<PBKDF2ParameterType> createPBKDF2Params(PBKDF2ParameterType value) {
        return new JAXBElement(_PBKDF2Params_QNAME, PBKDF2ParameterType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.w3.org/2009/xmlenc11#", name="MGF")
    public JAXBElement<MGFType> createMGF(MGFType value) {
        return new JAXBElement(_MGF_QNAME, MGFType.class, null, (Object)value);
    }
}

