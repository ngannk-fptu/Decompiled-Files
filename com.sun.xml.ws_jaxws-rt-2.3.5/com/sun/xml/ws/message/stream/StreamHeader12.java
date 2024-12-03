/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.FinalArrayList
 *  com.sun.xml.stream.buffer.XMLStreamBuffer
 */
package com.sun.xml.ws.message.stream;

import com.sun.istack.FinalArrayList;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.ws.message.Util;
import com.sun.xml.ws.message.stream.StreamHeader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class StreamHeader12
extends StreamHeader {
    protected static final String SOAP_1_2_MUST_UNDERSTAND = "mustUnderstand";
    protected static final String SOAP_1_2_ROLE = "role";
    protected static final String SOAP_1_2_RELAY = "relay";

    public StreamHeader12(XMLStreamReader reader, XMLStreamBuffer mark) {
        super(reader, mark);
    }

    public StreamHeader12(XMLStreamReader reader) throws XMLStreamException {
        super(reader);
    }

    @Override
    protected final FinalArrayList<StreamHeader.Attribute> processHeaderAttributes(XMLStreamReader reader) {
        FinalArrayList atts = null;
        this._role = "http://www.w3.org/2003/05/soap-envelope/role/ultimateReceiver";
        for (int i = 0; i < reader.getAttributeCount(); ++i) {
            String localName = reader.getAttributeLocalName(i);
            String namespaceURI = reader.getAttributeNamespace(i);
            String value = reader.getAttributeValue(i);
            if ("http://www.w3.org/2003/05/soap-envelope".equals(namespaceURI)) {
                if (SOAP_1_2_MUST_UNDERSTAND.equals(localName)) {
                    this._isMustUnderstand = Util.parseBool(value);
                } else if (SOAP_1_2_ROLE.equals(localName)) {
                    if (value != null && value.length() > 0) {
                        this._role = value;
                    }
                } else if (SOAP_1_2_RELAY.equals(localName)) {
                    this._isRelay = Util.parseBool(value);
                }
            }
            if (atts == null) {
                atts = new FinalArrayList();
            }
            atts.add((Object)new StreamHeader.Attribute(namespaceURI, localName, value));
        }
        return atts;
    }
}

