/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.bind.util.Which
 *  javax.xml.soap.MessageFactory
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPFactory
 */
package com.sun.xml.ws.api;

import com.oracle.webservices.api.EnvelopeStyle;
import com.oracle.webservices.api.EnvelopeStyleFeature;
import com.sun.xml.bind.util.Which;
import com.sun.xml.ws.api.message.saaj.SAAJFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

public enum SOAPVersion {
    SOAP_11("http://schemas.xmlsoap.org/wsdl/soap/http", "http://schemas.xmlsoap.org/soap/envelope/", "text/xml", "http://schemas.xmlsoap.org/soap/actor/next", "actor", "SOAP 1.1 Protocol", new QName("http://schemas.xmlsoap.org/soap/envelope/", "MustUnderstand"), "Client", "Server", Collections.singleton("http://schemas.xmlsoap.org/soap/actor/next")),
    SOAP_12("http://www.w3.org/2003/05/soap/bindings/HTTP/", "http://www.w3.org/2003/05/soap-envelope", "application/soap+xml", "http://www.w3.org/2003/05/soap-envelope/role/ultimateReceiver", "role", "SOAP 1.2 Protocol", new QName("http://www.w3.org/2003/05/soap-envelope", "MustUnderstand"), "Sender", "Receiver", new HashSet<String>(Arrays.asList("http://www.w3.org/2003/05/soap-envelope/role/next", "http://www.w3.org/2003/05/soap-envelope/role/ultimateReceiver")));

    public final String httpBindingId;
    public final String nsUri;
    public final String contentType;
    public final QName faultCodeMustUnderstand;
    @Deprecated
    public final transient MessageFactory saajMessageFactory;
    @Deprecated
    public final transient SOAPFactory saajSoapFactory;
    private final String saajFactoryString;
    public final String implicitRole;
    public final Set<String> implicitRoleSet;
    public final Set<String> requiredRoles;
    public final String roleAttributeName;
    public final QName faultCodeClient;
    public final QName faultCodeServer;

    private SOAPVersion(String httpBindingId, String nsUri, String contentType, String implicitRole, String roleAttributeName, String saajFactoryString, QName faultCodeMustUnderstand, String faultCodeClientLocalName, String faultCodeServerLocalName, Set<String> requiredRoles) {
        this.httpBindingId = httpBindingId;
        this.nsUri = nsUri;
        this.contentType = contentType;
        this.implicitRole = implicitRole;
        this.implicitRoleSet = Collections.singleton(implicitRole);
        this.roleAttributeName = roleAttributeName;
        this.saajFactoryString = saajFactoryString;
        try {
            this.saajMessageFactory = MessageFactory.newInstance((String)saajFactoryString);
            this.saajSoapFactory = SOAPFactory.newInstance((String)saajFactoryString);
        }
        catch (SOAPException e) {
            throw new Error(e);
        }
        catch (NoSuchMethodError e) {
            LinkageError x = new LinkageError("You are loading old SAAJ from " + Which.which(MessageFactory.class));
            x.initCause(e);
            throw x;
        }
        this.faultCodeMustUnderstand = faultCodeMustUnderstand;
        this.requiredRoles = requiredRoles;
        this.faultCodeClient = new QName(nsUri, faultCodeClientLocalName);
        this.faultCodeServer = new QName(nsUri, faultCodeServerLocalName);
    }

    public SOAPFactory getSOAPFactory() {
        try {
            return SAAJFactory.getSOAPFactory(this.saajFactoryString);
        }
        catch (SOAPException e) {
            throw new Error(e);
        }
        catch (NoSuchMethodError e) {
            LinkageError x = new LinkageError("You are loading old SAAJ from " + Which.which(MessageFactory.class));
            x.initCause(e);
            throw x;
        }
    }

    public MessageFactory getMessageFactory() {
        try {
            return SAAJFactory.getMessageFactory(this.saajFactoryString);
        }
        catch (SOAPException e) {
            throw new Error(e);
        }
        catch (NoSuchMethodError e) {
            LinkageError x = new LinkageError("You are loading old SAAJ from " + Which.which(MessageFactory.class));
            x.initCause(e);
            throw x;
        }
    }

    public String toString() {
        return this.httpBindingId;
    }

    public static SOAPVersion fromHttpBinding(String binding) {
        if (binding == null) {
            return SOAP_11;
        }
        if (binding.equals(SOAPVersion.SOAP_12.httpBindingId)) {
            return SOAP_12;
        }
        return SOAP_11;
    }

    public static SOAPVersion fromNsUri(String nsUri) {
        if (nsUri.equals(SOAPVersion.SOAP_12.nsUri)) {
            return SOAP_12;
        }
        return SOAP_11;
    }

    public static SOAPVersion from(EnvelopeStyleFeature f) {
        EnvelopeStyle.Style[] style = f.getStyles();
        if (style.length != 1) {
            throw new IllegalArgumentException("The EnvelopingFeature must has exactly one Enveloping.Style");
        }
        return SOAPVersion.from(style[0]);
    }

    public static SOAPVersion from(EnvelopeStyle.Style style) {
        switch (style) {
            case SOAP11: {
                return SOAP_11;
            }
            case SOAP12: {
                return SOAP_12;
            }
        }
        return SOAP_11;
    }

    public EnvelopeStyleFeature toFeature() {
        return SOAP_11.equals((Object)this) ? new EnvelopeStyleFeature(EnvelopeStyle.Style.SOAP11) : new EnvelopeStyleFeature(EnvelopeStyle.Style.SOAP12);
    }
}

