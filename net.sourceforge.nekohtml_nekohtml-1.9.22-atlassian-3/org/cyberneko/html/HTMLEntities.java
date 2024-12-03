/*
 * Decompiled with CFR 0.152.
 */
package org.cyberneko.html;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class HTMLEntities {
    protected static final Map ENTITIES;
    protected static final IntProperties SEITITNE;

    public static int get(String name) {
        String value = (String)ENTITIES.get(name);
        return value != null ? (int)value.charAt(0) : -1;
    }

    public static String get(int c) {
        return SEITITNE.get(c);
    }

    private static void load0(Properties props, String filename) {
        try {
            InputStream stream = HTMLEntities.class.getResourceAsStream(filename);
            props.load(stream);
            stream.close();
        }
        catch (IOException e) {
            System.err.println("error: unable to load resource \"" + filename + "\"");
        }
    }

    static {
        SEITITNE = new IntProperties();
        Properties props = new Properties();
        HTMLEntities.load0(props, "res/HTMLlat1.properties");
        HTMLEntities.load0(props, "res/HTMLspecial.properties");
        HTMLEntities.load0(props, "res/HTMLsymbol.properties");
        HTMLEntities.load0(props, "res/XMLbuiltin.properties");
        Enumeration<?> keys = props.propertyNames();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String value = props.getProperty(key);
            if (value.length() != 1) continue;
            char ivalue = value.charAt(0);
            SEITITNE.put(ivalue, key);
        }
        ENTITIES = Collections.unmodifiableMap(new HashMap<Object, Object>(props));
    }

    static class IntProperties {
        private Entry[] entries = new Entry[101];

        IntProperties() {
        }

        public void put(int key, String value) {
            Entry entry;
            int hash = key % this.entries.length;
            this.entries[hash] = entry = new Entry(key, value, this.entries[hash]);
        }

        public String get(int key) {
            int hash = key % this.entries.length;
            Entry entry = this.entries[hash];
            while (entry != null) {
                if (entry.key == key) {
                    return entry.value;
                }
                entry = entry.next;
            }
            return null;
        }

        static class Entry {
            public int key;
            public String value;
            public Entry next;

            public Entry(int key, String value, Entry next) {
                this.key = key;
                this.value = value;
                this.next = next;
            }
        }
    }
}

