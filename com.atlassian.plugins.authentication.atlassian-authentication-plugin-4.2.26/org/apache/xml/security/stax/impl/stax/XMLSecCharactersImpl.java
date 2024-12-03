/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.stax;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.stax.ext.stax.XMLSecCharacters;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl;

public class XMLSecCharactersImpl
extends XMLSecEventBaseImpl
implements XMLSecCharacters {
    private String data;
    private char[] text;
    private final boolean isCData;
    private final boolean isIgnorableWhiteSpace;
    private final boolean isWhiteSpace;

    public XMLSecCharactersImpl(String data, boolean isCData, boolean isIgnorableWhiteSpace, boolean isWhiteSpace, XMLSecStartElement parentXmlSecStartElement) {
        this.data = data;
        this.isCData = isCData;
        this.isIgnorableWhiteSpace = isIgnorableWhiteSpace;
        this.isWhiteSpace = isWhiteSpace;
        this.setParentXMLSecStartElement(parentXmlSecStartElement);
    }

    public XMLSecCharactersImpl(char[] text, boolean isCData, boolean isIgnorableWhiteSpace, boolean isWhiteSpace, XMLSecStartElement parentXmlSecStartElement) {
        this.text = text;
        this.isCData = isCData;
        this.isIgnorableWhiteSpace = isIgnorableWhiteSpace;
        this.isWhiteSpace = isWhiteSpace;
        this.setParentXMLSecStartElement(parentXmlSecStartElement);
    }

    @Override
    public String getData() {
        if (this.data == null) {
            this.data = new String(this.text);
        }
        return this.data;
    }

    @Override
    public char[] getText() {
        if (this.text == null) {
            this.text = this.data.toCharArray();
        }
        return this.text;
    }

    @Override
    public boolean isWhiteSpace() {
        return this.isWhiteSpace;
    }

    @Override
    public boolean isCData() {
        return this.isCData;
    }

    @Override
    public boolean isIgnorableWhiteSpace() {
        return this.isIgnorableWhiteSpace;
    }

    @Override
    public int getEventType() {
        if (this.isCData) {
            return 12;
        }
        return 4;
    }

    @Override
    public boolean isCharacters() {
        return true;
    }

    @Override
    public XMLSecCharacters asCharacters() {
        return this;
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            if (this.isCData) {
                writer.write("<![CDATA[");
                writer.write(this.getText());
                writer.write("]]>");
            } else {
                this.writeEncoded(writer, this.getText());
            }
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    private void writeEncoded(Writer writer, char[] text) throws IOException {
        int length = text.length;
        int idx = 0;
        block5: for (int i = 0; i < length; ++i) {
            char c = text[i];
            switch (c) {
                case '<': {
                    writer.write(text, idx, i - idx);
                    writer.write("&lt;");
                    idx = i + 1;
                    continue block5;
                }
                case '>': {
                    writer.write(text, idx, i - idx);
                    writer.write("&gt;");
                    idx = i + 1;
                    continue block5;
                }
                case '&': {
                    writer.write(text, idx, i - idx);
                    writer.write("&amp;");
                    idx = i + 1;
                }
            }
        }
        writer.write(text, idx, length - idx);
    }
}

