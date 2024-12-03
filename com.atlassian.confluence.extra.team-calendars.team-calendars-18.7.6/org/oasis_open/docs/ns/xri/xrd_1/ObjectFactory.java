/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.annotation.XmlElementDecl
 *  javax.xml.bind.annotation.XmlRegistry
 */
package org.oasis_open.docs.ns.xri.xrd_1;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.oasis_open.docs.ns.xri.xrd_1.AnyURI;
import org.oasis_open.docs.ns.xri.xrd_1.ExpiresType;
import org.oasis_open.docs.ns.xri.xrd_1.LinkType;
import org.oasis_open.docs.ns.xri.xrd_1.PropertyType;
import org.oasis_open.docs.ns.xri.xrd_1.String;
import org.oasis_open.docs.ns.xri.xrd_1.TitleType;
import org.oasis_open.docs.ns.xri.xrd_1.XRDSType;
import org.oasis_open.docs.ns.xri.xrd_1.XRDType;

@XmlRegistry
public class ObjectFactory {
    private static final QName _Alias_QNAME = new QName("http://docs.oasis-open.org/ns/xri/xrd-1.0", "Alias");
    private static final QName _XRDS_QNAME = new QName("http://docs.oasis-open.org/ns/xri/xrd-1.0", "XRDS");
    private static final QName _Property_QNAME = new QName("http://docs.oasis-open.org/ns/xri/xrd-1.0", "Property");
    private static final QName _XRD_QNAME = new QName("http://docs.oasis-open.org/ns/xri/xrd-1.0", "XRD");
    private static final QName _Title_QNAME = new QName("http://docs.oasis-open.org/ns/xri/xrd-1.0", "Title");
    private static final QName _Subject_QNAME = new QName("http://docs.oasis-open.org/ns/xri/xrd-1.0", "Subject");
    private static final QName _Expires_QNAME = new QName("http://docs.oasis-open.org/ns/xri/xrd-1.0", "Expires");
    private static final QName _Link_QNAME = new QName("http://docs.oasis-open.org/ns/xri/xrd-1.0", "Link");

    public ExpiresType createExpiresType() {
        return new ExpiresType();
    }

    public TitleType createTitleType() {
        return new TitleType();
    }

    public XRDType createXRDType() {
        return new XRDType();
    }

    public PropertyType createPropertyType() {
        return new PropertyType();
    }

    public AnyURI createAnyURI() {
        return new AnyURI();
    }

    public LinkType createLinkType() {
        return new LinkType();
    }

    public String createString() {
        return new String();
    }

    public XRDSType createXRDSType() {
        return new XRDSType();
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ns/xri/xrd-1.0", name="Alias")
    public JAXBElement<AnyURI> createAlias(AnyURI value) {
        return new JAXBElement(_Alias_QNAME, AnyURI.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ns/xri/xrd-1.0", name="XRDS")
    public JAXBElement<XRDSType> createXRDS(XRDSType value) {
        return new JAXBElement(_XRDS_QNAME, XRDSType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ns/xri/xrd-1.0", name="Property")
    public JAXBElement<PropertyType> createProperty(PropertyType value) {
        return new JAXBElement(_Property_QNAME, PropertyType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ns/xri/xrd-1.0", name="XRD")
    public JAXBElement<XRDType> createXRD(XRDType value) {
        return new JAXBElement(_XRD_QNAME, XRDType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ns/xri/xrd-1.0", name="Title")
    public JAXBElement<TitleType> createTitle(TitleType value) {
        return new JAXBElement(_Title_QNAME, TitleType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ns/xri/xrd-1.0", name="Subject")
    public JAXBElement<AnyURI> createSubject(AnyURI value) {
        return new JAXBElement(_Subject_QNAME, AnyURI.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ns/xri/xrd-1.0", name="Expires")
    public JAXBElement<ExpiresType> createExpires(ExpiresType value) {
        return new JAXBElement(_Expires_QNAME, ExpiresType.class, null, (Object)value);
    }

    @XmlElementDecl(namespace="http://docs.oasis-open.org/ns/xri/xrd-1.0", name="Link")
    public JAXBElement<LinkType> createLink(LinkType value) {
        return new JAXBElement(_Link_QNAME, LinkType.class, null, (Object)value);
    }
}

