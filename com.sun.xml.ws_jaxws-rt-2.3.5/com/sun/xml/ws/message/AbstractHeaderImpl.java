/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.xml.bind.api.Bridge
 *  com.sun.xml.bind.api.BridgeContext
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Unmarshaller
 */
package com.sun.xml.ws.message;

import com.sun.istack.NotNull;
import com.sun.xml.bind.api.Bridge;
import com.sun.xml.bind.api.BridgeContext;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.ws.spi.db.XMLBridge;
import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.helpers.AttributesImpl;

public abstract class AbstractHeaderImpl
implements Header {
    protected static final AttributesImpl EMPTY_ATTS = new AttributesImpl();

    protected AbstractHeaderImpl() {
    }

    public final <T> T readAsJAXB(Bridge<T> bridge, BridgeContext context) throws JAXBException {
        return this.readAsJAXB(bridge);
    }

    @Override
    public <T> T readAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
        try {
            return (T)unmarshaller.unmarshal(this.readHeader());
        }
        catch (Exception e) {
            throw new JAXBException((Throwable)e);
        }
    }

    @Override
    @Deprecated
    public <T> T readAsJAXB(Bridge<T> bridge) throws JAXBException {
        try {
            return (T)bridge.unmarshal(this.readHeader());
        }
        catch (XMLStreamException e) {
            throw new JAXBException((Throwable)e);
        }
    }

    @Override
    public <T> T readAsJAXB(XMLBridge<T> bridge) throws JAXBException {
        try {
            return bridge.unmarshal(this.readHeader(), null);
        }
        catch (XMLStreamException e) {
            throw new JAXBException((Throwable)e);
        }
    }

    @Override
    public WSEndpointReference readAsEPR(AddressingVersion expected) throws XMLStreamException {
        XMLStreamReader xsr = this.readHeader();
        WSEndpointReference epr = new WSEndpointReference(xsr, expected);
        XMLStreamReaderFactory.recycle(xsr);
        return epr;
    }

    @Override
    public boolean isIgnorable(@NotNull SOAPVersion soapVersion, @NotNull Set<String> roles) {
        String v = this.getAttribute(soapVersion.nsUri, "mustUnderstand");
        if (v == null || !this.parseBool(v)) {
            return true;
        }
        if (roles == null) {
            return true;
        }
        return !roles.contains(this.getRole(soapVersion));
    }

    @Override
    @NotNull
    public String getRole(@NotNull SOAPVersion soapVersion) {
        String v = this.getAttribute(soapVersion.nsUri, soapVersion.roleAttributeName);
        if (v == null) {
            v = soapVersion.implicitRole;
        }
        return v;
    }

    @Override
    public boolean isRelay() {
        String v = this.getAttribute(SOAPVersion.SOAP_12.nsUri, "relay");
        if (v == null) {
            return false;
        }
        return this.parseBool(v);
    }

    @Override
    public String getAttribute(QName name) {
        return this.getAttribute(name.getNamespaceURI(), name.getLocalPart());
    }

    protected final boolean parseBool(String value) {
        if (value.length() == 0) {
            return false;
        }
        char ch = value.charAt(0);
        return ch == 't' || ch == '1';
    }

    @Override
    public String getStringContent() {
        try {
            XMLStreamReader xsr = this.readHeader();
            xsr.nextTag();
            return xsr.getElementText();
        }
        catch (XMLStreamException e) {
            return null;
        }
    }
}

