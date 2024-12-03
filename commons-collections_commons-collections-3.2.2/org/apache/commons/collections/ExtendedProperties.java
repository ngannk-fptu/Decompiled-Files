/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

public class ExtendedProperties
extends Hashtable {
    private ExtendedProperties defaults;
    protected String file;
    protected String basePath;
    protected String fileSeparator;
    protected boolean isInitialized;
    protected static String include = "include";
    protected ArrayList keysAsListed;
    protected static final String START_TOKEN = "${";
    protected static final String END_TOKEN = "}";

    protected String interpolate(String base) {
        return this.interpolateHelper(base, null);
    }

    protected String interpolateHelper(String base, List priorVariables) {
        if (base == null) {
            return null;
        }
        if (priorVariables == null) {
            priorVariables = new ArrayList<String>();
            priorVariables.add(base);
        }
        int begin = -1;
        int end = -1;
        int prec = 0 - END_TOKEN.length();
        String variable = null;
        StringBuffer result = new StringBuffer();
        while ((begin = base.indexOf(START_TOKEN, prec + END_TOKEN.length())) > -1 && (end = base.indexOf(END_TOKEN, begin)) > -1) {
            result.append(base.substring(prec + END_TOKEN.length(), begin));
            variable = base.substring(begin + START_TOKEN.length(), end);
            if (priorVariables.contains(variable)) {
                String initialBase = priorVariables.remove(0).toString();
                priorVariables.add(variable);
                StringBuffer priorVariableSb = new StringBuffer();
                Iterator it = priorVariables.iterator();
                while (it.hasNext()) {
                    priorVariableSb.append(it.next());
                    if (!it.hasNext()) continue;
                    priorVariableSb.append("->");
                }
                throw new IllegalStateException("infinite loop in property interpolation of " + initialBase + ": " + priorVariableSb.toString());
            }
            priorVariables.add(variable);
            Object value = this.getProperty(variable);
            if (value != null) {
                result.append(this.interpolateHelper(value.toString(), priorVariables));
                priorVariables.remove(priorVariables.size() - 1);
            } else if (this.defaults != null && this.defaults.getString(variable, null) != null) {
                result.append(this.defaults.getString(variable));
            } else {
                result.append(START_TOKEN).append(variable).append(END_TOKEN);
            }
            prec = end;
        }
        result.append(base.substring(prec + END_TOKEN.length(), base.length()));
        return result.toString();
    }

    private static String escape(String s) {
        StringBuffer buf = new StringBuffer(s);
        for (int i = 0; i < buf.length(); ++i) {
            char c = buf.charAt(i);
            if (c != ',' && c != '\\') continue;
            buf.insert(i, '\\');
            ++i;
        }
        return buf.toString();
    }

    private static String unescape(String s) {
        StringBuffer buf = new StringBuffer(s);
        for (int i = 0; i < buf.length() - 1; ++i) {
            char c1 = buf.charAt(i);
            char c2 = buf.charAt(i + 1);
            if (c1 != '\\' || c2 != '\\') continue;
            buf.deleteCharAt(i);
        }
        return buf.toString();
    }

    private static int countPreceding(String line, int index, char ch) {
        int i;
        for (i = index - 1; i >= 0 && line.charAt(i) == ch; --i) {
        }
        return index - 1 - i;
    }

    private static boolean endsWithSlash(String line) {
        if (!line.endsWith("\\")) {
            return false;
        }
        return ExtendedProperties.countPreceding(line, line.length() - 1, '\\') % 2 == 0;
    }

    public ExtendedProperties() {
        try {
            this.fileSeparator = (String)AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    return System.getProperty("file.separator");
                }
            });
        }
        catch (SecurityException ex) {
            this.fileSeparator = File.separator;
        }
        this.isInitialized = false;
        this.keysAsListed = new ArrayList();
    }

    public ExtendedProperties(String file) throws IOException {
        this(file, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ExtendedProperties(String file, String defaultFile) throws IOException {
        try {
            this.fileSeparator = (String)AccessController.doPrivileged(new /* invalid duplicate definition of identical inner class */);
        }
        catch (SecurityException ex) {
            this.fileSeparator = File.separator;
        }
        this.isInitialized = false;
        this.keysAsListed = new ArrayList();
        this.file = file;
        this.basePath = new File(file).getAbsolutePath();
        this.basePath = this.basePath.substring(0, this.basePath.lastIndexOf(this.fileSeparator) + 1);
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            this.load(in);
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (IOException ex) {}
        }
        if (defaultFile != null) {
            this.defaults = new ExtendedProperties(defaultFile);
        }
    }

    public boolean isInitialized() {
        return this.isInitialized;
    }

    public String getInclude() {
        return include;
    }

    public void setInclude(String inc) {
        include = inc;
    }

    public void load(InputStream input) throws IOException {
        this.load(input, null);
    }

    public synchronized void load(InputStream input, String enc) throws IOException {
        PropertiesReader reader = null;
        if (enc != null) {
            try {
                reader = new PropertiesReader(new InputStreamReader(input, enc));
            }
            catch (UnsupportedEncodingException ex) {
                // empty catch block
            }
        }
        if (reader == null) {
            try {
                reader = new PropertiesReader(new InputStreamReader(input, "8859_1"));
            }
            catch (UnsupportedEncodingException ex) {
                reader = new PropertiesReader(new InputStreamReader(input));
            }
        }
        try {
            while (true) {
                String line;
                if ((line = reader.readProperty()) == null) {
                    return;
                }
                int equalSign = line.indexOf(61);
                if (equalSign <= 0) continue;
                String key = line.substring(0, equalSign).trim();
                String value = line.substring(equalSign + 1).trim();
                if ("".equals(value)) continue;
                if (this.getInclude() != null && key.equalsIgnoreCase(this.getInclude())) {
                    File file = null;
                    if (value.startsWith(this.fileSeparator)) {
                        file = new File(value);
                    } else {
                        if (value.startsWith("." + this.fileSeparator)) {
                            value = value.substring(2);
                        }
                        file = new File(this.basePath + value);
                    }
                    if (file == null || !file.exists() || !file.canRead()) continue;
                    this.load(new FileInputStream(file));
                    continue;
                }
                this.addProperty(key, value);
            }
        }
        finally {
            this.isInitialized = true;
        }
    }

    public Object getProperty(String key) {
        Object obj = this.get(key);
        if (obj == null && this.defaults != null) {
            obj = this.defaults.get(key);
        }
        return obj;
    }

    public void addProperty(String key, Object value) {
        if (value instanceof String) {
            String str = (String)value;
            if (str.indexOf(",") > 0) {
                PropertiesTokenizer tokenizer = new PropertiesTokenizer(str);
                while (tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken();
                    this.addPropertyInternal(key, ExtendedProperties.unescape(token));
                }
            } else {
                this.addPropertyInternal(key, ExtendedProperties.unescape(str));
            }
        } else {
            this.addPropertyInternal(key, value);
        }
        this.isInitialized = true;
    }

    private void addPropertyDirect(String key, Object value) {
        if (!this.containsKey(key)) {
            this.keysAsListed.add(key);
        }
        this.put(key, value);
    }

    private void addPropertyInternal(String key, Object value) {
        Object current = this.get(key);
        if (current instanceof String) {
            Vector<Object> values = new Vector<Object>(2);
            values.add(current);
            values.add(value);
            this.put(key, values);
        } else if (current instanceof List) {
            ((List)current).add(value);
        } else {
            if (!this.containsKey(key)) {
                this.keysAsListed.add(key);
            }
            this.put(key, value);
        }
    }

    public void setProperty(String key, Object value) {
        this.clearProperty(key);
        this.addProperty(key, value);
    }

    public synchronized void save(OutputStream output, String header) throws IOException {
        if (output == null) {
            return;
        }
        PrintWriter theWrtr = new PrintWriter(output);
        if (header != null) {
            theWrtr.println(header);
        }
        Enumeration theKeys = this.keys();
        while (theKeys.hasMoreElements()) {
            String key = (String)theKeys.nextElement();
            Object value = this.get(key);
            if (value != null) {
                if (value instanceof String) {
                    StringBuffer currentOutput = new StringBuffer();
                    currentOutput.append(key);
                    currentOutput.append("=");
                    currentOutput.append(ExtendedProperties.escape((String)value));
                    theWrtr.println(currentOutput.toString());
                } else if (value instanceof List) {
                    List values = (List)value;
                    Iterator it = values.iterator();
                    while (it.hasNext()) {
                        String currentElement = (String)it.next();
                        StringBuffer currentOutput = new StringBuffer();
                        currentOutput.append(key);
                        currentOutput.append("=");
                        currentOutput.append(ExtendedProperties.escape(currentElement));
                        theWrtr.println(currentOutput.toString());
                    }
                }
            }
            theWrtr.println();
            theWrtr.flush();
        }
    }

    public void combine(ExtendedProperties props) {
        Iterator it = props.getKeys();
        while (it.hasNext()) {
            String key = (String)it.next();
            this.setProperty(key, props.get(key));
        }
    }

    public void clearProperty(String key) {
        if (this.containsKey(key)) {
            for (int i = 0; i < this.keysAsListed.size(); ++i) {
                if (!this.keysAsListed.get(i).equals(key)) continue;
                this.keysAsListed.remove(i);
                break;
            }
            this.remove(key);
        }
    }

    public Iterator getKeys() {
        return this.keysAsListed.iterator();
    }

    public Iterator getKeys(String prefix) {
        Iterator keys = this.getKeys();
        ArrayList matchingKeys = new ArrayList();
        while (keys.hasNext()) {
            Object key = keys.next();
            if (!(key instanceof String) || !((String)key).startsWith(prefix)) continue;
            matchingKeys.add(key);
        }
        return matchingKeys.iterator();
    }

    public ExtendedProperties subset(String prefix) {
        ExtendedProperties c = new ExtendedProperties();
        Iterator keys = this.getKeys();
        boolean validSubset = false;
        while (keys.hasNext()) {
            Object key = keys.next();
            if (!(key instanceof String) || !((String)key).startsWith(prefix)) continue;
            if (!validSubset) {
                validSubset = true;
            }
            String newKey = null;
            newKey = ((String)key).length() == prefix.length() ? prefix : ((String)key).substring(prefix.length() + 1);
            c.addPropertyDirect(newKey, this.get(key));
        }
        if (validSubset) {
            return c;
        }
        return null;
    }

    public void display() {
        Iterator i = this.getKeys();
        while (i.hasNext()) {
            String key = (String)i.next();
            Object value = this.get(key);
            System.out.println(key + " => " + value);
        }
    }

    public String getString(String key) {
        return this.getString(key, null);
    }

    public String getString(String key, String defaultValue) {
        Object value = this.get(key);
        if (value instanceof String) {
            return this.interpolate((String)value);
        }
        if (value == null) {
            if (this.defaults != null) {
                return this.interpolate(this.defaults.getString(key, defaultValue));
            }
            return this.interpolate(defaultValue);
        }
        if (value instanceof List) {
            return this.interpolate((String)((List)value).get(0));
        }
        throw new ClassCastException('\'' + key + "' doesn't map to a String object");
    }

    public Properties getProperties(String key) {
        return this.getProperties(key, new Properties());
    }

    public Properties getProperties(String key, Properties defaults) {
        String[] tokens = this.getStringArray(key);
        Properties props = new Properties(defaults);
        for (int i = 0; i < tokens.length; ++i) {
            String token = tokens[i];
            int equalSign = token.indexOf(61);
            if (equalSign <= 0) {
                throw new IllegalArgumentException('\'' + token + "' does not contain " + "an equals sign");
            }
            String pkey = token.substring(0, equalSign).trim();
            String pvalue = token.substring(equalSign + 1).trim();
            props.put(pkey, pvalue);
        }
        return props;
    }

    public String[] getStringArray(String key) {
        List values;
        Object value = this.get(key);
        if (value instanceof String) {
            values = new Vector(1);
            values.add(value);
        } else if (value instanceof List) {
            values = (List)value;
        } else {
            if (value == null) {
                if (this.defaults != null) {
                    return this.defaults.getStringArray(key);
                }
                return new String[0];
            }
            throw new ClassCastException('\'' + key + "' doesn't map to a String/List object");
        }
        String[] tokens = new String[values.size()];
        for (int i = 0; i < tokens.length; ++i) {
            tokens[i] = (String)values.get(i);
        }
        return tokens;
    }

    public Vector getVector(String key) {
        return this.getVector(key, null);
    }

    public Vector getVector(String key, Vector defaultValue) {
        Object value = this.get(key);
        if (value instanceof List) {
            return new Vector((List)value);
        }
        if (value instanceof String) {
            Vector values = new Vector(1);
            values.add(value);
            this.put(key, values);
            return values;
        }
        if (value == null) {
            if (this.defaults != null) {
                return this.defaults.getVector(key, defaultValue);
            }
            return defaultValue == null ? new Vector() : defaultValue;
        }
        throw new ClassCastException('\'' + key + "' doesn't map to a Vector object");
    }

    public List getList(String key) {
        return this.getList(key, null);
    }

    public List getList(String key, List defaultValue) {
        Object value = this.get(key);
        if (value instanceof List) {
            return new ArrayList((List)value);
        }
        if (value instanceof String) {
            ArrayList values = new ArrayList(1);
            values.add(value);
            this.put(key, values);
            return values;
        }
        if (value == null) {
            if (this.defaults != null) {
                return this.defaults.getList(key, defaultValue);
            }
            return defaultValue == null ? new ArrayList() : defaultValue;
        }
        throw new ClassCastException('\'' + key + "' doesn't map to a List object");
    }

    public boolean getBoolean(String key) {
        Boolean b = this.getBoolean(key, null);
        if (b != null) {
            return b;
        }
        throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return this.getBoolean(key, new Boolean(defaultValue));
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        Object value = this.get(key);
        if (value instanceof Boolean) {
            return (Boolean)value;
        }
        if (value instanceof String) {
            String s = this.testBoolean((String)value);
            Boolean b = new Boolean(s);
            this.put(key, b);
            return b;
        }
        if (value == null) {
            if (this.defaults != null) {
                return this.defaults.getBoolean(key, defaultValue);
            }
            return defaultValue;
        }
        throw new ClassCastException('\'' + key + "' doesn't map to a Boolean object");
    }

    public String testBoolean(String value) {
        String s = value.toLowerCase();
        if (s.equals("true") || s.equals("on") || s.equals("yes")) {
            return "true";
        }
        if (s.equals("false") || s.equals("off") || s.equals("no")) {
            return "false";
        }
        return null;
    }

    public byte getByte(String key) {
        Byte b = this.getByte(key, null);
        if (b != null) {
            return b;
        }
        throw new NoSuchElementException('\'' + key + " doesn't map to an existing object");
    }

    public byte getByte(String key, byte defaultValue) {
        return this.getByte(key, new Byte(defaultValue));
    }

    public Byte getByte(String key, Byte defaultValue) {
        Object value = this.get(key);
        if (value instanceof Byte) {
            return (Byte)value;
        }
        if (value instanceof String) {
            Byte b = new Byte((String)value);
            this.put(key, b);
            return b;
        }
        if (value == null) {
            if (this.defaults != null) {
                return this.defaults.getByte(key, defaultValue);
            }
            return defaultValue;
        }
        throw new ClassCastException('\'' + key + "' doesn't map to a Byte object");
    }

    public short getShort(String key) {
        Short s = this.getShort(key, null);
        if (s != null) {
            return s;
        }
        throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
    }

    public short getShort(String key, short defaultValue) {
        return this.getShort(key, new Short(defaultValue));
    }

    public Short getShort(String key, Short defaultValue) {
        Object value = this.get(key);
        if (value instanceof Short) {
            return (Short)value;
        }
        if (value instanceof String) {
            Short s = new Short((String)value);
            this.put(key, s);
            return s;
        }
        if (value == null) {
            if (this.defaults != null) {
                return this.defaults.getShort(key, defaultValue);
            }
            return defaultValue;
        }
        throw new ClassCastException('\'' + key + "' doesn't map to a Short object");
    }

    public int getInt(String name) {
        return this.getInteger(name);
    }

    public int getInt(String name, int def) {
        return this.getInteger(name, def);
    }

    public int getInteger(String key) {
        Integer i = this.getInteger(key, null);
        if (i != null) {
            return i;
        }
        throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
    }

    public int getInteger(String key, int defaultValue) {
        Integer i = this.getInteger(key, null);
        if (i == null) {
            return defaultValue;
        }
        return i;
    }

    public Integer getInteger(String key, Integer defaultValue) {
        Object value = this.get(key);
        if (value instanceof Integer) {
            return (Integer)value;
        }
        if (value instanceof String) {
            Integer i = new Integer((String)value);
            this.put(key, i);
            return i;
        }
        if (value == null) {
            if (this.defaults != null) {
                return this.defaults.getInteger(key, defaultValue);
            }
            return defaultValue;
        }
        throw new ClassCastException('\'' + key + "' doesn't map to a Integer object");
    }

    public long getLong(String key) {
        Long l = this.getLong(key, null);
        if (l != null) {
            return l;
        }
        throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
    }

    public long getLong(String key, long defaultValue) {
        return this.getLong(key, new Long(defaultValue));
    }

    public Long getLong(String key, Long defaultValue) {
        Object value = this.get(key);
        if (value instanceof Long) {
            return (Long)value;
        }
        if (value instanceof String) {
            Long l = new Long((String)value);
            this.put(key, l);
            return l;
        }
        if (value == null) {
            if (this.defaults != null) {
                return this.defaults.getLong(key, defaultValue);
            }
            return defaultValue;
        }
        throw new ClassCastException('\'' + key + "' doesn't map to a Long object");
    }

    public float getFloat(String key) {
        Float f = this.getFloat(key, null);
        if (f != null) {
            return f.floatValue();
        }
        throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
    }

    public float getFloat(String key, float defaultValue) {
        return this.getFloat(key, new Float(defaultValue)).floatValue();
    }

    public Float getFloat(String key, Float defaultValue) {
        Object value = this.get(key);
        if (value instanceof Float) {
            return (Float)value;
        }
        if (value instanceof String) {
            Float f = new Float((String)value);
            this.put(key, f);
            return f;
        }
        if (value == null) {
            if (this.defaults != null) {
                return this.defaults.getFloat(key, defaultValue);
            }
            return defaultValue;
        }
        throw new ClassCastException('\'' + key + "' doesn't map to a Float object");
    }

    public double getDouble(String key) {
        Double d = this.getDouble(key, null);
        if (d != null) {
            return d;
        }
        throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
    }

    public double getDouble(String key, double defaultValue) {
        return this.getDouble(key, new Double(defaultValue));
    }

    public Double getDouble(String key, Double defaultValue) {
        Object value = this.get(key);
        if (value instanceof Double) {
            return (Double)value;
        }
        if (value instanceof String) {
            Double d = new Double((String)value);
            this.put(key, d);
            return d;
        }
        if (value == null) {
            if (this.defaults != null) {
                return this.defaults.getDouble(key, defaultValue);
            }
            return defaultValue;
        }
        throw new ClassCastException('\'' + key + "' doesn't map to a Double object");
    }

    public static ExtendedProperties convertProperties(Properties props) {
        ExtendedProperties c = new ExtendedProperties();
        Enumeration<?> e = props.propertyNames();
        while (e.hasMoreElements()) {
            String s = (String)e.nextElement();
            c.setProperty(s, props.getProperty(s));
        }
        return c;
    }

    static class PropertiesTokenizer
    extends StringTokenizer {
        static final String DELIMITER = ",";

        public PropertiesTokenizer(String string) {
            super(string, DELIMITER);
        }

        public boolean hasMoreTokens() {
            return super.hasMoreTokens();
        }

        public String nextToken() {
            StringBuffer buffer = new StringBuffer();
            while (this.hasMoreTokens()) {
                String token = super.nextToken();
                if (ExtendedProperties.endsWithSlash(token)) {
                    buffer.append(token.substring(0, token.length() - 1));
                    buffer.append(DELIMITER);
                    continue;
                }
                buffer.append(token);
                break;
            }
            return buffer.toString().trim();
        }
    }

    static class PropertiesReader
    extends LineNumberReader {
        public PropertiesReader(Reader reader) {
            super(reader);
        }

        public String readProperty() throws IOException {
            StringBuffer buffer = new StringBuffer();
            String line = this.readLine();
            while (line != null) {
                if ((line = line.trim()).length() != 0 && line.charAt(0) != '#') {
                    if (ExtendedProperties.endsWithSlash(line)) {
                        line = line.substring(0, line.length() - 1);
                        buffer.append(line);
                    } else {
                        buffer.append(line);
                        return buffer.toString();
                    }
                }
                line = this.readLine();
            }
            return null;
        }
    }
}

