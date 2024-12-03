/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.DatatypeConverter
 */
package com.atlassian.dbexporter.node.stax;

import com.atlassian.dbexporter.ImportExportErrorService;
import com.atlassian.dbexporter.node.NodeCreator;
import com.atlassian.dbexporter.node.NodeStreamWriter;
import com.atlassian.dbexporter.node.stax.StaxUtils;
import com.atlassian.javanet.staxutils.IndentingXMLStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Objects;
import javax.xml.bind.DatatypeConverter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public final class StaxStreamWriter
implements NodeStreamWriter {
    private static final String XMLSCHEMA_URI = "http://www.w3.org/2001/XMLSchema-instance";
    private final ImportExportErrorService errorService;
    private final XMLStreamWriter writer;
    private final String nameSpaceUri;
    private final Charset charset;
    private boolean rootExists = false;

    public StaxStreamWriter(ImportExportErrorService errorService, Writer output, Charset charset, String nameSpaceUri) {
        this.errorService = Objects.requireNonNull(errorService);
        this.writer = new IndentingXMLStreamWriter(Objects.requireNonNull(this.createXmlStreamWriter(output)));
        this.charset = Objects.requireNonNull(charset);
        this.nameSpaceUri = Objects.requireNonNull(nameSpaceUri);
    }

    private XMLStreamWriter createXmlStreamWriter(Writer writer) {
        try {
            return StaxUtils.newXmlOutputFactory().createXMLStreamWriter(writer);
        }
        catch (XMLStreamException xe) {
            throw this.errorService.newParseException(xe);
        }
    }

    @Override
    public NodeCreator addRootNode(String name) {
        if (this.rootExists) {
            throw new IllegalStateException("Root node already created.");
        }
        try {
            this.writer.writeStartDocument(this.charset.name(), "1.0");
            this.rootExists = true;
            NodeCreator nc = new NodeCreator(){
                private long depth = 0L;

                @Override
                public NodeCreator addNode(String name) {
                    try {
                        StaxStreamWriter.this.writer.writeStartElement(name);
                        ++this.depth;
                        return this;
                    }
                    catch (XMLStreamException e) {
                        throw StaxStreamWriter.this.errorService.newParseException(e);
                    }
                }

                @Override
                public NodeCreator closeEntity() {
                    try {
                        StaxStreamWriter.this.writer.writeEndElement();
                        return --this.depth == 0L ? null : this;
                    }
                    catch (XMLStreamException e) {
                        throw StaxStreamWriter.this.errorService.newParseException(e);
                    }
                }

                @Override
                public NodeCreator setContentAsDate(Date date) {
                    return this.setContentAsString(date == null ? null : StaxUtils.newDateFormat().format(date));
                }

                @Override
                public NodeCreator setContentAsBigInteger(BigInteger bigInteger) {
                    return this.setContentAsString(bigInteger == null ? null : bigInteger.toString());
                }

                @Override
                public NodeCreator setContentAsBigDecimal(BigDecimal bigDecimal) {
                    return this.setContentAsString(bigDecimal == null ? null : bigDecimal.toString());
                }

                @Override
                public NodeCreator setContentAsBoolean(Boolean bool) {
                    return this.setContentAsString(bool == null ? null : Boolean.toString(bool));
                }

                @Override
                public NodeCreator setContentAsBinary(byte[] bytes) {
                    return this.setContentAsString(bytes == null ? null : DatatypeConverter.printBase64Binary((byte[])bytes));
                }

                @Override
                public NodeCreator setContentAsString(String value) {
                    try {
                        if (value == null) {
                            StaxStreamWriter.this.writer.writeAttribute(StaxStreamWriter.XMLSCHEMA_URI, "nil", "true");
                        } else {
                            StaxStreamWriter.this.writer.writeCharacters(StaxUtils.unicodeEncode(value));
                        }
                        return this;
                    }
                    catch (XMLStreamException e) {
                        throw StaxStreamWriter.this.errorService.newParseException(e);
                    }
                }

                @Override
                public NodeCreator setContent(Reader data) {
                    throw new AssertionError((Object)"Not implemented");
                }

                @Override
                public NodeCreator addAttribute(String key, String value) {
                    try {
                        StaxStreamWriter.this.writer.writeAttribute(key, StaxUtils.unicodeEncode(value));
                        return this;
                    }
                    catch (XMLStreamException e) {
                        throw StaxStreamWriter.this.errorService.newParseException(e);
                    }
                }
            };
            NodeCreator nodeCreator = nc.addNode(name);
            this.writer.writeDefaultNamespace(this.nameSpaceUri);
            this.writer.writeNamespace("xsi", XMLSCHEMA_URI);
            return nodeCreator;
        }
        catch (XMLStreamException e) {
            throw this.errorService.newParseException("Unable to create the root node.", e);
        }
    }

    @Override
    public void flush() {
        try {
            this.writer.flush();
        }
        catch (XMLStreamException e) {
            throw this.errorService.newParseException(e);
        }
    }

    @Override
    public void close() {
        try {
            this.writer.close();
        }
        catch (XMLStreamException e) {
            throw this.errorService.newParseException(e);
        }
    }
}

