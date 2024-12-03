/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.axiom.util.stax;

import java.io.IOException;
import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.ext.stax.datahandler.DataHandlerWriter;
import org.apache.axiom.util.base64.Base64EncodingWriterOutputStream;
import org.apache.axiom.util.stax.XMLStreamIOException;
import org.apache.axiom.util.stax.XMLStreamWriterWriter;

public class XMLStreamWriterUtils {
    public static void writeBase64(XMLStreamWriter writer, DataHandler dh) throws IOException, XMLStreamException {
        Base64EncodingWriterOutputStream out = new Base64EncodingWriterOutputStream(new XMLStreamWriterWriter(writer));
        try {
            dh.writeTo((OutputStream)out);
            out.close();
        }
        catch (XMLStreamIOException ex) {
            throw ex.getXMLStreamException();
        }
    }

    private static DataHandlerWriter internalGetDataHandlerWriter(XMLStreamWriter writer) {
        try {
            return (DataHandlerWriter)writer.getProperty(DataHandlerWriter.PROPERTY);
        }
        catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public static DataHandlerWriter getDataHandlerWriter(final XMLStreamWriter writer) {
        DataHandlerWriter dataHandlerWriter = XMLStreamWriterUtils.internalGetDataHandlerWriter(writer);
        if (dataHandlerWriter == null) {
            return new DataHandlerWriter(){

                public void writeDataHandler(DataHandler dataHandler, String contentID, boolean optimize) throws IOException, XMLStreamException {
                    XMLStreamWriterUtils.writeBase64(writer, dataHandler);
                }

                public void writeDataHandler(DataHandlerProvider dataHandlerProvider, String contentID, boolean optimize) throws IOException, XMLStreamException {
                    XMLStreamWriterUtils.writeBase64(writer, dataHandlerProvider.getDataHandler());
                }
            };
        }
        return dataHandlerWriter;
    }

    public static void writeDataHandler(XMLStreamWriter writer, DataHandler dataHandler, String contentID, boolean optimize) throws IOException, XMLStreamException {
        DataHandlerWriter dataHandlerWriter = XMLStreamWriterUtils.internalGetDataHandlerWriter(writer);
        if (dataHandlerWriter != null) {
            dataHandlerWriter.writeDataHandler(dataHandler, contentID, optimize);
        } else {
            XMLStreamWriterUtils.writeBase64(writer, dataHandler);
        }
    }

    public static void writeDataHandler(XMLStreamWriter writer, DataHandlerProvider dataHandlerProvider, String contentID, boolean optimize) throws IOException, XMLStreamException {
        DataHandlerWriter dataHandlerWriter = XMLStreamWriterUtils.internalGetDataHandlerWriter(writer);
        if (dataHandlerWriter != null) {
            dataHandlerWriter.writeDataHandler(dataHandlerProvider, contentID, optimize);
        } else {
            XMLStreamWriterUtils.writeBase64(writer, dataHandlerProvider.getDataHandler());
        }
    }

    public static void writeDTD(XMLStreamWriter writer, String rootName, String publicId, String systemId, String internalSubset) throws XMLStreamException {
        StringBuilder buffer = new StringBuilder("<!DOCTYPE ");
        buffer.append(rootName);
        if (publicId != null) {
            buffer.append(" PUBLIC \"");
            buffer.append(publicId);
            buffer.append("\" \"");
            buffer.append(systemId);
            buffer.append("\"");
        } else if (systemId != null) {
            buffer.append(" SYSTEM \"");
            buffer.append(systemId);
            buffer.append("\"");
        }
        if (internalSubset != null) {
            buffer.append(" [");
            buffer.append(internalSubset);
            buffer.append("]");
        }
        buffer.append(">");
        writer.writeDTD(buffer.toString());
    }
}

