/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.random;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;

public class Rijndael_Properties {
    static final boolean GLOBAL_DEBUG = false;
    static final String ALGORITHM = "Rijndael";
    static final double VERSION = 0.1;
    static final String FULL_NAME = "Rijndael ver. 0.1";
    static final String NAME = "Rijndael_Properties";
    static final Properties properties;
    private static final String[][] DEFAULT_PROPERTIES;

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getProperty(String key, String value) {
        return properties.getProperty(key, value);
    }

    public static void list(PrintStream out) {
        Rijndael_Properties.list(new PrintWriter(out, true));
    }

    public static void list(PrintWriter out) {
        out.println("#");
        out.println("# ----- Begin Rijndael properties -----");
        out.println("#");
        Enumeration<?> e = properties.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String)e.nextElement();
            String value = Rijndael_Properties.getProperty(key);
            out.println(key + " = " + value);
        }
        out.println("#");
        out.println("# ----- End Rijndael properties -----");
    }

    public static Enumeration propertyNames() {
        return properties.propertyNames();
    }

    static int getLevel(String label) {
        String s = Rijndael_Properties.getProperty("Debug.Level." + label);
        if (s == null && (s = Rijndael_Properties.getProperty("Debug.Level.*")) == null) {
            return 0;
        }
        try {
            return Integer.parseInt(s);
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    static PrintWriter getOutput() {
        String name = Rijndael_Properties.getProperty("Output");
        PrintWriter pw = name != null && name.equals("out") ? new PrintWriter(System.out, true) : new PrintWriter(System.err, true);
        return pw;
    }

    static boolean isTraceable(String label) {
        String s = Rijndael_Properties.getProperty("Trace." + label);
        if (s == null) {
            return false;
        }
        return new Boolean(s);
    }

    static {
        boolean ok;
        properties = new Properties();
        DEFAULT_PROPERTIES = new String[][]{{"Trace.Rijndael_Algorithm", "true"}, {"Debug.Level.*", "1"}, {"Debug.Level.Rijndael_Algorithm", "9"}};
        String it = "Rijndael.properties";
        InputStream is = Rijndael_Properties.class.getResourceAsStream(it);
        boolean bl = ok = is != null;
        if (ok) {
            try {
                properties.load(is);
                is.close();
            }
            catch (Exception x) {
                ok = false;
            }
        }
        if (!ok) {
            int n = DEFAULT_PROPERTIES.length;
            for (int i = 0; i < n; ++i) {
                properties.put(DEFAULT_PROPERTIES[i][0], DEFAULT_PROPERTIES[i][1]);
            }
        }
    }
}

