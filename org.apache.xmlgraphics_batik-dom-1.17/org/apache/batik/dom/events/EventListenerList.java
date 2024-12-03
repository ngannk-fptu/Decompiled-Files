/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom.events;

import java.util.HashMap;
import org.apache.batik.dom.util.IntTable;
import org.w3c.dom.events.EventListener;

public class EventListenerList {
    protected int n;
    protected Entry head;
    protected IntTable counts = new IntTable();
    protected Entry[] listeners;
    protected HashMap listenersNS = new HashMap();

    public void addListener(String namespaceURI, Object group, EventListener listener) {
        Entry e = this.head;
        while (e != null) {
            if ((namespaceURI != null && namespaceURI.equals(e.namespaceURI) || namespaceURI == null && e.namespaceURI == null) && e.listener == listener) {
                return;
            }
            e = e.next;
        }
        this.head = new Entry(listener, namespaceURI, group, this.head);
        this.counts.inc(namespaceURI);
        ++this.n;
        this.listeners = null;
        this.listenersNS.remove(namespaceURI);
    }

    public void removeListener(String namespaceURI, EventListener listener) {
        if (this.head == null) {
            return;
        }
        if (this.head != null && (namespaceURI != null && namespaceURI.equals(this.head.namespaceURI) || namespaceURI == null && this.head.namespaceURI == null) && listener == this.head.listener) {
            this.head = this.head.next;
        } else {
            Entry prev = this.head;
            Entry e = this.head.next;
            while (e != null) {
                if ((namespaceURI != null && namespaceURI.equals(e.namespaceURI) || namespaceURI == null && e.namespaceURI == null) && e.listener == listener) {
                    prev.next = e.next;
                    break;
                }
                prev = e;
                e = e.next;
            }
            if (e == null) {
                return;
            }
        }
        this.counts.dec(namespaceURI);
        --this.n;
        this.listeners = null;
        this.listenersNS.remove(namespaceURI);
    }

    public Entry[] getEventListeners() {
        if (this.listeners != null) {
            return this.listeners;
        }
        this.listeners = new Entry[this.n];
        int i = 0;
        Entry e = this.head;
        while (e != null) {
            this.listeners[i++] = e;
            e = e.next;
        }
        return this.listeners;
    }

    public Entry[] getEventListeners(String namespaceURI) {
        if (namespaceURI == null) {
            return this.getEventListeners();
        }
        Entry[] ls = (Entry[])this.listenersNS.get(namespaceURI);
        if (ls != null) {
            return ls;
        }
        int count = this.counts.get(namespaceURI);
        if (count == 0) {
            return null;
        }
        ls = new Entry[count];
        this.listenersNS.put(namespaceURI, ls);
        int i = 0;
        Entry e = this.head;
        while (i < count) {
            if (namespaceURI.equals(e.namespaceURI)) {
                ls[i++] = e;
            }
            e = e.next;
        }
        return ls;
    }

    public boolean hasEventListener(String namespaceURI) {
        if (namespaceURI == null) {
            return this.n != 0;
        }
        return this.counts.get(namespaceURI) != 0;
    }

    public int size() {
        return this.n;
    }

    public static class Entry {
        protected EventListener listener;
        protected String namespaceURI;
        protected Object group;
        protected boolean mark;
        protected Entry next;

        public Entry(EventListener listener, String namespaceURI, Object group, Entry next) {
            this.listener = listener;
            this.namespaceURI = namespaceURI;
            this.group = group;
            this.next = next;
        }

        public EventListener getListener() {
            return this.listener;
        }

        public Object getGroup() {
            return this.group;
        }

        public String getNamespaceURI() {
            return this.namespaceURI;
        }
    }
}

