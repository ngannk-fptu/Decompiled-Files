/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import javanet.staxutils.IndentingXMLEventWriter;
import javanet.staxutils.IndentingXMLStreamWriter;
import javanet.staxutils.helpers.FilterXMLOutputFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

public class StaxUtilsXMLOutputFactory
extends FilterXMLOutputFactory {
    public static final String INDENTING = "net.java.staxutils.indenting";
    public static final String INDENT = "net.java.staxutils.indent";
    public static final String NEW_LINE = "net.java.staxutils.newLine";
    private boolean indenting = false;
    private String indent = "  ";
    private String newLine = "\n";

    public StaxUtilsXMLOutputFactory() {
    }

    public StaxUtilsXMLOutputFactory(XMLOutputFactory source) {
        super(source);
    }

    protected XMLEventWriter filter(XMLEventWriter writer) {
        if (this.indenting) {
            IndentingXMLEventWriter indenter = new IndentingXMLEventWriter(writer);
            indenter.setNewLine(this.newLine);
            indenter.setIndent(this.indent);
            writer = indenter;
        }
        return writer;
    }

    protected XMLStreamWriter filter(XMLStreamWriter writer) {
        if (this.indenting) {
            IndentingXMLStreamWriter indenter = new IndentingXMLStreamWriter(writer);
            indenter.setNewLine(this.newLine);
            indenter.setIndent(this.indent);
            writer = indenter;
        }
        return writer;
    }

    public boolean isPropertySupported(String name) {
        return INDENTING.equals(name) || INDENT.equals(name) || NEW_LINE.equals(name) || super.isPropertySupported(name);
    }

    public void setProperty(String name, Object value) throws IllegalArgumentException {
        if (INDENTING.equals(name)) {
            this.indenting = (Boolean)value;
        } else if (INDENT.equals(name)) {
            this.indent = (String)value;
        } else if (NEW_LINE.equals(name)) {
            this.newLine = (String)value;
        } else {
            super.setProperty(name, value);
        }
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        if (INDENTING.equals(name)) {
            return this.indenting ? Boolean.TRUE : Boolean.FALSE;
        }
        if (INDENT.equals(name)) {
            return this.indent;
        }
        if (NEW_LINE.equals(name)) {
            return this.newLine;
        }
        return super.getProperty(name);
    }

    public int hashCode() {
        return super.hashCode() + (this.indenting ? 1 : 0) + StaxUtilsXMLOutputFactory.hashCode(this.indent) + StaxUtilsXMLOutputFactory.hashCode(this.newLine);
    }

    public boolean equals(Object o) {
        if (!(o instanceof StaxUtilsXMLOutputFactory)) {
            return false;
        }
        StaxUtilsXMLOutputFactory that = (StaxUtilsXMLOutputFactory)o;
        return super.equals(that) && this.indenting == that.indenting && StaxUtilsXMLOutputFactory.equals(this.indent, that.indent) && StaxUtilsXMLOutputFactory.equals(this.newLine, that.newLine);
    }
}

