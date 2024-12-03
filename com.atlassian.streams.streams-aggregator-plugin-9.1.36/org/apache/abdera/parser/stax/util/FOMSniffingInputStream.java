/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import org.apache.abdera.i18n.text.io.CharsetSniffingInputStream;
import org.apache.abdera.i18n.text.io.PeekAheadInputStream;

public class FOMSniffingInputStream
extends CharsetSniffingInputStream {
    public FOMSniffingInputStream(InputStream in) {
        super(in);
    }

    protected String detectEncoding() throws IOException {
        String charset = super.detectEncoding();
        PeekAheadInputStream pin = this.getInternal();
        try {
            byte[] p = new byte[200];
            pin.peek(p);
            XMLStreamReader xmlreader = XMLInputFactory.newInstance().createXMLStreamReader(new ByteArrayInputStream(p));
            String cs = xmlreader.getCharacterEncodingScheme();
            if (cs != null) {
                charset = cs;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return charset;
    }
}

