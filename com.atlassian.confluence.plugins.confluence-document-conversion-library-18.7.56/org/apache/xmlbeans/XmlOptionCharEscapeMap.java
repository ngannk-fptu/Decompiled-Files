/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import java.util.HashMap;
import java.util.Map;
import org.apache.xmlbeans.XmlException;

public class XmlOptionCharEscapeMap {
    public static final int PREDEF_ENTITY = 0;
    public static final int DECIMAL = 1;
    public static final int HEXADECIMAL = 2;
    private final Map<Character, String> _charMap = new HashMap<Character, String>();
    private static final Map<Character, String> _predefEntities = new HashMap<Character, String>();

    public boolean containsChar(char ch) {
        return this._charMap.containsKey(Character.valueOf(ch));
    }

    public void addMapping(char ch, int mode) throws XmlException {
        Character theChar = Character.valueOf(ch);
        switch (mode) {
            case 0: {
                String replString = _predefEntities.get(theChar);
                if (replString == null) {
                    throw new XmlException("XmlOptionCharEscapeMap.addMapping(): the PREDEF_ENTITY mode can only be used for the following characters: <, >, &, \" and '");
                }
                this._charMap.put(theChar, replString);
                break;
            }
            case 1: {
                this._charMap.put(theChar, "&#" + ch + ";");
                break;
            }
            case 2: {
                String hexCharPoint = Integer.toHexString(ch);
                this._charMap.put(theChar, "&#x" + hexCharPoint + ";");
                break;
            }
            default: {
                throw new XmlException("XmlOptionCharEscapeMap.addMapping(): mode must be PREDEF_ENTITY, DECIMAL or HEXADECIMAL");
            }
        }
    }

    public void addMappings(char ch1, char ch2, int mode) throws XmlException {
        if (ch1 > ch2) {
            throw new XmlException("XmlOptionCharEscapeMap.addMappings(): ch1 must be <= ch2");
        }
        for (char c = ch1; c <= ch2; c = (char)(c + '\u0001')) {
            this.addMapping(c, mode);
        }
    }

    public String getEscapedString(char ch) {
        return this._charMap.get(Character.valueOf(ch));
    }

    static {
        _predefEntities.put(Character.valueOf('<'), "&lt;");
        _predefEntities.put(Character.valueOf('>'), "&gt;");
        _predefEntities.put(Character.valueOf('&'), "&amp;");
        _predefEntities.put(Character.valueOf('\''), "&apos;");
        _predefEntities.put(Character.valueOf('\"'), "&quot;");
    }
}

