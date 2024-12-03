/*
 * Decompiled with CFR 0.152.
 */
package org.jvnet.fastinfoset.sax.helpers;

import org.jvnet.fastinfoset.sax.EncodingAlgorithmContentHandler;
import org.jvnet.fastinfoset.sax.PrimitiveTypeContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class FastInfosetDefaultHandler
extends DefaultHandler
implements LexicalHandler,
EncodingAlgorithmContentHandler,
PrimitiveTypeContentHandler {
    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
    }

    @Override
    public void startCDATA() throws SAXException {
    }

    @Override
    public void endCDATA() throws SAXException {
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
    }

    @Override
    public void endDTD() throws SAXException {
    }

    @Override
    public void startEntity(String name) throws SAXException {
    }

    @Override
    public void endEntity(String name) throws SAXException {
    }

    @Override
    public void octets(String URI2, int algorithm, byte[] b, int start, int length) throws SAXException {
    }

    @Override
    public void object(String URI2, int algorithm, Object o) throws SAXException {
    }

    @Override
    public void booleans(boolean[] b, int start, int length) throws SAXException {
    }

    @Override
    public void bytes(byte[] b, int start, int length) throws SAXException {
    }

    @Override
    public void shorts(short[] s, int start, int length) throws SAXException {
    }

    @Override
    public void ints(int[] i, int start, int length) throws SAXException {
    }

    @Override
    public void longs(long[] l, int start, int length) throws SAXException {
    }

    @Override
    public void floats(float[] f, int start, int length) throws SAXException {
    }

    @Override
    public void doubles(double[] d, int start, int length) throws SAXException {
    }

    @Override
    public void uuids(long[] msblsb, int start, int length) throws SAXException {
    }
}

