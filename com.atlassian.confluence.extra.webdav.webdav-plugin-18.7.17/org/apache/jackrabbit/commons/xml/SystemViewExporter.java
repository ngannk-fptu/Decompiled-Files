/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.xml;

import java.io.IOException;
import java.io.Writer;
import javax.jcr.Node;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import org.apache.jackrabbit.commons.xml.Exporter;
import org.apache.jackrabbit.value.ValueHelper;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class SystemViewExporter
extends Exporter {
    private static final String SV = "http://www.jcp.org/jcr/sv/1.0";
    private static final String XS = "http://www.w3.org/2001/XMLSchema";
    private static final String XSI = "http://www.w3.org/2001/XMLSchema-instance";

    public SystemViewExporter(Session session, ContentHandler handler, boolean recurse, boolean binary) {
        super(session, handler, recurse, binary);
        this.addNamespace("sv", SV);
    }

    @Override
    protected void exportNode(String uri, String local, Node node) throws RepositoryException, SAXException {
        this.addAttribute(SV, "name", this.getXMLName(uri, local));
        this.startElement(SV, "node");
        this.exportProperties(node);
        this.exportNodes(node);
        this.endElement(SV, "node");
    }

    @Override
    protected void exportProperty(String uri, String local, Value value) throws RepositoryException, SAXException {
        this.addAttribute(SV, "name", this.getXMLName(uri, local));
        this.addAttribute(SV, "type", PropertyType.nameFromValue(value.getType()));
        this.startElement(SV, "property");
        this.exportValue(value);
        this.endElement(SV, "property");
    }

    @Override
    protected void exportProperty(String uri, String local, int type, Value[] values) throws RepositoryException, SAXException {
        this.addAttribute(SV, "name", this.getXMLName(uri, local));
        this.addAttribute(SV, "type", PropertyType.nameFromValue(type));
        this.addAttribute(SV, "multiple", Boolean.TRUE.toString());
        this.startElement(SV, "property");
        for (int i = 0; i < values.length; ++i) {
            this.exportValue(values[i]);
        }
        this.endElement(SV, "property");
    }

    private void exportValue(Value value) throws RepositoryException, SAXException {
        try {
            boolean binary = this.mustSendBinary(value);
            if (binary) {
                this.addNamespace("xs", XS);
                this.addNamespace("xsi", XSI);
                this.addAttribute(XSI, "type", this.getXMLName(XS, "base64Binary"));
            }
            this.startElement(SV, "value");
            ValueHelper.serialize(value, false, binary, new Writer(){

                @Override
                public void write(char[] cbuf, int off, int len) throws IOException {
                    try {
                        SystemViewExporter.this.characters(cbuf, off, len);
                    }
                    catch (Exception e) {
                        IOException exception = new IOException();
                        exception.initCause(e);
                        throw exception;
                    }
                }

                @Override
                public void close() {
                }

                @Override
                public void flush() {
                }
            });
            this.endElement(SV, "value");
        }
        catch (IOException e) {
            if (e.getCause() instanceof SAXException) {
                throw (SAXException)e.getCause();
            }
            throw new RepositoryException(e);
        }
    }

    private boolean mustSendBinary(Value value) throws RepositoryException {
        if (value.getType() != 2) {
            String string = value.getString();
            for (int i = 0; i < string.length(); ++i) {
                char c = string.charAt(i);
                if (c < '\u0000' || c >= ' ' || c == '\n' || c == '\t') continue;
                return true;
            }
        }
        return false;
    }
}

