/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.components.encoding;

import java.io.IOException;
import java.io.Writer;
import org.apache.axis.components.encoding.AbstractXMLEncoder;
import org.apache.axis.i18n.Messages;

class UTF16Encoder
extends AbstractXMLEncoder {
    UTF16Encoder() {
    }

    public String getEncoding() {
        return "UTF-16";
    }

    public void writeEncoded(Writer writer, String xmlString) throws IOException {
        if (xmlString == null) {
            return;
        }
        int length = xmlString.length();
        block9: for (int i = 0; i < length; ++i) {
            char character = xmlString.charAt(i);
            switch (character) {
                case '&': {
                    writer.write("&amp;");
                    continue block9;
                }
                case '\"': {
                    writer.write("&quot;");
                    continue block9;
                }
                case '<': {
                    writer.write("&lt;");
                    continue block9;
                }
                case '>': {
                    writer.write("&gt;");
                    continue block9;
                }
                case '\n': {
                    writer.write("\n");
                    continue block9;
                }
                case '\r': {
                    writer.write("\r");
                    continue block9;
                }
                case '\t': {
                    writer.write("\t");
                    continue block9;
                }
                default: {
                    if (character < ' ') {
                        throw new IllegalArgumentException(Messages.getMessage("invalidXmlCharacter00", Integer.toHexString(character), xmlString));
                    }
                    if (character > '\uffff') {
                        writer.write(55232 + (character >> 10));
                        writer.write(0xDC00 | character & 0x3FF);
                        continue block9;
                    }
                    writer.write(character);
                }
            }
        }
    }
}

