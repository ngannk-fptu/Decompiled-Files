/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.EncoderException
 *  org.apache.commons.codec.binary.Base64
 */
package org.apache.xmlrpc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.xmlrpc.DefaultTypeDecoder;
import org.apache.xmlrpc.TypeDecoder;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.util.DateTool;

class XmlWriter
extends OutputStreamWriter {
    protected static final String PROLOG_START = "<?xml version=\"1.0";
    protected static final String PROLOG_END = "\"?>";
    protected static final String CLOSING_TAG_START = "</";
    protected static final String SINGLE_TAG_END = "/>";
    protected static final String LESS_THAN_ENTITY = "&lt;";
    protected static final String GREATER_THAN_ENTITY = "&gt;";
    protected static final String AMPERSAND_ENTITY = "&amp;";
    private static final char[] PROLOG = new char["<?xml version=\"1.0".length() + "\"?>".length()];
    static final String ISO8859_1 = "ISO8859_1";
    static final String UTF8 = "UTF8";
    static final String UTF16 = "UTF-16";
    protected static final Base64 base64Codec;
    protected static TypeDecoder typeDecoder;
    private static Properties encodings;
    private static DateTool dateTool;
    boolean hasWrittenProlog = false;

    public XmlWriter(OutputStream out, String enc) throws UnsupportedEncodingException {
        super(out, XmlWriter.forceUnicode(enc));
    }

    private static String forceUnicode(String encoding) {
        if (encoding == null || !encoding.toUpperCase().startsWith("UTF")) {
            encoding = UTF8;
        }
        return encoding;
    }

    protected static String canonicalizeEncoding(String javaEncoding) {
        return encodings.getProperty(javaEncoding, javaEncoding);
    }

    public void write(char[] cbuf, int off, int len) throws IOException {
        if (!this.hasWrittenProlog) {
            super.write(PROLOG, 0, PROLOG.length);
            this.hasWrittenProlog = true;
        }
        super.write(cbuf, off, len);
    }

    public void write(char c) throws IOException {
        if (!this.hasWrittenProlog) {
            super.write(PROLOG, 0, PROLOG.length);
            this.hasWrittenProlog = true;
        }
        super.write(c);
    }

    public void write(String str, int off, int len) throws IOException {
        if (!this.hasWrittenProlog) {
            super.write(PROLOG, 0, PROLOG.length);
            this.hasWrittenProlog = true;
        }
        super.write(str, off, len);
    }

    public void writeObject(Object obj) throws XmlRpcException, IOException {
        this.startElement("value");
        if (obj == null) {
            throw new XmlRpcException(0, "null values not supported by XML-RPC");
        }
        if (obj instanceof String) {
            this.chardata(obj.toString());
        } else if (typeDecoder.isXmlRpcI4(obj)) {
            this.startElement("int");
            this.write(obj.toString());
            this.endElement("int");
        } else if (obj instanceof Boolean) {
            this.startElement("boolean");
            this.write((Boolean)obj != false ? "1" : "0");
            this.endElement("boolean");
        } else if (typeDecoder.isXmlRpcDouble(obj)) {
            this.startElement("double");
            this.write(obj.toString());
            this.endElement("double");
        } else if (obj instanceof Date) {
            this.startElement("dateTime.iso8601");
            Date d = (Date)obj;
            this.write(dateTool.format(d));
            this.endElement("dateTime.iso8601");
        } else if (obj instanceof byte[]) {
            this.startElement("base64");
            try {
                this.write((byte[])base64Codec.encode(obj));
            }
            catch (EncoderException e) {
                throw new XmlRpcException(0, "Unable to Base 64 encode byte array", e);
            }
            this.endElement("base64");
        } else if (obj instanceof Object[]) {
            this.startElement("array");
            this.startElement("data");
            Object[] array = (Object[])obj;
            for (int i = 0; i < array.length; ++i) {
                this.writeObject(array[i]);
            }
            this.endElement("data");
            this.endElement("array");
        } else if (obj instanceof Vector) {
            this.startElement("array");
            this.startElement("data");
            Vector array = (Vector)obj;
            int size = array.size();
            for (int i = 0; i < size; ++i) {
                this.writeObject(array.elementAt(i));
            }
            this.endElement("data");
            this.endElement("array");
        } else if (obj instanceof Hashtable) {
            this.startElement("struct");
            Hashtable struct = (Hashtable)obj;
            Enumeration e = struct.keys();
            while (e.hasMoreElements()) {
                String key = (String)e.nextElement();
                Object value = struct.get(key);
                this.startElement("member");
                this.startElement("name");
                this.chardata(key);
                this.endElement("name");
                this.writeObject(value);
                this.endElement("member");
            }
            this.endElement("struct");
        } else {
            throw new XmlRpcException(0, "Unsupported Java type: " + obj.getClass(), null);
        }
        this.endElement("value");
    }

    protected void write(byte[] byteData) throws IOException {
        for (int i = 0; i < byteData.length; ++i) {
            this.write(byteData[i]);
        }
    }

    private void writeCharacterReference(char c) throws IOException {
        this.write("&#");
        this.write(String.valueOf((int)c));
        this.write(';');
    }

    protected void startElement(String elem) throws IOException {
        this.write('<');
        this.write(elem);
        this.write('>');
    }

    protected void endElement(String elem) throws IOException {
        this.write(CLOSING_TAG_START);
        this.write(elem);
        this.write('>');
    }

    protected void emptyElement(String elem) throws IOException {
        this.write('<');
        this.write(elem);
        this.write(SINGLE_TAG_END);
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
                    this.write(LESS_THAN_ENTITY);
                    continue block7;
                }
                case '>': {
                    this.write(GREATER_THAN_ENTITY);
                    continue block7;
                }
                case '&': {
                    this.write(AMPERSAND_ENTITY);
                    continue block7;
                }
                default: {
                    if (c > '\u007f' || !XmlWriter.isValidXMLChar(c)) {
                        this.writeCharacterReference(c);
                        continue block7;
                    }
                    this.write(c);
                }
            }
        }
    }

    private static final boolean isValidXMLChar(char c) {
        switch (c) {
            case '\t': 
            case '\n': 
            case '\r': {
                return true;
            }
        }
        return ' ' < c && c <= '\ud7ff' || '\ue000' < c && c <= '\ufffd' || '\u10000' < c && c <= '\u10ffff';
    }

    protected static void setTypeDecoder(TypeDecoder newTypeDecoder) {
        typeDecoder = newTypeDecoder;
    }

    static {
        int len = PROLOG_START.length();
        PROLOG_START.getChars(0, len, PROLOG, 0);
        PROLOG_END.getChars(0, PROLOG_END.length(), PROLOG, len);
        base64Codec = new Base64();
        encodings = new Properties();
        ((Hashtable)encodings).put(UTF8, "UTF-8");
        ((Hashtable)encodings).put(ISO8859_1, "ISO-8859-1");
        typeDecoder = new DefaultTypeDecoder();
        dateTool = new DateTool();
    }
}

