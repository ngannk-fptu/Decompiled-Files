/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax.util;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import org.apache.abdera.i18n.text.io.PeekAheadInputStream;

public class FOMXmlVersionInputStream
extends FilterInputStream {
    private String version = null;

    public FOMXmlVersionInputStream(InputStream in) {
        super(new PeekAheadInputStream(in, 4));
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
        PeekAheadInputStream pin = (PeekAheadInputStream)this.in;
        try {
            byte[] p = new byte[200];
            pin.peek(p);
            XMLStreamReader xmlreader = XMLInputFactory.newInstance().createXMLStreamReader(new ByteArrayInputStream(p));
            String v = xmlreader.getVersion();
            if (v != null) {
                version = v;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return version;
    }
}

