/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax.util;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

public class FOMXmlVersionReader
extends PushbackReader {
    private String version = null;

    public FOMXmlVersionReader(Reader in) {
        super(in, 200);
        try {
            this.version = this.detectVersion();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public String getVersion() {
        return this.version;
    }

    private String detectVersion() throws IOException {
        String version = "1.0";
        try {
            char[] p = new char[200];
            int r = this.read(p);
            XMLStreamReader xmlreader = XMLInputFactory.newInstance().createXMLStreamReader(new CharArrayReader(p));
            String v = xmlreader.getVersion();
            if (v != null) {
                version = v;
            }
            this.unread(p, 0, r);
        }
        catch (Exception exception) {
            // empty catch block
        }
        return version;
    }
}

