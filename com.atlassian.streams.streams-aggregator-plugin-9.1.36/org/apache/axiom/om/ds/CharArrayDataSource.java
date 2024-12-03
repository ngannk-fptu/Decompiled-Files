/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.ds;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.ds.OMDataSourceExtBase;
import org.apache.axiom.om.util.StAXUtils;

public class CharArrayDataSource
extends OMDataSourceExtBase {
    char[] chars = null;

    public CharArrayDataSource(char[] chars) {
        this.chars = chars;
    }

    public void serialize(Writer writer, OMOutputFormat format) throws XMLStreamException {
        try {
            writer.write(this.chars);
        }
        catch (UnsupportedEncodingException e) {
            throw new XMLStreamException(e);
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    public XMLStreamReader getReader() throws XMLStreamException {
        CharArrayReader reader = new CharArrayReader(this.chars);
        return StAXUtils.createXMLStreamReader(reader);
    }

    public Object getObject() {
        return this.chars;
    }

    public boolean isDestructiveRead() {
        return false;
    }

    public boolean isDestructiveWrite() {
        return false;
    }

    public byte[] getXMLBytes(String encoding) throws UnsupportedEncodingException {
        String text = new String(this.chars);
        return text.getBytes(encoding);
    }

    public void close() {
        this.chars = null;
    }

    public OMDataSourceExt copy() {
        return new CharArrayDataSource(this.chars);
    }
}

