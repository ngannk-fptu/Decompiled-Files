/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding;

import java.io.IOException;
import java.io.Writer;
import javax.xml.namespace.QName;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.Messages;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

public class TextSerializationContext
extends SerializationContext {
    private boolean ignore = false;
    private int depth = 0;

    public TextSerializationContext(Writer writer) {
        super(writer);
        this.startOfDocument = false;
    }

    public TextSerializationContext(Writer writer, MessageContext msgContext) {
        super(writer, msgContext);
        this.startOfDocument = false;
    }

    public void serialize(QName elemQName, Attributes attributes, Object value, QName xmlType, Boolean sendNull, Boolean sendType) throws IOException {
        throw new IOException(Messages.getMessage("notImplemented00", "serialize"));
    }

    public void writeDOMElement(Element el) throws IOException {
        throw new IOException(Messages.getMessage("notImplemented00", "writeDOMElement"));
    }

    public void startElement(QName qName, Attributes attributes) throws IOException {
        ++this.depth;
        if (this.depth == 2) {
            this.ignore = true;
        }
    }

    public void endElement() throws IOException {
        --this.depth;
        this.ignore = true;
    }

    public void writeChars(char[] p1, int p2, int p3) throws IOException {
        if (!this.ignore) {
            super.writeChars(p1, p2, p3);
        }
    }

    public void writeString(String string) throws IOException {
        if (!this.ignore) {
            super.writeString(string);
        }
    }
}

