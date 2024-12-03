/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;
import org.apache.xmlrpc.DefaultTypeFactory;
import org.apache.xmlrpc.TypeFactory;
import org.apache.xmlrpc.XmlWriter;
import org.xml.sax.AttributeList;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import uk.co.wilson.xml.MinML;

public abstract class XmlRpc
extends HandlerBase {
    public static final String version = "Apache XML-RPC 2.0";
    private static final String DEFAULT_PARSER = MinML.class.getName();
    private static int maxThreads = 100;
    String methodName;
    private static Class parserClass;
    private static Hashtable saxDrivers;
    Stack values;
    Value currentValue;
    StringBuffer cdata;
    boolean readCdata;
    static final int STRING = 0;
    static final int INTEGER = 1;
    static final int BOOLEAN = 2;
    static final int DOUBLE = 3;
    static final int DATE = 4;
    static final int BASE64 = 5;
    static final int STRUCT = 6;
    static final int ARRAY = 7;
    int errorLevel;
    String errorMsg;
    static final int NONE = 0;
    static final int RECOVERABLE = 1;
    static final int FATAL = 2;
    static boolean keepalive;
    public static boolean debug;
    static final String[] types;
    static String encoding;
    static String defaultInputEncoding;
    private TypeFactory typeFactory;
    private String inputEncoding;

    protected XmlRpc() {
        String typeFactoryName;
        block2: {
            typeFactoryName = null;
            try {
                typeFactoryName = System.getProperty(TypeFactory.class.getName());
            }
            catch (SecurityException e) {
                if (!debug) break block2;
                System.out.println("Unable to determine the value of the system property '" + TypeFactory.class.getName() + "': " + e.getMessage());
            }
        }
        this.typeFactory = this.createTypeFactory(typeFactoryName);
        this.inputEncoding = defaultInputEncoding;
    }

    protected XmlRpc(String typeFactoryName) {
        this.typeFactory = this.createTypeFactory(typeFactoryName);
    }

    private TypeFactory createTypeFactory(String className) {
        Class<?> c = null;
        if (className != null && className.length() > 0) {
            try {
                c = Class.forName(className);
            }
            catch (ClassNotFoundException e) {
                System.err.println("Error loading TypeFactory '' " + c.getName() + "': Using the default instead: " + e.getMessage());
            }
        }
        if (c == null || DefaultTypeFactory.class.equals(c)) {
            return new DefaultTypeFactory();
        }
        try {
            return (TypeFactory)c.newInstance();
        }
        catch (Exception e) {
            System.err.println("Unable to create configured TypeFactory '" + c.getName() + "': " + e.getMessage() + ": Falling back to default");
            if (debug) {
                e.printStackTrace();
            }
            return new DefaultTypeFactory();
        }
    }

    public static void setDriver(String driver) throws ClassNotFoundException {
        String parserClassName = null;
        try {
            parserClassName = (String)saxDrivers.get(driver);
            if (parserClassName == null) {
                parserClassName = driver;
            }
            parserClass = Class.forName(parserClassName);
        }
        catch (ClassNotFoundException x) {
            throw new ClassNotFoundException("SAX driver not found: " + parserClassName);
        }
    }

    public static void setDriver(Class driver) {
        parserClass = driver;
    }

    public static void setEncoding(String enc) {
        encoding = enc;
    }

    public String getEncoding() {
        return XmlWriter.canonicalizeEncoding(encoding);
    }

    public static void setDefaultInputEncoding(String enc) {
        defaultInputEncoding = enc;
    }

    public static String getDefaultInputEncoding() {
        return defaultInputEncoding;
    }

    public void setInputEncoding(String enc) {
        this.inputEncoding = enc;
    }

    public String getInputEncoding() {
        return this.inputEncoding;
    }

    public static int getMaxThreads() {
        return maxThreads;
    }

    public static void setMaxThreads(int maxThreads) {
        XmlRpc.maxThreads = maxThreads;
    }

    public static void setDebug(boolean val) {
        debug = val;
    }

    public static void setKeepAlive(boolean val) {
        keepalive = val;
    }

    public static boolean getKeepAlive() {
        return keepalive;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    synchronized void parse(InputStream is) throws Exception {
        this.errorLevel = 0;
        this.errorMsg = null;
        this.values = new Stack();
        this.cdata = new StringBuffer();
        this.readCdata = false;
        this.currentValue = null;
        long now = System.currentTimeMillis();
        if (parserClass == null) {
            String driver;
            try {
                driver = System.getProperty("sax.driver", DEFAULT_PARSER);
            }
            catch (SecurityException e) {
                driver = DEFAULT_PARSER;
            }
            XmlRpc.setDriver(driver);
        }
        Parser parser = null;
        try {
            parser = (Parser)parserClass.newInstance();
        }
        catch (NoSuchMethodError nsm) {
            throw new Exception("Can't create Parser: " + parserClass);
        }
        parser.setDocumentHandler(this);
        parser.setErrorHandler(this);
        if (debug) {
            System.out.println("Beginning parsing XML input stream");
        }
        try {
            if (this.inputEncoding == null) {
                parser.parse(new InputSource(is));
            } else {
                parser.parse(new InputSource(new InputStreamReader(is, this.inputEncoding)));
            }
        }
        finally {
            if (this.cdata.length() > 512) {
                this.cdata = null;
            }
        }
        if (debug) {
            System.out.println("Spent " + (System.currentTimeMillis() - now) + " millis parsing");
        }
    }

    protected abstract void objectParsed(Object var1);

    public void characters(char[] ch, int start, int length) throws SAXException {
        if (this.readCdata) {
            this.cdata.append(ch, start, length);
        }
    }

    public void endElement(String name) throws SAXException {
        int depth;
        if (debug) {
            System.out.println("endElement: " + name);
        }
        if (this.currentValue != null && this.readCdata) {
            this.currentValue.characterData(this.cdata.toString());
            this.cdata = new StringBuffer();
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
            this.cdata = new StringBuffer();
            this.readCdata = false;
        }
    }

    public void startElement(String name, AttributeList atts) throws SAXException {
        if (debug) {
            System.out.println("startElement: " + name);
        }
        if ("value".equals(name)) {
            Value v = new Value();
            this.values.push(v);
            this.currentValue = v;
            this.cdata = new StringBuffer();
            this.readCdata = true;
        } else if ("methodName".equals(name)) {
            this.cdata = new StringBuffer();
            this.readCdata = true;
        } else if ("name".equals(name)) {
            this.cdata = new StringBuffer();
            this.readCdata = true;
        } else if ("string".equals(name)) {
            this.cdata = new StringBuffer();
            this.readCdata = true;
        } else if ("i4".equals(name) || "int".equals(name)) {
            this.currentValue.setType(1);
            this.cdata = new StringBuffer();
            this.readCdata = true;
        } else if ("boolean".equals(name)) {
            this.currentValue.setType(2);
            this.cdata = new StringBuffer();
            this.readCdata = true;
        } else if ("double".equals(name)) {
            this.currentValue.setType(3);
            this.cdata = new StringBuffer();
            this.readCdata = true;
        } else if ("dateTime.iso8601".equals(name)) {
            this.currentValue.setType(4);
            this.cdata = new StringBuffer();
            this.readCdata = true;
        } else if ("base64".equals(name)) {
            this.currentValue.setType(5);
            this.cdata = new StringBuffer();
            this.readCdata = true;
        } else if ("struct".equals(name)) {
            this.currentValue.setType(6);
        } else if ("array".equals(name)) {
            this.currentValue.setType(7);
        }
    }

    public void error(SAXParseException e) throws SAXException {
        System.err.println("Error parsing XML: " + e);
        this.errorLevel = 1;
        this.errorMsg = e.toString();
    }

    public void fatalError(SAXParseException e) throws SAXException {
        System.err.println("Fatal error parsing XML: " + e);
        this.errorLevel = 2;
        this.errorMsg = e.toString();
    }

    static {
        saxDrivers = new Hashtable(8);
        saxDrivers.put("xerces", "org.apache.xerces.parsers.SAXParser");
        saxDrivers.put("xp", "com.jclark.xml.sax.Driver");
        saxDrivers.put("ibm1", "com.ibm.xml.parser.SAXDriver");
        saxDrivers.put("ibm2", "com.ibm.xml.parsers.SAXParser");
        saxDrivers.put("aelfred", "com.microstar.xml.SAXDriver");
        saxDrivers.put("oracle1", "oracle.xml.parser.XMLParser");
        saxDrivers.put("oracle2", "oracle.xml.parser.v2.SAXParser");
        saxDrivers.put("openxml", "org.openxml.parser.XMLSAXParser");
        keepalive = false;
        debug = false;
        types = new String[]{"String", "Integer", "Boolean", "Double", "Date", "Base64", "Struct", "Array"};
        encoding = "UTF8";
        defaultInputEncoding = null;
    }

    class Value {
        int type = 0;
        Object value;
        String nextMemberName;
        Hashtable struct;
        Vector array;

        public void endElement(Value child) {
            switch (this.type) {
                case 7: {
                    this.array.addElement(child.value);
                    break;
                }
                case 6: {
                    this.struct.put(this.nextMemberName, child.value);
                }
            }
        }

        public void setType(int type) {
            this.type = type;
            switch (type) {
                case 7: {
                    this.value = this.array = new Vector();
                    break;
                }
                case 6: {
                    this.value = this.struct = new Hashtable();
                }
            }
        }

        public void characterData(String cdata) {
            switch (this.type) {
                case 1: {
                    this.value = XmlRpc.this.typeFactory.createInteger(cdata);
                    break;
                }
                case 2: {
                    this.value = XmlRpc.this.typeFactory.createBoolean(cdata);
                    break;
                }
                case 3: {
                    this.value = XmlRpc.this.typeFactory.createDouble(cdata);
                    break;
                }
                case 4: {
                    this.value = XmlRpc.this.typeFactory.createDate(cdata);
                    break;
                }
                case 5: {
                    this.value = XmlRpc.this.typeFactory.createBase64(cdata);
                    break;
                }
                case 0: {
                    this.value = XmlRpc.this.typeFactory.createString(cdata);
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

