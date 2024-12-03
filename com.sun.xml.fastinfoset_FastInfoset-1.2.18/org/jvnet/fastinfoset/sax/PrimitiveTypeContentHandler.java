/*
 * Decompiled with CFR 0.152.
 */
package org.jvnet.fastinfoset.sax;

import org.xml.sax.SAXException;

public interface PrimitiveTypeContentHandler {
    public void booleans(boolean[] var1, int var2, int var3) throws SAXException;

    public void bytes(byte[] var1, int var2, int var3) throws SAXException;

    public void shorts(short[] var1, int var2, int var3) throws SAXException;

    public void ints(int[] var1, int var2, int var3) throws SAXException;

    public void longs(long[] var1, int var2, int var3) throws SAXException;

    public void floats(float[] var1, int var2, int var3) throws SAXException;

    public void doubles(double[] var1, int var2, int var3) throws SAXException;

    public void uuids(long[] var1, int var2, int var3) throws SAXException;
}

