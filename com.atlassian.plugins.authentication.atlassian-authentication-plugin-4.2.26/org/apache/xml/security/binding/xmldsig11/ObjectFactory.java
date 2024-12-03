/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.annotation.XmlElementDecl
 *  javax.xml.bind.annotation.XmlRegistry
 */
package org.apache.xml.security.binding.xmldsig11;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.apache.xml.security.binding.xmldsig11.CharTwoFieldParamsType;
import org.apache.xml.security.binding.xmldsig11.CurveType;
import org.apache.xml.security.binding.xmldsig11.DEREncodedKeyValueType;
import org.apache.xml.security.binding.xmldsig11.ECKeyValueType;
import org.apache.xml.security.binding.xmldsig11.ECParametersType;
import org.apache.xml.security.binding.xmldsig11.ECValidationDataType;
import org.apache.xml.security.binding.xmldsig11.FieldIDType;
import org.apache.xml.security.binding.xmldsig11.KeyInfoReferenceType;
import org.apache.xml.security.binding.xmldsig11.NamedCurveType;
import org.apache.xml.security.binding.xmldsig11.PnBFieldParamsType;
import org.apache.xml.security.binding.xmldsig11.PrimeFieldParamsType;
import org.apache.xml.security.binding.xmldsig11.TnBFieldParamsType;
import org.apache.xml.security.binding.xmldsig11.X509DigestType;

@XmlRegistry
public class ObjectFactory {
    private static final QName _ECKeyValue_QNAME = new QName("http://www.w3.org/2009/xmldsig11#", "ECKeyValue");
    private static final QName _Prime_QNAME = new QName("http://www.w3.org/2009/xmldsig11#", "Prime");
    private static final QName _GnB_QNAME = new QName("http://www.w3.org/2009/xmldsig11#", "GnB");
    private static final QName _TnB_QNAME = new QName("http://www.w3.org/2009/xmldsig11#", "TnB");
    private static final QName _PnB_QNAME = new QName("http://www.w3.org/2009/xmldsig11#", "PnB");
    private static final QName _OCSPResponse_QNAME = new QName("http://www.w3.org/2009/xmldsig11#", "OCSPResponse");
    private static final QName _DEREncodedKeyValue_QNAME = new QName("http://www.w3.org/2009/xmldsig11#", "DEREncodedKeyValue");
    private static final QName _KeyInfoReference_QNAME = new QName("http://www.w3.org/2009/xmldsig11#", "KeyInfoReference");
    private static final QName _X509Digest_QNAME = new QName("http://www.w3.org/2009/xmldsig11#", "X509Digest");

    public ECKeyValueType createECKeyValueType() {
        return new ECKeyValueType();
    }

    public PrimeFieldParamsType createPrimeFieldParamsType() {
        return new PrimeFieldParamsType();
    }

    public CharTwoFieldParamsType createCharTwoFieldParamsType() {
        return new CharTwoFieldParamsType();
    }

    public TnBFieldParamsType createTnBFieldParamsType() {
        return new TnBFieldParamsType();
    }

    public PnBFieldParamsType createPnBFieldParamsType() {
        return new PnBFieldParamsType();
    }

    public DEREncodedKeyValueType createDEREncodedKeyValueType() {
        return new DEREncodedKeyValueType();
    }

    public KeyInfoReferenceType createKeyInfoReferenceType() {
        return new KeyInfoReferenceType();
    }

    public X509DigestType createX509DigestType() {
        return new X509DigestType();
    }

    public NamedCurveType createNamedCurveType() {
        return new NamedCurveType();
    }

    public ECParametersType createECParametersType() {
        return new ECParametersType();
    }

    public FieldIDType createFieldIDType() {
        return new FieldIDType();
    }

    public CurveType createCurveType() {
        return new CurveType();
    }

    public ECValidationDataType createECValidationDataType() {
        return new ECValidationDataType();
    }

    @XmlElementDecl(namespace="http://www.w3.org/2009/xmldsig11#", name="ECKeyValue")
    public JAXBElement<ECKeyValueType> createECKeyValue(ECKeyValueType value) {
        return new JAXBElement(_ECKeyValue_QNAME, ECKeyValueType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.w3.org/2009/xmldsig11#", name="Prime")
    public JAXBElement<PrimeFieldParamsType> createPrime(PrimeFieldParamsType value) {
        return new JAXBElement(_Prime_QNAME, PrimeFieldParamsType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.w3.org/2009/xmldsig11#", name="GnB")
    public JAXBElement<CharTwoFieldParamsType> createGnB(CharTwoFieldParamsType value) {
        return new JAXBElement(_GnB_QNAME, CharTwoFieldParamsType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.w3.org/2009/xmldsig11#", name="TnB")
    public JAXBElement<TnBFieldParamsType> createTnB(TnBFieldParamsType value) {
        return new JAXBElement(_TnB_QNAME, TnBFieldParamsType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.w3.org/2009/xmldsig11#", name="PnB")
    public JAXBElement<PnBFieldParamsType> createPnB(PnBFieldParamsType value) {
        return new JAXBElement(_PnB_QNAME, PnBFieldParamsType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.w3.org/2009/xmldsig11#", name="OCSPResponse")
    public JAXBElement<byte[]> createOCSPResponse(byte[] value) {
        return new JAXBElement(_OCSPResponse_QNAME, byte[].class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.w3.org/2009/xmldsig11#", name="DEREncodedKeyValue")
    public JAXBElement<DEREncodedKeyValueType> createDEREncodedKeyValue(DEREncodedKeyValueType value) {
        return new JAXBElement(_DEREncodedKeyValue_QNAME, DEREncodedKeyValueType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.w3.org/2009/xmldsig11#", name="KeyInfoReference")
    public JAXBElement<KeyInfoReferenceType> createKeyInfoReference(KeyInfoReferenceType value) {
        return new JAXBElement(_KeyInfoReference_QNAME, KeyInfoReferenceType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://www.w3.org/2009/xmldsig11#", name="X509Digest")
    public JAXBElement<X509DigestType> createX509Digest(X509DigestType value) {
        return new JAXBElement(_X509Digest_QNAME, X509DigestType.class, null, (Object)value);
    }
}

