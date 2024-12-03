/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.transport;

import java.io.Serializable;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Headers
extends TreeMap<String, List<String>> {
    private static final InsensitiveComparator INSTANCE = new InsensitiveComparator();

    public Headers() {
        super(INSTANCE);
    }

    public void add(String key, String value) {
        LinkedList<String> list = (LinkedList<String>)this.get(key);
        if (list == null) {
            list = new LinkedList<String>();
            this.put(key, list);
        }
        list.add(value);
    }

    public String getFirst(String key) {
        List l = (List)this.get(key);
        return l == null ? null : (String)l.get(0);
    }

    public void set(String key, String value) {
        LinkedList<String> l = new LinkedList<String>();
        l.add(value);
        this.put(key, l);
    }

    @Override
    public void putAll(Map<? extends String, ? extends List<String>> map) {
        for (Map.Entry<? extends String, ? extends List<String>> entry : map.entrySet()) {
            List<String> list = entry.getValue();
            for (String v : list) {
                this.add(entry.getKey(), v);
            }
        }
    }

    private static final class InsensitiveComparator
    implements Comparator<String>,
    Serializable {
        private InsensitiveComparator() {
        }

        @Override
        public int compare(String o1, String o2) {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }
            return o1.compareToIgnoreCase(o2);
        }
    }
}

