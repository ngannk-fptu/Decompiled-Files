/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.DecoderException
 *  org.apache.commons.codec.EncoderException
 *  org.apache.commons.codec.binary.Base64
 */
package org.apache.xmlrpc.applet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.xmlrpc.applet.XmlRpcException;
import org.xml.sax.AttributeList;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import uk.co.wilson.xml.MinML;

class XmlRpcSupport
extends HandlerBase {
    URL url;
    String methodName;
    boolean fault = false;
    Object result = null;
    Base64 base64 = new Base64();
    Stack values;
    Value currentValue;
    boolean readCdata;
    static final DateFormat format = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");
    StringBuffer cdata = new StringBuffer();
    static final int STRING = 0;
    static final int INTEGER = 1;
    static final int BOOLEAN = 2;
    static final int DOUBLE = 3;
    static final int DATE = 4;
    static final int BASE64 = 5;
    static final int STRUCT = 6;
    static final int ARRAY = 7;
    public static boolean debug = false;
    static final String[] types = new String[]{"String", "Integer", "Boolean", "Double", "Date", "Base64", "Struct", "Array"};
    static /* synthetic */ Class class$org$apache$commons$codec$binary$Base64;

    public XmlRpcSupport(URL url) {
        this.url = url;
    }

    public static void setDebug(boolean val) {
        debug = val;
    }

    synchronized void parse(InputStream is) throws Exception {
        this.values = new Stack();
        long now = System.currentTimeMillis();
        MinML parser = new MinML();
        parser.setDocumentHandler(this);
        parser.setErrorHandler(this);
        parser.parse(new InputSource(is));
        if (debug) {
            System.out.println("Spent " + (System.currentTimeMillis() - now) + " parsing");
        }
    }

    void writeObject(Object what, XmlWriter writer) throws IOException {
        writer.startElement("value");
        if (what instanceof String) {
            writer.write(what.toString());
        } else if (what instanceof Integer) {
            writer.startElement("int");
            writer.write(what.toString());
            writer.endElement("int");
        } else if (what instanceof Boolean) {
            writer.startElement("boolean");
            writer.write((Boolean)what != false ? "1" : "0");
            writer.endElement("boolean");
        } else if (what instanceof Double) {
            writer.startElement("double");
            writer.write(what.toString());
            writer.endElement("double");
        } else if (what instanceof Date) {
            writer.startElement("dateTime.iso8601");
            Date d = (Date)what;
            writer.write(format.format(d));
            writer.endElement("dateTime.iso8601");
        } else if (what instanceof byte[]) {
            writer.startElement("base64");
            try {
                writer.write((byte[])this.base64.encode(what));
            }
            catch (EncoderException e) {
                throw new RuntimeException("Possibly incompatible version of '" + (class$org$apache$commons$codec$binary$Base64 == null ? (class$org$apache$commons$codec$binary$Base64 = XmlRpcSupport.class$("org.apache.commons.codec.binary.Base64")) : class$org$apache$commons$codec$binary$Base64).getName() + "' used: " + (Object)((Object)e));
            }
            writer.endElement("base64");
        } else if (what instanceof Vector) {
            writer.startElement("array");
            writer.startElement("data");
            Vector v = (Vector)what;
            int l2 = v.size();
            for (int i2 = 0; i2 < l2; ++i2) {
                this.writeObject(v.elementAt(i2), writer);
            }
            writer.endElement("data");
            writer.endElement("array");
        } else if (what instanceof Hashtable) {
            writer.startElement("struct");
            Hashtable h = (Hashtable)what;
            Enumeration e = h.keys();
            while (e.hasMoreElements()) {
                String nextkey = (String)e.nextElement();
                Object nextval = h.get(nextkey);
                writer.startElement("member");
                writer.startElement("name");
                writer.write(nextkey);
                writer.endElement("name");
                this.writeObject(nextval, writer);
                writer.endElement("member");
            }
            writer.endElement("struct");
        } else {
            String unsupportedType = what == null ? "null" : what.getClass().toString();
            throw new IOException("unsupported Java type: " + unsupportedType);
        }
        writer.endElement("value");
    }

    public Object execute(String method, Vector arguments) throws XmlRpcException, IOException {
        this.fault = false;
        long now = System.currentTimeMillis();
        try {
            StringBuffer strbuf = new StringBuffer();
            XmlWriter writer = new XmlWriter(strbuf);
            this.writeRequest(writer, method, arguments);
            byte[] request = strbuf.toString().getBytes();
            URLConnection con = this.url.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setAllowUserInteraction(false);
            con.setRequestProperty("Content-Length", Integer.toString(request.length));
            con.setRequestProperty("Content-Type", "text/xml");
            OutputStream out = con.getOutputStream();
            out.write(request);
            out.flush();
            InputStream in = con.getInputStream();
            this.parse(in);
            System.out.println("result = " + this.result);
        }
        catch (Exception x) {
            x.printStackTrace();
            throw new IOException(x.getMessage());
        }
        if (this.fault) {
            XmlRpcException exception = null;
            try {
                Hashtable f = (Hashtable)this.result;
                String faultString = (String)f.get("faultString");
                int faultCode = Integer.parseInt(f.get("faultCode").toString());
                exception = new XmlRpcException(faultCode, faultString.trim());
            }
            catch (Exception x) {
                throw new XmlRpcException(0, "Invalid fault response");
            }
            throw exception;
        }
        System.out.println("Spent " + (System.currentTimeMillis() - now) + " in request");
        return this.result;
    }

    void objectParsed(Object what) {
        this.result = what;
    }

    void writeRequest(XmlWriter writer, String method, Vector params) throws IOException {
        writer.startElement("methodCall");
        writer.startElement("methodName");
        writer.write(method);
        writer.endElement("methodName");
        writer.startElement("params");
        int l = params.size();
        for (int i = 0; i < l; ++i) {
            writer.startElement("param");
            this.writeObject(params.elementAt(i), writer);
            writer.endElement("param");
        }
        writer.endElement("params");
        writer.endElement("methodCall");
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        if (!this.readCdata) {
            return;
        }
        this.cdata.append(ch, start, length);
    }

    public void endElement(String name) throws SAXException {
        int depth;
        if (debug) {
            System.err.println("endElement: " + name);
        }
        if (this.currentValue != null && this.readCdata) {
            this.currentValue.characterData(this.cdata.toString());
            this.cdata.setLength(0);
            this.readCdata = false;
        }
        if ("value".equals(name) && ((depth = this.values.size()) < 2 || this.values.elementAt(depth - 2).hashCode() != 6)) {
            Value v = this.currentValue;
            this.values.pop();
            if (depth < 2) {
                this.objectParsed(v.value);
                this.currentValue = null;
            } else {
                this.currentValue = (Value)this.values.peek();
                this.currentValue.endElement(v);
            }
        }
        if ("member".equals(name)) {
            Value v = this.currentValue;
            this.values.pop();
            this.currentValue = (Value)this.values.peek();
            this.currentValue.endElement(v);
        } else if ("methodName".equals(name)) {
            this.methodName = this.cdata.toString();
            this.cdata.setLength(0);
            this.readCdata = false;
        }
    }

    public void startElement(String name, AttributeList atts) throws SAXException {
        if (debug) {
            System.err.println("startElement: " + name);
        }
        if ("value".equals(name)) {
            Value v = new Value();
            this.values.push(v);
            this.currentValue = v;
            this.cdata.setLength(0);
            this.readCdata = true;
        } else if ("methodName".equals(name)) {
            this.cdata.setLength(0);
            this.readCdata = true;
        } else if ("name".equals(name)) {
            this.cdata.setLength(0);
            this.readCdata = true;
        } else if ("string".equals(name)) {
            this.cdata.setLength(0);
            this.readCdata = true;
        } else if ("i4".equals(name) || "int".equals(name)) {
            this.currentValue.setType(1);
            this.cdata.setLength(0);
            this.readCdata = true;
        } else if ("boolean".equals(name)) {
            this.currentValue.setType(2);
            this.cdata.setLength(0);
            this.readCdata = true;
        } else if ("double".equals(name)) {
            this.currentValue.setType(3);
            this.cdata.setLength(0);
            this.readCdata = true;
        } else if ("dateTime.iso8601".equals(name)) {
            this.currentValue.setType(4);
            this.cdata.setLength(0);
            this.readCdata = true;
        } else if ("base64".equals(name)) {
            this.currentValue.setType(5);
            this.cdata.setLength(0);
            this.readCdata = true;
        } else if ("struct".equals(name)) {
            this.currentValue.setType(6);
        } else if ("array".equals(name)) {
            this.currentValue.setType(7);
        }
    }

    public void error(SAXParseException e) throws SAXException {
        System.err.println("Error parsing XML: " + e);
    }

    public void fatalError(SAXParseException e) throws SAXException {
        System.err.println("Fatal error parsing XML: " + e);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    class XmlWriter {
        StringBuffer buf;
        String enc;

        public XmlWriter(StringBuffer buf) {
            this.buf = buf;
            buf.append("<?xml version=\"1.0\"?>");
        }

        public void startElement(String elem) {
            this.buf.append("<");
            this.buf.append(elem);
            this.buf.append(">");
        }

        public void endElement(String elem) {
            this.buf.append("</");
            this.buf.append(elem);
            this.buf.append(">");
        }

        public void emptyElement(String elem) {
            this.buf.append("<");
            this.buf.append(elem);
            this.buf.append("/>");
        }

        public void chardata(String text) {
            int l = text.length();
            block5: for (int i = 0; i < l; ++i) {
                char c = text.charAt(i);
                switch (c) {
                    case '<': {
                        this.buf.append("&lt;");
                        continue block5;
                    }
                    case '>': {
                        this.buf.append("&gt;");
                        continue block5;
                    }
                    case '&': {
                        this.buf.append("&amp;");
                        continue block5;
                    }
                    default: {
                        this.buf.append(c);
                    }
                }
            }
        }

        public void write(byte[] text) {
            for (int i = 0; i < text.length; ++i) {
                this.buf.append((char)text[i]);
            }
        }

        public void write(char[] text) {
            this.buf.append(text);
        }

        public void write(String text) {
            this.buf.append(text);
        }

        public String toString() {
            return this.buf.toString();
        }

        public byte[] getBytes() throws UnsupportedEncodingException {
            return this.buf.toString().getBytes();
        }
    }

    class Value {
        int type = 0;
        Object value;
        String nextMemberName;
        Hashtable struct;
        Vector array;

        public void endElement(Value child) {
            if (this.type == 7) {
                this.array.addElement(child.value);
            } else if (this.type == 6) {
                this.struct.put(this.nextMemberName, child.value);
            }
        }

        public void setType(int type) {
            this.type = type;
            if (type == 7) {
                this.value = this.array = new Vector();
            }
            if (type == 6) {
                this.value = this.struct = new Hashtable();
            }
        }

        public void characterData(String cdata) {
            switch (this.type) {
                case 1: {
                    this.value = new Integer(cdata.trim());
                    break;
                }
                case 2: {
                    this.value = new Boolean("1".equals(cdata.trim()));
                    break;
                }
                case 3: {
                    this.value = new Double(cdata.trim());
                    break;
                }
                case 4: {
                    try {
                        this.value = format.parse(cdata.trim());
                        break;
                    }
                    catch (ParseException p) {
                        throw new RuntimeException(p.getMessage());
                    }
                }
                case 5: {
                    try {
                        this.value = XmlRpcSupport.this.base64.decode((Object)cdata.getBytes());
                    }
                    catch (DecoderException e) {
                        this.value = cdata;
                    }
                    break;
                }
                case 0: {
                    this.value = cdata;
                    break;
                }
                case 6: {
                    this.nextMemberName = cdata;
                }
            }
        }

        public int hashCode() {
            return this.type;
        }

        public String toString() {
            return types[this.type] + " element " + this.value;
        }
    }
}

