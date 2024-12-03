/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.om.impl.llom;

import java.io.OutputStream;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMSerializable;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class OMSerializableImpl
implements OMSerializable {
    private static final Log log = LogFactory.getLog(OMSerializableImpl.class);
    protected OMFactory factory;

    public OMSerializableImpl(OMFactory factory) {
        this.factory = factory;
    }

    public OMFactory getOMFactory() {
        if (this.factory == null) {
            this.factory = ((StAXSOAPModelBuilder)this.getBuilder()).getSOAPFactory();
        }
        return this.factory;
    }

    public abstract OMXMLParserWrapper getBuilder();

    public void close(boolean build) {
        OMXMLParserWrapper builder = this.getBuilder();
        if (build) {
            this.build();
        }
        this.setComplete(true);
        if (builder instanceof StAXBuilder && !((StAXBuilder)builder).isClosed()) {
            ((StAXBuilder)builder).releaseParserOnClose(true);
            ((StAXBuilder)builder).close();
        }
    }

    public abstract void setComplete(boolean var1);

    public abstract void internalSerialize(XMLStreamWriter var1, boolean var2) throws XMLStreamException;

    public void serialize(XMLStreamWriter xmlWriter) throws XMLStreamException {
        this.serialize(xmlWriter, true);
    }

    public void serializeAndConsume(XMLStreamWriter xmlWriter) throws XMLStreamException {
        this.serialize(xmlWriter, false);
    }

    public void serialize(XMLStreamWriter xmlWriter, boolean cache) throws XMLStreamException {
        MTOMXMLStreamWriter writer = xmlWriter instanceof MTOMXMLStreamWriter ? (MTOMXMLStreamWriter)xmlWriter : new MTOMXMLStreamWriter(xmlWriter);
        this.internalSerialize(writer, cache);
        writer.flush();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void serialize(OutputStream output) throws XMLStreamException {
        XMLStreamWriter xmlStreamWriter = StAXUtils.createXMLStreamWriter(output);
        try {
            this.serialize(xmlStreamWriter);
        }
        finally {
            xmlStreamWriter.close();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void serialize(Writer writer) throws XMLStreamException {
        XMLStreamWriter xmlStreamWriter = StAXUtils.createXMLStreamWriter(writer);
        try {
            this.serialize(xmlStreamWriter);
        }
        finally {
            xmlStreamWriter.close();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void serializeAndConsume(OutputStream output) throws XMLStreamException {
        XMLStreamWriter xmlStreamWriter = StAXUtils.createXMLStreamWriter(output);
        try {
            this.serializeAndConsume(xmlStreamWriter);
        }
        finally {
            xmlStreamWriter.close();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void serializeAndConsume(Writer writer) throws XMLStreamException {
        XMLStreamWriter xmlStreamWriter = StAXUtils.createXMLStreamWriter(writer);
        try {
            this.serializeAndConsume(xmlStreamWriter);
        }
        finally {
            xmlStreamWriter.close();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void serialize(OutputStream output, OMOutputFormat format) throws XMLStreamException {
        MTOMXMLStreamWriter writer = new MTOMXMLStreamWriter(output, format, true);
        try {
            this.internalSerialize(writer, true);
            writer.flush();
        }
        finally {
            writer.close();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void serialize(Writer writer2, OMOutputFormat format) throws XMLStreamException {
        MTOMXMLStreamWriter writer = new MTOMXMLStreamWriter(StAXUtils.createXMLStreamWriter(writer2));
        writer.setOutputFormat(format);
        try {
            this.internalSerialize(writer, true);
            writer.flush();
        }
        finally {
            writer.close();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void serializeAndConsume(OutputStream output, OMOutputFormat format) throws XMLStreamException {
        MTOMXMLStreamWriter writer = new MTOMXMLStreamWriter(output, format, false);
        try {
            this.internalSerialize(writer, false);
            writer.flush();
        }
        finally {
            writer.close();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void serializeAndConsume(Writer writer2, OMOutputFormat format) throws XMLStreamException {
        MTOMXMLStreamWriter writer = new MTOMXMLStreamWriter(StAXUtils.createXMLStreamWriter(writer2));
        writer.setOutputFormat(format);
        try {
            this.internalSerialize(writer, false);
            writer.flush();
        }
        finally {
            writer.close();
        }
    }
}

