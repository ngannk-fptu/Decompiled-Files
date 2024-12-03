/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.om.impl.serialize;

import java.io.IOException;
import java.util.ArrayList;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.ext.stax.datahandler.DataHandlerWriter;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMSerializer;
import org.apache.axiom.om.impl.OMXMLStreamReaderEx;
import org.apache.axiom.om.impl.util.OMSerializerUtil;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;
import org.apache.axiom.util.stax.XMLStreamWriterUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StreamingOMSerializer
implements XMLStreamConstants,
OMSerializer {
    private static final Log log = LogFactory.getLog(StreamingOMSerializer.class);
    private static int namespaceSuffix = 0;
    public static final String NAMESPACE_PREFIX = "ns";
    private static final String XSI_URI = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String XSI_LOCAL_NAME = "type";
    private int depth = 0;
    private DataHandlerReader dataHandlerReader;
    private DataHandlerWriter dataHandlerWriter;

    public void serialize(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        this.serialize(reader, writer, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void serialize(XMLStreamReader reader, XMLStreamWriter writer, boolean startAtNext) throws XMLStreamException {
        int event;
        this.dataHandlerReader = XMLStreamReaderUtils.getDataHandlerReader(reader);
        this.dataHandlerWriter = XMLStreamWriterUtils.getDataHandlerWriter(writer);
        if (reader instanceof OMXMLStreamReaderEx && ((event = reader.getEventType()) <= 0 || event == 3 || event == 7)) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"Enable OMDataSource events while serializing this document");
            }
            ((OMXMLStreamReaderEx)reader).enableDataSourceEvents(true);
        }
        try {
            this.serializeNode(reader, writer, startAtNext);
        }
        finally {
            if (reader instanceof OMXMLStreamReaderEx) {
                ((OMXMLStreamReaderEx)reader).enableDataSourceEvents(false);
            }
        }
    }

    protected void serializeNode(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        this.serializeNode(reader, writer, true);
    }

    protected void serializeNode(XMLStreamReader reader, XMLStreamWriter writer, boolean startAtNext) throws XMLStreamException {
        boolean useCurrentEvent;
        boolean bl = useCurrentEvent = !startAtNext;
        while (reader.hasNext() || useCurrentEvent) {
            int event = 0;
            OMDataSource ds = null;
            if (useCurrentEvent) {
                event = reader.getEventType();
                useCurrentEvent = false;
            } else {
                event = reader.next();
            }
            if (reader instanceof OMXMLStreamReaderEx) {
                ds = ((OMXMLStreamReaderEx)reader).getDataSource();
            }
            if (ds != null) {
                ds.serialize(writer);
            } else {
                switch (event) {
                    case 1: {
                        this.serializeElement(reader, writer);
                        ++this.depth;
                        break;
                    }
                    case 10: {
                        this.serializeAttributes(reader, writer);
                        break;
                    }
                    case 4: {
                        if (this.dataHandlerReader != null && this.dataHandlerReader.isBinary()) {
                            this.serializeDataHandler();
                            break;
                        }
                    }
                    case 6: {
                        this.serializeText(reader, writer);
                        break;
                    }
                    case 5: {
                        this.serializeComment(reader, writer);
                        break;
                    }
                    case 12: {
                        this.serializeCData(reader, writer);
                        break;
                    }
                    case 3: {
                        this.serializeProcessingInstruction(reader, writer);
                        break;
                    }
                    case 2: {
                        this.serializeEndElement(writer);
                        --this.depth;
                        break;
                    }
                    case 7: {
                        ++this.depth;
                        break;
                    }
                    case 8: {
                        if (this.depth != 0) {
                            --this.depth;
                        }
                        try {
                            this.serializeEndElement(writer);
                        }
                        catch (Exception e) {}
                        break;
                    }
                    case 11: {
                        this.serializeDTD(reader, writer);
                        break;
                    }
                    case 9: {
                        writer.writeEntityRef(reader.getLocalName());
                    }
                }
            }
            if (this.depth != 0) continue;
            break;
        }
    }

    protected void serializeElement(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        String namespace;
        String prefix;
        int i;
        ArrayList<String> writePrefixList = null;
        ArrayList<String> writeNSList = null;
        String ePrefix = reader.getPrefix();
        ePrefix = ePrefix != null && ePrefix.length() == 0 ? null : ePrefix;
        String eNamespace = reader.getNamespaceURI();
        String string = eNamespace = eNamespace != null && eNamespace.length() == 0 ? null : eNamespace;
        if (eNamespace != null) {
            if (ePrefix == null) {
                if (!OMSerializerUtil.isAssociated("", eNamespace, writer)) {
                    if (writePrefixList == null) {
                        writePrefixList = new ArrayList<String>();
                        writeNSList = new ArrayList<String>();
                    }
                    writePrefixList.add("");
                    writeNSList.add(eNamespace);
                }
                writer.writeStartElement("", reader.getLocalName(), eNamespace);
            } else {
                if (!OMSerializerUtil.isAssociated(ePrefix, eNamespace, writer)) {
                    if (writePrefixList == null) {
                        writePrefixList = new ArrayList();
                        writeNSList = new ArrayList();
                    }
                    writePrefixList.add(ePrefix);
                    writeNSList.add(eNamespace);
                }
                writer.writeStartElement(ePrefix, reader.getLocalName(), eNamespace);
            }
        } else {
            writer.writeStartElement(reader.getLocalName());
        }
        int count = reader.getNamespaceCount();
        for (int i2 = 0; i2 < count; ++i2) {
            String prefix2 = reader.getNamespacePrefix(i2);
            prefix2 = prefix2 != null && prefix2.length() == 0 ? null : prefix2;
            String namespace2 = reader.getNamespaceURI(i2);
            String newPrefix = OMSerializerUtil.generateSetPrefix(prefix2, namespace2 = namespace2 != null && namespace2.length() == 0 ? null : namespace2, writer, false);
            if (newPrefix == null) continue;
            if (writePrefixList == null) {
                writePrefixList = new ArrayList();
                writeNSList = new ArrayList();
            }
            if (writePrefixList.contains(newPrefix)) continue;
            writePrefixList.add(newPrefix);
            writeNSList.add(namespace2);
        }
        String newPrefix = OMSerializerUtil.generateSetPrefix(ePrefix, eNamespace, writer, false);
        if (newPrefix != null) {
            if (writePrefixList == null) {
                writePrefixList = new ArrayList();
                writeNSList = new ArrayList();
            }
            if (!writePrefixList.contains(newPrefix)) {
                writePrefixList.add(newPrefix);
                writeNSList.add(eNamespace);
            }
        }
        count = reader.getAttributeCount();
        for (i = 0; i < count; ++i) {
            prefix = reader.getAttributePrefix(i);
            prefix = prefix != null && prefix.length() == 0 ? null : prefix;
            namespace = reader.getAttributeNamespace(i);
            String string2 = namespace = namespace != null && namespace.length() == 0 ? null : namespace;
            if (prefix == null && namespace != null) {
                String writerPrefix = writer.getPrefix(namespace);
                writerPrefix = writerPrefix != null && writerPrefix.length() == 0 ? null : writerPrefix;
                String string3 = prefix = writerPrefix != null ? writerPrefix : this.generateUniquePrefix(writer.getNamespaceContext());
            }
            if ((newPrefix = OMSerializerUtil.generateSetPrefix(prefix, namespace, writer, true)) == null) continue;
            if (writePrefixList == null) {
                writePrefixList = new ArrayList();
                writeNSList = new ArrayList();
            }
            if (writePrefixList.contains(newPrefix)) continue;
            writePrefixList.add(newPrefix);
            writeNSList.add(namespace);
        }
        count = reader.getAttributeCount();
        for (i = 0; i < count; ++i) {
            String refPrefix;
            String refNamespace;
            prefix = reader.getAttributePrefix(i);
            prefix = prefix != null && prefix.length() == 0 ? null : prefix;
            namespace = reader.getAttributeNamespace(i);
            namespace = namespace != null && namespace.length() == 0 ? null : namespace;
            String localName = reader.getAttributeLocalName(i);
            if (!XSI_URI.equals(namespace) || !XSI_LOCAL_NAME.equals(localName)) continue;
            String value = reader.getAttributeValue(i);
            if (log.isDebugEnabled()) {
                log.debug((Object)("The value of xsi:type is " + value));
            }
            if (value == null || (value = value.trim()).indexOf(":") <= 0 || (refNamespace = reader.getNamespaceURI(refPrefix = value.substring(0, value.indexOf(":")))) == null || refNamespace.length() <= 0 || (newPrefix = OMSerializerUtil.generateSetPrefix(refPrefix, refNamespace, writer, true)) == null) continue;
            if (log.isDebugEnabled()) {
                log.debug((Object)("An xmlns:" + newPrefix + "=\"" + refNamespace + "\" will be written"));
            }
            if (writePrefixList == null) {
                writePrefixList = new ArrayList();
                writeNSList = new ArrayList();
            }
            if (writePrefixList.contains(newPrefix)) continue;
            writePrefixList.add(newPrefix);
            writeNSList.add(refNamespace);
        }
        if (writePrefixList != null) {
            for (i = 0; i < writePrefixList.size(); ++i) {
                prefix = (String)writePrefixList.get(i);
                namespace = (String)writeNSList.get(i);
                if (prefix != null) {
                    if (namespace == null) {
                        writer.writeNamespace(prefix, "");
                        continue;
                    }
                    writer.writeNamespace(prefix, namespace);
                    continue;
                }
                writer.writeDefaultNamespace(namespace);
            }
        }
        count = reader.getAttributeCount();
        for (i = 0; i < count; ++i) {
            String writerPrefix;
            prefix = reader.getAttributePrefix(i);
            prefix = prefix != null && prefix.length() == 0 ? null : prefix;
            namespace = reader.getAttributeNamespace(i);
            String string4 = namespace = namespace != null && namespace.length() == 0 ? null : namespace;
            if (prefix == null && namespace != null) {
                prefix = writer.getPrefix(namespace);
                if (prefix == null || "".equals(prefix)) {
                    for (int j = 0; j < writePrefixList.size(); ++j) {
                        if (!namespace.equals((String)writeNSList.get(j))) continue;
                        prefix = (String)writePrefixList.get(j);
                    }
                }
            } else if (!(namespace == null || prefix.equals("xml") || prefix.equals(writerPrefix = writer.getPrefix(namespace)) || "".equals(writerPrefix))) {
                prefix = writerPrefix;
            }
            if (namespace != null) {
                writer.writeAttribute(prefix, namespace, reader.getAttributeLocalName(i), reader.getAttributeValue(i));
                continue;
            }
            writer.writeAttribute(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
        }
    }

    protected void serializeEndElement(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndElement();
    }

    protected void serializeText(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        writer.writeCharacters(reader.getText());
    }

    protected void serializeCData(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        writer.writeCData(reader.getText());
    }

    protected void serializeComment(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        writer.writeComment(reader.getText());
    }

    protected void serializeProcessingInstruction(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        writer.writeProcessingInstruction(reader.getPITarget(), reader.getPIData());
    }

    protected void serializeAttributes(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        int count = reader.getAttributeCount();
        String prefix = null;
        String namespaceName = null;
        String writerPrefix = null;
        for (int i = 0; i < count; ++i) {
            prefix = reader.getAttributePrefix(i);
            namespaceName = reader.getAttributeNamespace(i);
            namespaceName = namespaceName == null ? "" : namespaceName;
            writerPrefix = writer.getPrefix(namespaceName);
            if (!"".equals(namespaceName)) {
                if (writerPrefix != null && (prefix == null || prefix.equals(""))) {
                    writer.writeAttribute(writerPrefix, namespaceName, reader.getAttributeLocalName(i), reader.getAttributeValue(i));
                    continue;
                }
                if (prefix != null && !"".equals(prefix) && !prefix.equals(writerPrefix)) {
                    writer.writeNamespace(prefix, namespaceName);
                    writer.writeAttribute(prefix, namespaceName, reader.getAttributeLocalName(i), reader.getAttributeValue(i));
                    continue;
                }
                prefix = this.generateUniquePrefix(writer.getNamespaceContext());
                writer.writeNamespace(prefix, namespaceName);
                writer.writeAttribute(prefix, namespaceName, reader.getAttributeLocalName(i), reader.getAttributeValue(i));
                continue;
            }
            writer.writeAttribute(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
        }
    }

    private String generateUniquePrefix(NamespaceContext nsCtxt) {
        String prefix = NAMESPACE_PREFIX + namespaceSuffix++;
        while (nsCtxt.getNamespaceURI(prefix) != null) {
            prefix = NAMESPACE_PREFIX + namespaceSuffix++;
        }
        return prefix;
    }

    private void serializeNamespace(String prefix, String URI2, XMLStreamWriter writer) throws XMLStreamException {
        String prefix1 = writer.getPrefix(URI2);
        if (prefix1 == null) {
            writer.writeNamespace(prefix, URI2);
            writer.setPrefix(prefix, URI2);
        }
    }

    private void serializeDataHandler() throws XMLStreamException {
        try {
            if (this.dataHandlerReader.isDeferred()) {
                this.dataHandlerWriter.writeDataHandler(this.dataHandlerReader.getDataHandlerProvider(), this.dataHandlerReader.getContentID(), this.dataHandlerReader.isOptimized());
            } else {
                this.dataHandlerWriter.writeDataHandler(this.dataHandlerReader.getDataHandler(), this.dataHandlerReader.getContentID(), this.dataHandlerReader.isOptimized());
            }
        }
        catch (IOException ex) {
            throw new XMLStreamException("Error while reading data handler", ex);
        }
    }

    private void serializeDTD(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        DTDReader dtdReader;
        try {
            dtdReader = (DTDReader)reader.getProperty(DTDReader.PROPERTY);
        }
        catch (IllegalArgumentException ex) {
            dtdReader = null;
        }
        if (dtdReader == null) {
            throw new XMLStreamException("Cannot serialize the DTD because the XMLStreamReader doesn't support the DTDReader extension");
        }
        XMLStreamWriterUtils.writeDTD(writer, dtdReader.getRootName(), dtdReader.getPublicId(), dtdReader.getSystemId(), reader.getText());
    }
}

