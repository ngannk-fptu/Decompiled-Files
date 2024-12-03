/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.dom.DOMMessageFormatter
 *  org.apache.xml.serialize.HTMLdtd
 */
package org.owasp.validator.html.scan;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xml.serialize.HTMLdtd;

public class FastHTMLdtd {
    private static final String ENTITIES_RESOURCE = "HTMLEntities.res";
    private static HashMap<Integer, String> _byChar;
    private static HashMap<String, Integer> _byName;

    public static String fromChar(int value) {
        if (value > 65535) {
            return null;
        }
        FastHTMLdtd.initialize();
        String name = _byChar.get(value);
        return name;
    }

    private static void initialize() {
        InputStream is = null;
        BufferedReader reader = null;
        if (_byName != null) {
            return;
        }
        try {
            _byName = new HashMap();
            _byChar = new HashMap();
            is = HTMLdtd.class.getResourceAsStream(ENTITIES_RESOURCE);
            if (is == null) {
                throw new RuntimeException(DOMMessageFormatter.formatMessage((String)"http://apache.org/xml/serializer", (String)"ResourceNotFound", (Object[])new Object[]{ENTITIES_RESOURCE}));
            }
            reader = new BufferedReader(new InputStreamReader(is, "ASCII"));
            String line = reader.readLine();
            while (line != null) {
                if (line.length() == 0 || line.charAt(0) == '#') {
                    line = reader.readLine();
                    continue;
                }
                int index = line.indexOf(32);
                if (index > 1) {
                    String name = line.substring(0, index);
                    if (++index < line.length()) {
                        String value = line.substring(index);
                        if ((index = value.indexOf(32)) > 0) {
                            value = value.substring(0, index);
                        }
                        int code = Integer.parseInt(value);
                        FastHTMLdtd.defineEntity(name, (char)code);
                    }
                }
                line = reader.readLine();
            }
            is.close();
        }
        catch (Exception except) {
            throw new RuntimeException(DOMMessageFormatter.formatMessage((String)"http://apache.org/xml/serializer", (String)"ResourceNotLoaded", (Object[])new Object[]{ENTITIES_RESOURCE, except.toString()}));
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (Exception exception) {}
            }
        }
    }

    private static void defineEntity(String name, char value) {
        if (_byName.get(name) == null) {
            _byName.put(name, Integer.valueOf(value));
            _byChar.put(Integer.valueOf(value), name);
        }
    }
}

