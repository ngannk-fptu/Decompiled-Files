/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.session;

import java.util.Enumeration;
import java.util.Hashtable;
import org.apache.axis.session.Session;

public class SimpleSession
implements Session {
    private Hashtable rep = null;
    private int timeout = -1;
    private long lastTouched = System.currentTimeMillis();

    public Object get(String key) {
        if (this.rep == null) {
            return null;
        }
        this.lastTouched = System.currentTimeMillis();
        return this.rep.get(key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void set(String key, Object value) {
        SimpleSession simpleSession = this;
        synchronized (simpleSession) {
            if (this.rep == null) {
                this.rep = new Hashtable();
            }
        }
        this.lastTouched = System.currentTimeMillis();
        this.rep.put(key, value);
    }

    public void remove(String key) {
        if (this.rep != null) {
            this.rep.remove(key);
        }
        this.lastTouched = System.currentTimeMillis();
    }

    public Enumeration getKeys() {
        if (this.rep != null) {
            return this.rep.keys();
        }
        return null;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public void touch() {
        this.lastTouched = System.currentTimeMillis();
    }

    public void invalidate() {
        this.rep = null;
        this.lastTouched = System.currentTimeMillis();
        this.timeout = -1;
    }

    public long getLastAccessTime() {
        return this.lastTouched;
    }

    public synchronized Object getLockObject() {
        if (this.rep == null) {
            this.rep = new Hashtable();
        }
        return this.rep;
    }
}

