/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 */
package org.apache.hc.client5.http.cookie;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieIdentityComparator;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;

@Contract(threading=ThreadingBehavior.SAFE)
public class BasicCookieStore
implements CookieStore,
Serializable {
    private static final long serialVersionUID = -7581093305228232025L;
    private final TreeSet<Cookie> cookies = new TreeSet<Cookie>(CookieIdentityComparator.INSTANCE);
    private transient ReadWriteLock lock = new ReentrantReadWriteLock();

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.lock = new ReentrantReadWriteLock();
    }

    @Override
    public void addCookie(Cookie cookie) {
        if (cookie != null) {
            this.lock.writeLock().lock();
            try {
                this.cookies.remove(cookie);
                if (!cookie.isExpired(Instant.now())) {
                    this.cookies.add(cookie);
                }
            }
            finally {
                this.lock.writeLock().unlock();
            }
        }
    }

    public void addCookies(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                this.addCookie(cookie);
            }
        }
    }

    @Override
    public List<Cookie> getCookies() {
        this.lock.readLock().lock();
        try {
            ArrayList<Cookie> arrayList = new ArrayList<Cookie>(this.cookies);
            return arrayList;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean clearExpired(Date date) {
        if (date == null) {
            return false;
        }
        this.lock.writeLock().lock();
        try {
            boolean removed = false;
            Iterator<Cookie> it = this.cookies.iterator();
            while (it.hasNext()) {
                if (!it.next().isExpired(date)) continue;
                it.remove();
                removed = true;
            }
            boolean bl = removed;
            return bl;
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean clearExpired(Instant instant) {
        if (instant == null) {
            return false;
        }
        this.lock.writeLock().lock();
        try {
            boolean removed = false;
            Iterator<Cookie> it = this.cookies.iterator();
            while (it.hasNext()) {
                if (!it.next().isExpired(instant)) continue;
                it.remove();
                removed = true;
            }
            boolean bl = removed;
            return bl;
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public void clear() {
        this.lock.writeLock().lock();
        try {
            this.cookies.clear();
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    public String toString() {
        this.lock.readLock().lock();
        try {
            String string = this.cookies.toString();
            return string;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }
}

