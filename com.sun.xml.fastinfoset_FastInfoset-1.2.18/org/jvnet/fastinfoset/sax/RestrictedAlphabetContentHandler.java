/*
 * Decompiled with CFR 0.152.
 */
package org.jvnet.fastinfoset.sax;

import org.xml.sax.SAXException;

public interface RestrictedAlphabetContentHandler {
    public void numericCharacters(char[] var1, int var2, int var3) throws SAXException;

    public void dateTimeCharacters(char[] var1, int var2, int var3) throws SAXException;

    public void alphabetCharacters(String var1, char[] var2, int var3, int var4) throws SAXException;
}

