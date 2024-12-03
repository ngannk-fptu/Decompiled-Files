/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter.node.stax;

import com.atlassian.dbexporter.ImportExportErrorService;
import com.atlassian.dbexporter.node.NodeParser;
import com.atlassian.dbexporter.node.NodeStreamReader;
import com.atlassian.dbexporter.node.stax.StaxUtils;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public final class StaxStreamReader
implements NodeStreamReader {
    private static final String XMLSCHEMA_URI = "http://www.w3.org/2001/XMLSchema-instance";
    private final ImportExportErrorService errorService;
    private final XMLStreamReader reader;

    public StaxStreamReader(ImportExportErrorService errorService, Reader input) {
        this.errorService = Objects.requireNonNull(errorService);
        this.reader = this.createXmlStreamReader(Objects.requireNonNull(input));
    }

    private XMLStreamReader createXmlStreamReader(Reader reader) {
        try {
            return StaxUtils.newXmlInputFactory().createXMLStreamReader(reader);
        }
        catch (XMLStreamException e) {
            throw this.errorService.newParseException(e);
        }
    }

    @Override
    public NodeParser getRootNode() {
        if (this.reader.getEventType() != 7) {
            throw new IllegalStateException("The root node has already been returned.");
        }
        try {
            this.reader.nextTag();
            return new NodeParser(){

                @Override
                public String getAttribute(String key) {
                    return this.getAttribute(key, null, false);
                }

                @Override
                public String getRequiredAttribute(String key) {
                    return this.getAttribute(key, null, true);
                }

                private String getAttribute(String key, String namespaceUri, boolean required) {
                    this.requireStartElement();
                    for (int i = 0; i < StaxStreamReader.this.reader.getAttributeCount(); ++i) {
                        if (!key.equals(StaxStreamReader.this.reader.getAttributeName(i).getLocalPart()) || namespaceUri != null && !namespaceUri.equals(StaxStreamReader.this.reader.getAttributeName(i).getNamespaceURI())) continue;
                        return StaxUtils.unicodeDecode(StaxStreamReader.this.reader.getAttributeValue(i));
                    }
                    if (required) {
                        throw StaxStreamReader.this.errorService.newParseException(String.format("Required attribute %s not found in node %s", key, this.getName()));
                    }
                    return null;
                }

                @Override
                public String getName() {
                    return StaxStreamReader.this.reader.getLocalName();
                }

                @Override
                public boolean isClosed() {
                    return StaxStreamReader.this.reader.getEventType() == 2 || StaxStreamReader.this.reader.getEventType() == 8;
                }

                private int nextTagOrEndOfDocument() {
                    try {
                        int eventType = StaxStreamReader.this.reader.next();
                        while (eventType == 4 && StaxStreamReader.this.reader.isWhiteSpace() || eventType == 12 && StaxStreamReader.this.reader.isWhiteSpace() || eventType == 6 || eventType == 3 || eventType == 5) {
                            eventType = StaxStreamReader.this.reader.next();
                        }
                        if (eventType != 1 && eventType != 2 && eventType != 8) {
                            throw StaxStreamReader.this.errorService.newParseException("Unable to find start or end tag, or end of document. Location: " + StaxStreamReader.this.reader.getLocation());
                        }
                        return eventType;
                    }
                    catch (XMLStreamException e) {
                        throw StaxStreamReader.this.errorService.newParseException(e);
                    }
                }

                @Override
                public NodeParser getNextNode() {
                    int event = this.nextTagOrEndOfDocument();
                    assert (StaxStreamReader.this.reader.isStartElement() || StaxStreamReader.this.reader.isEndElement() || 8 == StaxStreamReader.this.reader.getEventType());
                    return 8 == event ? null : this;
                }

                @Override
                public String getContentAsString() {
                    this.requireStartElement();
                    try {
                        if (Boolean.parseBoolean(this.getAttribute("nil", StaxStreamReader.XMLSCHEMA_URI, false))) {
                            this.nextTagOrEndOfDocument();
                            return null;
                        }
                        return StaxUtils.unicodeDecode(StaxStreamReader.this.reader.getElementText());
                    }
                    catch (XMLStreamException e) {
                        throw StaxStreamReader.this.errorService.newParseException(e);
                    }
                }

                @Override
                public Boolean getContentAsBoolean() {
                    String value = this.getContentAsString();
                    return value == null ? null : Boolean.valueOf(Boolean.parseBoolean(value));
                }

                @Override
                public Date getContentAsDate() {
                    String value = this.getContentAsString();
                    try {
                        return value == null ? null : StaxUtils.newDateFormat().parse(value);
                    }
                    catch (ParseException pe) {
                        throw StaxStreamReader.this.errorService.newParseException(pe);
                    }
                }

                @Override
                public BigInteger getContentAsBigInteger() {
                    String value = this.getContentAsString();
                    return value == null ? null : new BigInteger(value);
                }

                @Override
                public BigDecimal getContentAsBigDecimal() {
                    String value = this.getContentAsString();
                    return value == null ? null : new BigDecimal(value);
                }

                @Override
                public void getContent(Writer writer) {
                    throw new AssertionError((Object)"Not implemented.");
                }

                private void requireStartElement() throws IllegalStateException {
                    if (!StaxStreamReader.this.reader.isStartElement()) {
                        throw new IllegalStateException("Not currently positioned at the start of a node.");
                    }
                }

                public String toString() {
                    StringBuilder sb = new StringBuilder();
                    sb.append("<");
                    if (this.isClosed()) {
                        sb.append("/");
                    }
                    sb.append(this.getName());
                    if (!this.isClosed()) {
                        for (int i = 0; i < StaxStreamReader.this.reader.getAttributeCount(); ++i) {
                            sb.append(" ").append(StaxStreamReader.this.reader.getAttributeName(i).getLocalPart()).append("=\"").append(StaxUtils.unicodeDecode(StaxStreamReader.this.reader.getAttributeValue(i))).append("\"");
                        }
                    }
                    sb.append(">");
                    return sb.toString();
                }
            };
        }
        catch (XMLStreamException e) {
            throw this.errorService.newParseException(e);
        }
    }

    @Override
    public void close() {
        try {
            this.reader.close();
        }
        catch (XMLStreamException e) {
            throw this.errorService.newParseException(e);
        }
    }
}

