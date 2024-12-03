/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.components.encoding;

import org.apache.axis.components.encoding.XMLEncoder;
import org.apache.axis.utils.Messages;

public abstract class AbstractXMLEncoder
implements XMLEncoder {
    protected static final String AMP = "&amp;";
    protected static final String QUOTE = "&quot;";
    protected static final String LESS = "&lt;";
    protected static final String GREATER = "&gt;";
    protected static final String LF = "\n";
    protected static final String CR = "\r";
    protected static final String TAB = "\t";

    public abstract String getEncoding();

    public String encode(String xmlString) {
        if (xmlString == null) {
            return "";
        }
        char[] characters = xmlString.toCharArray();
        StringBuffer out = null;
        block9: for (int i = 0; i < characters.length; ++i) {
            char character = characters[i];
            switch (character) {
                case '&': {
                    if (out == null) {
                        out = this.getInitialByteArray(xmlString, i);
                    }
                    out.append(AMP);
                    continue block9;
                }
                case '\"': {
                    if (out == null) {
                        out = this.getInitialByteArray(xmlString, i);
                    }
                    out.append(QUOTE);
                    continue block9;
                }
                case '<': {
                    if (out == null) {
                        out = this.getInitialByteArray(xmlString, i);
                    }
                    out.append(LESS);
                    continue block9;
                }
                case '>': {
                    if (out == null) {
                        out = this.getInitialByteArray(xmlString, i);
                    }
                    out.append(GREATER);
                    continue block9;
                }
                case '\n': {
                    if (out == null) {
                        out = this.getInitialByteArray(xmlString, i);
                    }
                    out.append(LF);
                    continue block9;
                }
                case '\r': {
                    if (out == null) {
                        out = this.getInitialByteArray(xmlString, i);
                    }
                    out.append(CR);
                    continue block9;
                }
                case '\t': {
                    if (out == null) {
                        out = this.getInitialByteArray(xmlString, i);
                    }
                    out.append(TAB);
                    continue block9;
                }
                default: {
                    if (character < ' ') {
                        throw new IllegalArgumentException(Messages.getMessage("invalidXmlCharacter00", Integer.toHexString(character), xmlString));
                    }
                    if (out == null) continue block9;
                    out.append(character);
                }
            }
        }
        if (out == null) {
            return xmlString;
        }
        return out.toString();
    }

    protected StringBuffer getInitialByteArray(String aXmlString, int pos) {
        return new StringBuffer(aXmlString.substring(0, pos));
    }
}

