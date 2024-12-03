/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xmlrpc.XmlRpcException
 *  org.apache.xmlrpc.XmlWriter
 */
package org.apache.xmlrpc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlWriter;

public class SurrogatePairCapableXmlWriter
extends XmlWriter {
    public SurrogatePairCapableXmlWriter(OutputStream out, String encoding) throws UnsupportedEncodingException {
        super(out, encoding);
    }

    private void writeCharacterReference(int c) throws IOException {
        if (1 <= c && c <= 31) {
            this.write("&amp;#");
        } else {
            this.write("&#");
        }
        this.write(String.valueOf(c));
        this.write(';');
    }

    private static final boolean isValidXMLChar(int c) {
        switch (c) {
            case 9: 
            case 10: 
            case 13: {
                return true;
            }
        }
        return 32 <= c && c <= 55295 || 57344 <= c && c <= 65533 || 65536 <= c && c <= 0x10FFFF;
    }

    protected void chardata(String text) throws XmlRpcException, IOException {
        int l = text.length();
        block7: for (int i = 0; i < l; ++i) {
            char c = text.charAt(i);
            switch (c) {
                case '\t': 
                case '\n': {
                    this.write(c);
                    continue block7;
                }
                case '\r': {
                    this.writeCharacterReference(c);
                    continue block7;
                }
                case '<': {
                    this.write("&lt;");
                    continue block7;
                }
                case '>': {
                    this.write("&gt;");
                    continue block7;
                }
                case '&': {
                    this.write("&amp;");
                    continue block7;
                }
                default: {
                    if (c > '\u007f' || !SurrogatePairCapableXmlWriter.isValidXMLChar(c)) {
                        if (Character.isLowSurrogate(c)) continue block7;
                        this.writeCharacterReference(text.codePointAt(i));
                        continue block7;
                    }
                    this.write(c);
                }
            }
        }
    }
}

