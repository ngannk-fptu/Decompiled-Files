/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.impl.serialize.StreamingOMSerializer;
import org.apache.axiom.om.impl.util.OMSerializerUtil;

public class OMDocumentImplUtil {
    private OMDocumentImplUtil() {
    }

    public static void internalSerialize(OMDocument document, XMLStreamWriter writer2, boolean cache, boolean includeXMLDeclaration) throws XMLStreamException {
        MTOMXMLStreamWriter writer = (MTOMXMLStreamWriter)writer2;
        if (includeXMLDeclaration) {
            String version;
            String encoding = writer.getCharSetEncoding();
            if (encoding == null || "".equals(encoding)) {
                encoding = document.getCharsetEncoding();
            }
            if ((version = document.getXMLVersion()) == null) {
                version = "1.0";
            }
            if (encoding == null) {
                writer.getXmlStreamWriter().writeStartDocument(version);
            } else {
                writer.getXmlStreamWriter().writeStartDocument(encoding, version);
            }
        }
        if (cache || document.isComplete() || document.getBuilder() == null) {
            OMSerializerUtil.serializeChildren(document, writer, cache);
        } else {
            StreamingOMSerializer streamingOMSerializer = new StreamingOMSerializer();
            XMLStreamReader reader = document.getXMLStreamReaderWithoutCaching();
            while (reader.getEventType() != 8) {
                streamingOMSerializer.serialize(reader, writer);
            }
        }
    }
}

