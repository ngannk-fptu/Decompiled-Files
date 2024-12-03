/*
 * Decompiled with CFR 0.152.
 */
package org.unbescape.xml;

import java.util.Arrays;
import org.unbescape.xml.XmlCodepointValidator;
import org.unbescape.xml.XmlEscapeSymbols;

final class Xml10EscapeSymbolsInitializer {
    static XmlEscapeSymbols initializeXml10(boolean attributes) {
        int c;
        XmlEscapeSymbols.References xml10References = new XmlEscapeSymbols.References();
        xml10References.addReference(34, "&quot;");
        xml10References.addReference(38, "&amp;");
        xml10References.addReference(39, "&apos;");
        xml10References.addReference(60, "&lt;");
        xml10References.addReference(62, "&gt;");
        byte[] escapeLevels = new byte[161];
        Arrays.fill(escapeLevels, (byte)3);
        for (c = 128; c < 161; c = (int)((char)(c + 1))) {
            escapeLevels[c] = 2;
        }
        for (c = 65; c <= 90; c = (int)((char)(c + 1))) {
            escapeLevels[c] = 4;
        }
        for (c = 97; c <= 122; c = (int)((char)(c + 1))) {
            escapeLevels[c] = 4;
        }
        for (c = 48; c <= 57; c = (int)((char)(c + 1))) {
            escapeLevels[c] = 4;
        }
        escapeLevels[39] = 1;
        escapeLevels[34] = 1;
        escapeLevels[60] = 1;
        escapeLevels[62] = 1;
        escapeLevels[38] = 1;
        if (attributes) {
            escapeLevels[9] = 1;
            escapeLevels[10] = 1;
            escapeLevels[13] = 1;
        }
        for (c = 127; c <= 132; c = (int)((char)(c + 1))) {
            escapeLevels[c] = 1;
        }
        for (c = 134; c <= 159; c = (int)((char)(c + 1))) {
            escapeLevels[c] = 1;
        }
        return new XmlEscapeSymbols(xml10References, escapeLevels, new Xml10CodepointValidator());
    }

    private Xml10EscapeSymbolsInitializer() {
    }

    static final class Xml10CodepointValidator
    implements XmlCodepointValidator {
        Xml10CodepointValidator() {
        }

        @Override
        public boolean isValid(int codepoint) {
            if (codepoint < 32) {
                return codepoint == 9 || codepoint == 10 || codepoint == 13;
            }
            if (codepoint <= 55295) {
                return true;
            }
            if (codepoint < 57344) {
                return false;
            }
            if (codepoint <= 65533) {
                return true;
            }
            return codepoint >= 65536;
        }
    }
}

