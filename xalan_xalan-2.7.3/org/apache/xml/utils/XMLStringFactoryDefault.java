/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.utils;

import org.apache.xml.utils.FastStringBuffer;
import org.apache.xml.utils.XMLString;
import org.apache.xml.utils.XMLStringDefault;
import org.apache.xml.utils.XMLStringFactory;

public class XMLStringFactoryDefault
extends XMLStringFactory {
    private static final XMLStringDefault EMPTY_STR = new XMLStringDefault("");

    @Override
    public XMLString newstr(String string) {
        return new XMLStringDefault(string);
    }

    @Override
    public XMLString newstr(FastStringBuffer fsb, int start, int length) {
        return new XMLStringDefault(fsb.getString(start, length));
    }

    @Override
    public XMLString newstr(char[] string, int start, int length) {
        return new XMLStringDefault(new String(string, start, length));
    }

    @Override
    public XMLString emptystr() {
        return EMPTY_STR;
    }
}

