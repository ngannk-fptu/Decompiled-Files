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

public class StreamHeader11
extends StreamHeader {
    protected static final String SOAP_1_1_MUST_UNDERSTAND = "mustUnderstand";
    protected static final String SOAP_1_1_ROLE = "actor";

    public StreamHeader11(XMLStreamReader reader, XMLStreamBuffer mark) {
        super(reader, mark);
    }

    public StreamHeader11(XMLStreamReader reader) throws XMLStreamException {
        super(reader);
    }

    @Override
    protected final FinalArrayList<StreamHeader.Attribute> processHeaderAttributes(XMLStreamReader reader) {
        FinalArrayList atts = null;
        this._role = "http://schemas.xmlsoap.org/soap/actor/next";
        for (int i = 0; i < reader.getAttributeCount(); ++i) {
            String localName = reader.getAttributeLocalName(i);
            String namespaceURI = reader.getAttributeNamespace(i);
            String value = reader.getAttributeValue(i);
            if ("http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceURI)) {
                if (SOAP_1_1_MUST_UNDERSTAND.equals(localName)) {
                    this._isMustUnderstand = Util.parseBool(value);
                } else if (SOAP_1_1_ROLE.equals(localName) && value != null && value.length() > 0) {
                    this._role = value;
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

