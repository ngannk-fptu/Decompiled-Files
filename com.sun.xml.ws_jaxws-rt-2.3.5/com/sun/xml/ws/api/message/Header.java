/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  com.sun.xml.bind.api.Bridge
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPMessage
 */
package com.sun.xml.ws.api.message;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.bind.api.Bridge;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.spi.db.XMLBridge;
import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public interface Header {
    public boolean isIgnorable(@NotNull SOAPVersion var1, @NotNull Set<String> var2);

    @NotNull
    public String getRole(@NotNull SOAPVersion var1);

    public boolean isRelay();

    @NotNull
    public String getNamespaceURI();

    @NotNull
    public String getLocalPart();

    @Nullable
    public String getAttribute(@NotNull String var1, @NotNull String var2);

    @Nullable
    public String getAttribute(@NotNull QName var1);

    public XMLStreamReader readHeader() throws XMLStreamException;

    public <T> T readAsJAXB(Unmarshaller var1) throws JAXBException;

    public <T> T readAsJAXB(Bridge<T> var1) throws JAXBException;

    public <T> T readAsJAXB(XMLBridge<T> var1) throws JAXBException;

    @NotNull
    public WSEndpointReference readAsEPR(AddressingVersion var1) throws XMLStreamException;

    public void writeTo(XMLStreamWriter var1) throws XMLStreamException;

    public void writeTo(SOAPMessage var1) throws SOAPException;

    public void writeTo(ContentHandler var1, ErrorHandler var2) throws SAXException;

    @NotNull
    public String getStringContent();
}

