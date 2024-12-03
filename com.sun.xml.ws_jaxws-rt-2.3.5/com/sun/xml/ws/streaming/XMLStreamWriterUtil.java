/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.streaming;

import com.sun.istack.Nullable;
import com.sun.xml.ws.api.streaming.XMLStreamWriterFactory;
import com.sun.xml.ws.encoding.HasEncoding;
import com.sun.xml.ws.streaming.PrefixFactory;
import java.io.OutputStream;
import java.util.Map;
import java.util.Objects;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class XMLStreamWriterUtil {
    private XMLStreamWriterUtil() {
    }

    @Nullable
    public static OutputStream getOutputStream(XMLStreamWriter writer) throws XMLStreamException {
        XMLStreamWriter xmlStreamWriter;
        XMLStreamWriter w = Objects.requireNonNull(writer);
        Object obj = null;
        XMLStreamWriter xMLStreamWriter = xmlStreamWriter = w instanceof XMLStreamWriterFactory.HasEncodingWriter ? ((XMLStreamWriterFactory.HasEncodingWriter)w).getWriter() : w;
        if (xmlStreamWriter instanceof Map) {
            obj = ((Map)((Object)xmlStreamWriter)).get("sjsxp-outputstream");
        }
        if (obj == null) {
            try {
                obj = w.getProperty("com.ctc.wstx.outputUnderlyingStream");
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (obj == null) {
            try {
                obj = w.getProperty("http://java.sun.com/xml/stream/properties/outputstream");
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (obj != null) {
            w.writeCharacters("");
            w.flush();
            return (OutputStream)obj;
        }
        return null;
    }

    @Nullable
    public static String getEncoding(XMLStreamWriter writer) {
        return writer instanceof HasEncoding ? ((HasEncoding)((Object)writer)).getEncoding() : null;
    }

    public static String encodeQName(XMLStreamWriter writer, QName qname, PrefixFactory prefixFactory) {
        try {
            String namespaceURI = qname.getNamespaceURI();
            String localPart = qname.getLocalPart();
            if (namespaceURI == null || namespaceURI.equals("")) {
                return localPart;
            }
            String prefix = writer.getPrefix(namespaceURI);
            if (prefix == null) {
                prefix = prefixFactory.getPrefix(namespaceURI);
                writer.writeNamespace(prefix, namespaceURI);
            }
            return prefix + ":" + localPart;
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }
}

