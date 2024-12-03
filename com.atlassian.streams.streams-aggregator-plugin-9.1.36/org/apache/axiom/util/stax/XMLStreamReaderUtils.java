/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.util.stax;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.ext.stax.CharacterDataReader;
import org.apache.axiom.ext.stax.DelegatingXMLStreamReader;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.util.activation.EmptyDataSource;
import org.apache.axiom.util.base64.Base64DecodingOutputStreamWriter;
import org.apache.axiom.util.blob.BlobDataSource;
import org.apache.axiom.util.blob.MemoryBlob;
import org.apache.axiom.util.stax.TextFromElementReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XMLStreamReaderUtils {
    private static final String IS_BINARY = "Axiom.IsBinary";
    private static final String DATA_HANDLER = "Axiom.DataHandler";
    private static final String IS_DATA_HANDLERS_AWARE = "IsDatahandlersAwareParsing";
    private static final Log log = LogFactory.getLog(XMLStreamReaderUtils.class);

    private XMLStreamReaderUtils() {
    }

    public static DataHandlerReader getDataHandlerReader(final XMLStreamReader reader) {
        Boolean isDataHandlerAware;
        try {
            DataHandlerReader dhr = (DataHandlerReader)reader.getProperty(DataHandlerReader.PROPERTY);
            if (dhr != null) {
                return dhr;
            }
        }
        catch (IllegalArgumentException ex) {
            // empty catch block
        }
        try {
            isDataHandlerAware = (Boolean)reader.getProperty(IS_DATA_HANDLERS_AWARE);
        }
        catch (IllegalArgumentException ex) {
            return null;
        }
        if (isDataHandlerAware != null && isDataHandlerAware.booleanValue()) {
            return new DataHandlerReader(){

                public boolean isBinary() {
                    return (Boolean)reader.getProperty(XMLStreamReaderUtils.IS_BINARY);
                }

                public boolean isOptimized() {
                    return true;
                }

                public boolean isDeferred() {
                    return false;
                }

                public String getContentID() {
                    return null;
                }

                public DataHandler getDataHandler() {
                    return (DataHandler)reader.getProperty(XMLStreamReaderUtils.DATA_HANDLER);
                }

                public DataHandlerProvider getDataHandlerProvider() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        return null;
    }

    public static Object processGetProperty(DataHandlerReader extension, String propertyName) {
        if (extension == null || propertyName == null) {
            throw new IllegalArgumentException();
        }
        if (propertyName.equals(DataHandlerReader.PROPERTY)) {
            return extension;
        }
        if (propertyName.equals(IS_DATA_HANDLERS_AWARE)) {
            return Boolean.TRUE;
        }
        if (propertyName.equals(IS_BINARY)) {
            return extension.isBinary();
        }
        if (propertyName.equals(DATA_HANDLER)) {
            try {
                return extension.getDataHandler();
            }
            catch (XMLStreamException ex) {
                throw new RuntimeException(ex);
            }
        }
        return null;
    }

    public static DataHandler getDataHandlerFromElement(XMLStreamReader reader) throws XMLStreamException {
        int event = reader.next();
        if (event == 2) {
            return new DataHandler((DataSource)new EmptyDataSource("application/octet-stream"));
        }
        if (event != 4) {
            throw new XMLStreamException("Expected a CHARACTER event");
        }
        DataHandlerReader dhr = XMLStreamReaderUtils.getDataHandlerReader(reader);
        if (dhr != null && dhr.isBinary()) {
            DataHandler dh = dhr.getDataHandler();
            reader.next();
            return dh;
        }
        MemoryBlob blob = new MemoryBlob();
        Base64DecodingOutputStreamWriter out = new Base64DecodingOutputStreamWriter(blob.getOutputStream());
        try {
            XMLStreamReaderUtils.writeTextTo(reader, out);
            block6: while (true) {
                switch (reader.next()) {
                    case 4: {
                        XMLStreamReaderUtils.writeTextTo(reader, out);
                        continue block6;
                    }
                    case 2: {
                        break block6;
                    }
                    default: {
                        throw new XMLStreamException("Expected a CHARACTER event");
                    }
                }
                break;
            }
            ((Writer)out).close();
        }
        catch (IOException ex) {
            throw new XMLStreamException("Error during base64 decoding", ex);
        }
        return new DataHandler((DataSource)new BlobDataSource(blob, "application/octet-string"));
    }

    public static void writeTextTo(XMLStreamReader reader, Writer writer) throws XMLStreamException, IOException {
        CharacterDataReader cdataReader;
        try {
            cdataReader = (CharacterDataReader)reader.getProperty(CharacterDataReader.PROPERTY);
        }
        catch (IllegalArgumentException ex) {
            cdataReader = null;
        }
        if (cdataReader != null) {
            cdataReader.writeTextTo(writer);
        } else {
            writer.write(reader.getText());
        }
    }

    public static Reader getElementTextAsStream(XMLStreamReader reader, boolean allowNonTextChildren) {
        if (reader.getEventType() != 1) {
            throw new IllegalStateException("Reader must be on a START_ELEMENT event");
        }
        return new TextFromElementReader(reader, allowNonTextChildren);
    }

    public static XMLStreamReader getOriginalXMLStreamReader(XMLStreamReader parser) {
        String clsName;
        if (log.isDebugEnabled()) {
            clsName = parser != null ? parser.getClass().toString() : "null";
            log.debug((Object)("Entry getOriginalXMLStreamReader: " + clsName));
        }
        while (parser instanceof DelegatingXMLStreamReader) {
            parser = ((DelegatingXMLStreamReader)parser).getParent();
            if (!log.isDebugEnabled()) continue;
            clsName = parser != null ? parser.getClass().toString() : "null";
            log.debug((Object)("  parent: " + clsName));
        }
        if (log.isDebugEnabled()) {
            clsName = parser != null ? parser.getClass().toString() : "null";
            log.debug((Object)("Exit getOriginalXMLStreamReader: " + clsName));
        }
        return parser;
    }
}

