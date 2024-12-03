/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom.util;

import java.util.HashMap;

public class HashTableStack {
    protected Link current = new Link(null);

    public void push() {
        ++this.current.pushCount;
    }

    public void pop() {
        if (this.current.pushCount-- == 0) {
            this.current = this.current.next;
        }
    }

    public String put(String s, String v) {
        if (this.current.pushCount != 0) {
            --this.current.pushCount;
            this.current = new Link(this.current);
        }
        if (s.length() == 0) {
            this.current.defaultStr = v;
        }
        return this.current.table.put(s, v);
    }

    public String get(String s) {
        if (s.length() == 0) {
            return this.current.defaultStr;
        }
        Link l = this.current;
        while (l != null) {
            String uri = (String)l.table.get(s);
            if (uri != null) {
                return uri;
            }
            l = l.next;
        }
        return null;
    }

    protected static class Link {
        public HashMap table = new HashMap();
        public Link next;
        public String defaultStr;
        public int pushCount = 0;

        public Link(Link n) {
            this.next = n;
            if (this.next != null) {
                this.defaultStr = this.next.defaultStr;
            }
        }
    }
}

