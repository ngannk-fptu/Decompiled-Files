/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.util.stax.XMLStreamIOException;

public class XMLStreamWriterWriter
extends Writer {
    private final XMLStreamWriter writer;

    public XMLStreamWriterWriter(XMLStreamWriter writer) {
        this.writer = writer;
    }

    public void write(char[] cbuf, int off, int len) throws IOException {
        try {
            this.writer.writeCharacters(cbuf, off, len);
        }
        catch (XMLStreamException ex) {
            throw new XMLStreamIOException(ex);
        }
    }

    public void write(String str, int off, int len) throws IOException {
        this.write(str.substring(off, off + len));
    }

    public void write(String str) throws IOException {
        try {
            this.writer.writeCharacters(str);
        }
        catch (XMLStreamException ex) {
            throw new XMLStreamIOException(ex);
        }
    }

    public void write(int c) throws IOException {
        this.write(new char[]{(char)c});
    }

    public void flush() throws IOException {
    }

    public void close() throws IOException {
    }
}

