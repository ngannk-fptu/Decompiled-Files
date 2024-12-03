/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.jrcs.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;

public class ToString {
    public String toString() {
        StringBuffer s = new StringBuffer();
        this.toString(s);
        return s.toString();
    }

    public void toString(StringBuffer s) {
        s.append(super.toString());
    }

    public static String[] stringToArray(String value) {
        BufferedReader reader = new BufferedReader(new StringReader(value));
        LinkedList<String> l = new LinkedList<String>();
        try {
            String s;
            while ((s = reader.readLine()) != null) {
                l.add(s);
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return l.toArray(new String[l.size()]);
    }

    public static String arrayToString(Object[] o) {
        return ToString.arrayToString(o, System.getProperty("line.separator"));
    }

    public static String arrayToString(Object[] o, String EOL) {
        StringBuffer buf = new StringBuffer();
        int i = 0;
        while (i < o.length - 1) {
            buf.append(o[i]);
            buf.append(EOL);
            ++i;
        }
        buf.append(o[o.length - 1]);
        return buf.toString();
    }
}

