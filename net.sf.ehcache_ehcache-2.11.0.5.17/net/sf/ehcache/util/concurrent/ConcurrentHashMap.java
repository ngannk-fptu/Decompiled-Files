/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.util.concurrent;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import net.sf.ehcache.Element;
import net.sf.ehcache.pool.PoolAccessor;
import net.sf.ehcache.pool.PoolParticipant;
import net.sf.ehcache.pool.impl.UnboundedPool;
import net.sf.ehcache.util.concurrent.LongAdder;
import net.sf.ehcache.util.concurrent.ThreadLocalRandom;

public class ConcurrentHashMap<K, V>
implements ConcurrentMap<K, V> {
    private static final long serialVersionUID = 7249069246763182397L;
    protected static final Node FAKE_NODE = new Node(0, null, null, null, 0);
    protected static final Node FAKE_TREE_NODE = new TreeNode(0, null, null, null, 0, null);
    private static final int MAXIMUM_CAPACITY = 0x40000000;
    private static final int DEFAULT_CAPACITY = 16;
    static final int MAX_ARRAY_SIZE = 0x7FFFFFF7;
    private static final int DEFAULT_CONCURRENCY_LEVEL = 16;
    private static final float LOAD_FACTOR = 0.75f;
    private static final int TRANSFER_BUFFER_SIZE = 32;
    private static final int TREE_THRESHOLD = 8;
    static final int MOVED = Integer.MIN_VALUE;
    static final int LOCKED = 0x40000000;
    static final int WAITING = -1073741824;
    static final int HASH_BITS = 0x3FFFFFFF;
    volatile transient AtomicReferenceArray<Node> table;
    private volatile PoolAccessor<PoolParticipant> poolAccessor;
    private final transient LongAdder counter;
    private volatile transient int sizeCtl;
    private transient KeySetView<K, V> keySet;
    private transient ValuesView<K, V> values;
    private transient EntrySetView<K, V> entrySet;
    private Segment<K, V>[] segments;
    private static final AtomicIntegerFieldUpdater<ConcurrentHashMap> SIZECTL_UPDATER = AtomicIntegerFieldUpdater.newUpdater(ConcurrentHashMap.class, "sizeCtl");

    static final Node tabAt(AtomicReferenceArray<Node> tab, int i) {
        return tab.get(i);
    }

    private static final boolean casTabAt(AtomicReferenceArray<Node> tab, int i, Node c, Node v) {
        return tab.compareAndSet(i, c, v);
    }

    private static final void setTabAt(AtomicReferenceArray<Node> tab, int i, Node v) {
        tab.set(i, v);
    }

    private static final int spread(int h) {
        h ^= h >>> 18 ^ h >>> 12;
        return (h ^ h >>> 10) & 0x3FFFFFFF;
    }

    private final void replaceWithTreeBin(AtomicReferenceArray<Node> tab, int index, Object key) {
        if (key instanceof Comparable && (tab.length() >= 0x40000000 || this.counter.sum() < (long)this.sizeCtl)) {
            TreeBin t = new TreeBin();
            Node e = ConcurrentHashMap.tabAt(tab, index);
            while (e != null) {
                t.putTreeNode(e.hash & 0x3FFFFFFF, e.key, e.val, e.size);
                e.val = null;
                e.size = -1;
                e = e.next;
            }
            ConcurrentHashMap.setTabAt(tab, index, new Node(Integer.MIN_VALUE, t, null, null));
        }
    }

    private final Object internalGet(Object k) {
        int h = ConcurrentHashMap.spread(k.hashCode());
        AtomicReferenceArray tab = this.table;
        block0: while (tab != null) {
            Node e = ConcurrentHashMap.tabAt(tab, tab.length() - 1 & h);
            while (e != null) {
                Object ev;
                Object ek;
                int eh = e.hash;
                if (eh == Integer.MIN_VALUE) {
                    ek = e.key;
                    if (ek instanceof TreeBin) {
                        return ((TreeBin)ek).getValue(h, k);
                    }
                    tab = (AtomicReferenceArray)ek;
                    continue block0;
                }
                if ((eh & 0x3FFFFFFF) == h && (ev = e.val) != null && ((ek = e.key) == k || k.equals(ek))) {
                    return ev;
                }
                e = e.next;
            }
            break block0;
        }
        return null;
    }

    private final Object internalReplace(Object k, Object v, Object cv) {
        return this.internalReplace(k, v, cv, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final Object internalReplace(Object k, Object v, Object cv, RemovalCallback hook) {
        int i;
        Node f;
        RuntimeException runtimeException = null;
        int h = ConcurrentHashMap.spread(k.hashCode());
        Object oldVal = null;
        int newSize = v != null ? (int)this.poolAccessor.add(k, v, FAKE_TREE_NODE, true) : 0;
        AtomicReferenceArray tab = this.table;
        while (tab != null && (f = ConcurrentHashMap.tabAt(tab, i = tab.length() - 1 & h)) != null) {
            int fh = f.hash;
            if (fh == Integer.MIN_VALUE) {
                Object fk = f.key;
                if (fk instanceof TreeBin) {
                    TreeBin t = (TreeBin)fk;
                    boolean validated = false;
                    boolean deleted = false;
                    t.acquire(0);
                    try {
                        if (ConcurrentHashMap.tabAt(tab, i) == f) {
                            validated = true;
                            TreeNode p = t.getTreeNode(h, k, t.root);
                            if (p != null) {
                                Object pv = p.val;
                                if (cv == null || cv == pv || !(cv instanceof Element) && cv.equals(pv)) {
                                    oldVal = pv;
                                    p.val = v;
                                    if (p.val == null) {
                                        deleted = true;
                                        t.deleteTreeNode(p);
                                    }
                                    this.poolAccessor.delete(p.size);
                                    int n = p.size = deleted ? -1 : newSize;
                                }
                            }
                        }
                        if (deleted && hook != null) {
                            try {
                                hook.removed(k, oldVal);
                            }
                            catch (Throwable e) {
                                runtimeException = e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException(e);
                            }
                        }
                    }
                    finally {
                        t.release(0);
                    }
                    if (!validated) continue;
                    if (!deleted) break;
                    this.counter.add(-1L);
                    break;
                }
                tab = (AtomicReferenceArray)fk;
                continue;
            }
            if ((fh & 0x3FFFFFFF) != h && f.next == null) break;
            if ((fh & 0x40000000) != 0) {
                this.checkForResize();
                f.tryAwaitLock(tab, i);
                continue;
            }
            if (!f.casHash(fh, fh | 0x40000000)) continue;
            boolean validated = false;
            boolean deleted = false;
            try {
                if (ConcurrentHashMap.tabAt(tab, i) == f) {
                    validated = true;
                    Node e = f;
                    Node pred = null;
                    do {
                        Object ek;
                        Object ev;
                        if ((e.hash & 0x3FFFFFFF) == h && (ev = e.val) != null && ((ek = e.key) == k || k.equals(ek))) {
                            if (cv != null && cv != ev && (cv instanceof Element || !cv.equals(ev))) break;
                            oldVal = ev;
                            e.val = v;
                            if (e.val == null) {
                                deleted = true;
                                Node en = e.next;
                                if (pred != null) {
                                    pred.next = en;
                                } else {
                                    ConcurrentHashMap.setTabAt(tab, i, en);
                                }
                            }
                            this.poolAccessor.delete(e.size);
                            e.size = deleted ? -1 : newSize;
                            break;
                        }
                        pred = e;
                    } while ((e = e.next) != null);
                }
                if (deleted && hook != null) {
                    try {
                        hook.removed(k, oldVal);
                    }
                    catch (Throwable e) {
                        runtimeException = e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException(e);
                    }
                }
            }
            finally {
                if (!f.casHash(fh | 0x40000000, fh)) {
                    Node.HASH_UPDATER.set(f, fh);
                    Node node = f;
                    synchronized (node) {
                        f.notifyAll();
                    }
                }
            }
            if (!validated) continue;
            if (!deleted) break;
            this.counter.add(-1L);
            break;
        }
        if (newSize > 0 && oldVal == null) {
            this.poolAccessor.delete(newSize);
        }
        if (runtimeException != null) {
            throw runtimeException;
        }
        return oldVal;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final Object internalPut(Object k, Object v) {
        int count;
        block29: {
            Object oldVal;
            int h = ConcurrentHashMap.spread(k.hashCode());
            count = 0;
            AtomicReferenceArray tab = this.table;
            while (true) {
                block31: {
                    if (tab == null) {
                        tab = this.initTable();
                        continue;
                    }
                    int i = tab.length() - 1 & h;
                    Node f = ConcurrentHashMap.tabAt(tab, i);
                    if (f == null) {
                        if (!ConcurrentHashMap.casTabAt(tab, i, null, new Node(h, k, v, null))) continue;
                        break block29;
                    }
                    int fh = f.hash;
                    if (fh == Integer.MIN_VALUE) {
                        Object fk = f.key;
                        if (fk instanceof TreeBin) {
                            TreeBin t = (TreeBin)fk;
                            Object oldVal2 = null;
                            t.acquire(0);
                            try {
                                if (ConcurrentHashMap.tabAt(tab, i) == f) {
                                    count = 2;
                                    TreeNode p = t.putTreeNode(h, k, v);
                                    if (p != null) {
                                        oldVal2 = p.val;
                                        p.val = v;
                                    }
                                }
                            }
                            finally {
                                t.release(0);
                            }
                            if (count == 0) continue;
                            if (oldVal2 != null) {
                                return oldVal2;
                            }
                            break block29;
                        }
                        tab = (AtomicReferenceArray)fk;
                        continue;
                    }
                    if ((fh & 0x40000000) != 0) {
                        this.checkForResize();
                        f.tryAwaitLock(tab, i);
                        continue;
                    }
                    if (!f.casHash(fh, fh | 0x40000000)) continue;
                    oldVal = null;
                    try {
                        if (ConcurrentHashMap.tabAt(tab, i) != f) break block31;
                        count = 1;
                        Node e = f;
                        while (true) {
                            Object ek;
                            Object ev;
                            if ((e.hash & 0x3FFFFFFF) == h && (ev = e.val) != null && ((ek = e.key) == k || k.equals(ek))) {
                                oldVal = ev;
                                e.val = v;
                                break;
                            }
                            Node last = e;
                            e = e.next;
                            if (e == null) {
                                last.next = new Node(h, k, v, null);
                                if (count >= 8) {
                                    this.replaceWithTreeBin(tab, i, k);
                                }
                                break;
                            }
                            ++count;
                        }
                    }
                    finally {
                        if (!f.casHash(fh | 0x40000000, fh)) {
                            Node.HASH_UPDATER.set(f, fh);
                            Node node = f;
                            synchronized (node) {
                                f.notifyAll();
                            }
                        }
                    }
                }
                if (count != 0) break;
            }
            if (oldVal != null) {
                return oldVal;
            }
            if (tab.length() <= 64) {
                count = 2;
            }
        }
        this.counter.add(1L);
        if (count > 1) {
            this.checkForResize();
        }
        return null;
    }

    private final Object internalPutIfAbsent(Object k, Object v) {
        return this.internalPutIfAbsent(k, v, 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final Object internalPutIfAbsent(Object k, Object v, int size) {
        int count;
        block32: {
            Object oldVal;
            int h = ConcurrentHashMap.spread(k.hashCode());
            count = 0;
            AtomicReferenceArray tab = this.table;
            while (true) {
                block34: {
                    Object fv;
                    Object fk;
                    if (tab == null) {
                        tab = this.initTable();
                        continue;
                    }
                    int i = tab.length() - 1 & h;
                    Node f = ConcurrentHashMap.tabAt(tab, i);
                    if (f == null) {
                        if (!ConcurrentHashMap.casTabAt(tab, i, null, new Node(h, k, v, null, size))) continue;
                        break block32;
                    }
                    int fh = f.hash;
                    if (fh == Integer.MIN_VALUE) {
                        fk = f.key;
                        if (fk instanceof TreeBin) {
                            TreeBin t = (TreeBin)fk;
                            oldVal = null;
                            t.acquire(0);
                            try {
                                if (ConcurrentHashMap.tabAt(tab, i) == f) {
                                    count = 2;
                                    TreeNode p = t.putTreeNode(h, k, v, size);
                                    if (p != null) {
                                        oldVal = p.val;
                                    }
                                }
                            }
                            finally {
                                t.release(0);
                            }
                            if (count == 0) continue;
                            if (oldVal != null) {
                                return oldVal;
                            }
                            break block32;
                        }
                        tab = (AtomicReferenceArray)fk;
                        continue;
                    }
                    if ((fh & 0x3FFFFFFF) == h && (fv = f.val) != null && ((fk = f.key) == k || k.equals(fk))) {
                        return fv;
                    }
                    Node g = f.next;
                    if (g != null) {
                        Node e = g;
                        do {
                            Object ek;
                            Object ev;
                            if ((e.hash & 0x3FFFFFFF) != h || (ev = e.val) == null || (ek = e.key) != k && !k.equals(ek)) continue;
                            return ev;
                        } while ((e = e.next) != null);
                        this.checkForResize();
                    }
                    if (((fh = f.hash) & 0x40000000) != 0) {
                        this.checkForResize();
                        f.tryAwaitLock(tab, i);
                        continue;
                    }
                    if (ConcurrentHashMap.tabAt(tab, i) != f || !f.casHash(fh, fh | 0x40000000)) continue;
                    oldVal = null;
                    try {
                        if (ConcurrentHashMap.tabAt(tab, i) != f) break block34;
                        count = 1;
                        Node e = f;
                        while (true) {
                            Object ek;
                            Object ev;
                            if ((e.hash & 0x3FFFFFFF) == h && (ev = e.val) != null && ((ek = e.key) == k || k.equals(ek))) {
                                oldVal = ev;
                                break;
                            }
                            Node last = e;
                            e = e.next;
                            if (e == null) {
                                last.next = new Node(h, k, v, null, size);
                                if (count >= 8) {
                                    this.replaceWithTreeBin(tab, i, k);
                                }
                                break;
                            }
                            ++count;
                        }
                    }
                    finally {
                        if (!f.casHash(fh | 0x40000000, fh)) {
                            Node.HASH_UPDATER.set(f, fh);
                            Node node = f;
                            synchronized (node) {
                                f.notifyAll();
                            }
                        }
                    }
                }
                if (count != 0) break;
            }
            if (oldVal != null) {
                return oldVal;
            }
            if (tab.length() <= 64) {
                count = 2;
            }
        }
        this.counter.add(1L);
        if (count > 1) {
            this.checkForResize();
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void internalPutAll(Map<?, ?> m) {
        this.tryPresize(m.size());
        long delta = 0L;
        boolean npe = false;
        try {
            block15: for (Map.Entry<?, ?> entry : m.entrySet()) {
                int count;
                Object v;
                Object k;
                if (entry == null || (k = entry.getKey()) == null || (v = entry.getValue()) == null) {
                    npe = true;
                    break;
                }
                int h = ConcurrentHashMap.spread(k.hashCode());
                AtomicReferenceArray tab = this.table;
                while (true) {
                    block35: {
                        if (tab == null) {
                            tab = this.initTable();
                            continue;
                        }
                        int i = tab.length() - 1 & h;
                        Node f = ConcurrentHashMap.tabAt(tab, i);
                        if (f == null) {
                            if (!ConcurrentHashMap.casTabAt(tab, i, null, new Node(h, k, v, null))) continue;
                            ++delta;
                            continue block15;
                        }
                        int fh = f.hash;
                        if (fh == Integer.MIN_VALUE) {
                            Object fk = f.key;
                            if (fk instanceof TreeBin) {
                                TreeBin t = (TreeBin)fk;
                                boolean validated = false;
                                t.acquire(0);
                                try {
                                    if (ConcurrentHashMap.tabAt(tab, i) == f) {
                                        validated = true;
                                        TreeNode p = t.getTreeNode(h, k, t.root);
                                        if (p != null) {
                                            p.val = v;
                                        } else {
                                            t.putTreeNode(h, k, v);
                                            ++delta;
                                        }
                                    }
                                }
                                finally {
                                    t.release(0);
                                }
                                if (!validated) continue;
                                continue block15;
                            }
                            tab = (AtomicReferenceArray)fk;
                            continue;
                        }
                        if ((fh & 0x40000000) != 0) {
                            this.counter.add(delta);
                            delta = 0L;
                            this.checkForResize();
                            f.tryAwaitLock(tab, i);
                            continue;
                        }
                        if (!f.casHash(fh, fh | 0x40000000)) continue;
                        count = 0;
                        try {
                            if (ConcurrentHashMap.tabAt(tab, i) != f) break block35;
                            count = 1;
                            Node e = f;
                            while (true) {
                                Object ek;
                                Object ev;
                                if ((e.hash & 0x3FFFFFFF) == h && (ev = e.val) != null && ((ek = e.key) == k || k.equals(ek))) {
                                    e.val = v;
                                    break;
                                }
                                Node last = e;
                                e = e.next;
                                if (e == null) {
                                    ++delta;
                                    last.next = new Node(h, k, v, null);
                                    if (count >= 8) {
                                        this.replaceWithTreeBin(tab, i, k);
                                    }
                                    break;
                                }
                                ++count;
                            }
                        }
                        finally {
                            if (!f.casHash(fh | 0x40000000, fh)) {
                                Node.HASH_UPDATER.set(f, fh);
                                Node node = f;
                                synchronized (node) {
                                    f.notifyAll();
                                }
                            }
                        }
                    }
                    if (count != 0) break;
                }
                if (count <= true) continue;
                this.counter.add(delta);
                delta = 0L;
                this.checkForResize();
            }
        }
        finally {
            if (delta != 0L) {
                this.counter.add(delta);
            }
        }
        if (npe) {
            throw new NullPointerException();
        }
    }

    private static final int tableSizeFor(int c) {
        int n = c - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        return (n |= n >>> 16) < 0 ? 1 : (n >= 0x40000000 ? 0x40000000 : n + 1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final AtomicReferenceArray<Node> initTable() {
        AtomicReferenceArray<Node> tab;
        while ((tab = this.table) == null) {
            int sc = this.sizeCtl;
            if (sc < 0) {
                Thread.yield();
                continue;
            }
            if (!SIZECTL_UPDATER.compareAndSet(this, sc, -1)) continue;
            try {
                tab = this.table;
                if (tab != null) break;
                int n = sc > 0 ? sc : 16;
                tab = this.table = new AtomicReferenceArray(n);
                sc = n - (n >>> 2);
                break;
            }
            finally {
                SIZECTL_UPDATER.set(this, sc);
            }
        }
        return tab;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void checkForResize() {
        int sc;
        int n;
        AtomicReferenceArray<Node> tab;
        while ((tab = this.table) != null && (n = tab.length()) < 0x40000000 && (sc = this.sizeCtl) >= 0 && this.counter.sum() >= (long)sc && SIZECTL_UPDATER.compareAndSet(this, sc, -1)) {
            try {
                if (tab != this.table) continue;
                this.table = ConcurrentHashMap.rebuild(tab);
                sc = (n << 1) - (n >>> 1);
            }
            finally {
                SIZECTL_UPDATER.set(this, sc);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void tryPresize(int size) {
        int sc;
        int c;
        int n = c = size >= 0x20000000 ? 0x40000000 : ConcurrentHashMap.tableSizeFor(size + (size >>> 1) + 1);
        while ((sc = this.sizeCtl) >= 0) {
            int n2;
            AtomicReferenceArray<Node> tab = this.table;
            if (tab == null || (n2 = tab.length()) == 0) {
                int n3 = n2 = sc > c ? sc : c;
                if (!SIZECTL_UPDATER.compareAndSet(this, sc, -1)) continue;
                try {
                    if (this.table != tab) continue;
                    this.table = new AtomicReferenceArray(n2);
                    sc = n2 - (n2 >>> 2);
                    continue;
                }
                finally {
                    SIZECTL_UPDATER.set(this, sc);
                    continue;
                }
            }
            if (c <= sc || n2 >= 0x40000000) break;
            if (!SIZECTL_UPDATER.compareAndSet(this, sc, -1)) continue;
            try {
                if (this.table != tab) continue;
                this.table = ConcurrentHashMap.rebuild(tab);
                sc = (n2 << 1) - (n2 >>> 1);
            }
            finally {
                SIZECTL_UPDATER.set(this, sc);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static final AtomicReferenceArray<Node> rebuild(AtomicReferenceArray<Node> tab) {
        int bin;
        int n = tab.length();
        AtomicReferenceArray<Node> nextTab = new AtomicReferenceArray<Node>(n << 1);
        Node fwd = new Node(Integer.MIN_VALUE, nextTab, null, null);
        int[] buffer = null;
        Node rev = null;
        int nbuffered = 0;
        int bufferIndex = 0;
        int i = bin = n - 1;
        while (true) {
            Node node;
            Node f;
            if ((f = ConcurrentHashMap.tabAt(tab, i)) == null) {
                if (bin >= 0) {
                    if (!ConcurrentHashMap.casTabAt(tab, i, f, fwd)) {
                        continue;
                    }
                } else {
                    Node g = new Node(-1073741824, nextTab, null, null);
                    if (!ConcurrentHashMap.casTabAt(tab, i, f, g)) continue;
                    ConcurrentHashMap.setTabAt(nextTab, i, null);
                    ConcurrentHashMap.setTabAt(nextTab, i + n, null);
                    ConcurrentHashMap.setTabAt(tab, i, fwd);
                    if (!g.casHash(-1073741824, Integer.MIN_VALUE)) {
                        Node.HASH_UPDATER.set(g, Integer.MIN_VALUE);
                        node = g;
                        synchronized (node) {
                            g.notifyAll();
                        }
                    }
                }
            } else {
                int fh = f.hash;
                if (fh == Integer.MIN_VALUE) {
                    Object fk = f.key;
                    if (fk instanceof TreeBin) {
                        TreeBin t = (TreeBin)fk;
                        boolean validated = false;
                        t.acquire(0);
                        try {
                            if (ConcurrentHashMap.tabAt(tab, i) == f) {
                                validated = true;
                                ConcurrentHashMap.splitTreeBin(nextTab, i, t);
                                ConcurrentHashMap.setTabAt(tab, i, fwd);
                            }
                        }
                        finally {
                            t.release(0);
                        }
                        if (!validated) {
                            continue;
                        }
                    }
                } else if ((fh & 0x40000000) == 0 && f.casHash(fh, fh | 0x40000000)) {
                    boolean validated = false;
                    try {
                        if (ConcurrentHashMap.tabAt(tab, i) == f) {
                            validated = true;
                            ConcurrentHashMap.splitBin(nextTab, i, f);
                            ConcurrentHashMap.setTabAt(tab, i, fwd);
                        }
                    }
                    finally {
                        if (!f.casHash(fh | 0x40000000, fh)) {
                            Node.HASH_UPDATER.set(f, fh);
                            node = f;
                            synchronized (node) {
                                f.notifyAll();
                            }
                        }
                    }
                    if (!validated) {
                        continue;
                    }
                } else {
                    if (buffer == null) {
                        buffer = new int[32];
                    }
                    if (bin < 0 && bufferIndex > 0) {
                        int j = buffer[--bufferIndex];
                        buffer[bufferIndex] = i;
                        i = j;
                        continue;
                    }
                    if (bin < 0 || nbuffered >= 32) {
                        f.tryAwaitLock(tab, i);
                        continue;
                    }
                    if (rev == null) {
                        rev = new Node(Integer.MIN_VALUE, tab, null, null);
                    }
                    if (ConcurrentHashMap.tabAt(tab, i) != f || (f.hash & 0x40000000) == 0) continue;
                    buffer[nbuffered++] = i;
                    ConcurrentHashMap.setTabAt(nextTab, i, rev);
                    ConcurrentHashMap.setTabAt(nextTab, i + n, rev);
                }
            }
            if (bin > 0) {
                i = --bin;
                continue;
            }
            if (buffer == null || nbuffered <= 0) break;
            bin = -1;
            bufferIndex = --nbuffered;
            i = buffer[bufferIndex];
        }
        return nextTab;
    }

    private static void splitBin(AtomicReferenceArray<Node> nextTab, int i, Node e) {
        int bit = nextTab.length() >>> 1;
        int runBit = e.hash & bit;
        Node lastRun = e;
        Node lo = null;
        Node hi = null;
        Node p = e.next;
        while (p != null) {
            int b = p.hash & bit;
            if (b != runBit) {
                runBit = b;
                lastRun = p;
            }
            p = p.next;
        }
        if (runBit == 0) {
            lo = lastRun;
        } else {
            hi = lastRun;
        }
        p = e;
        while (p != lastRun) {
            int ph = p.hash & 0x3FFFFFFF;
            Object pk = p.key;
            Object pv = p.val;
            int ps = p.size;
            if ((ph & bit) == 0) {
                lo = new Node(ph, pk, pv, lo, ps);
            } else {
                hi = new Node(ph, pk, pv, hi, ps);
            }
            p = p.next;
        }
        ConcurrentHashMap.setTabAt(nextTab, i, lo);
        ConcurrentHashMap.setTabAt(nextTab, i + bit, hi);
    }

    private static void splitTreeBin(AtomicReferenceArray<Node> nextTab, int i, TreeBin t) {
        Node hn;
        Node p;
        Node ln;
        int bit = nextTab.length() >>> 1;
        TreeBin lt = new TreeBin();
        TreeBin ht = new TreeBin();
        int lc = 0;
        int hc = 0;
        Node e = t.first;
        while (e != null) {
            int h = e.hash & 0x3FFFFFFF;
            Object k = e.key;
            Object v = e.val;
            if ((h & bit) == 0) {
                ++lc;
                lt.putTreeNode(h, k, v, e.size);
            } else {
                ++hc;
                ht.putTreeNode(h, k, v, e.size);
            }
            e.val = null;
            e.size = -1;
            e = e.next;
        }
        if (lc <= 4) {
            ln = null;
            p = lt.first;
            while (p != null) {
                ln = new Node(p.hash, p.key, p.val, ln, p.size);
                p = p.next;
            }
        } else {
            ln = new Node(Integer.MIN_VALUE, lt, null, null);
        }
        ConcurrentHashMap.setTabAt(nextTab, i, ln);
        if (hc <= 4) {
            hn = null;
            p = ht.first;
            while (p != null) {
                hn = new Node(p.hash, p.key, p.val, hn, p.size);
                p = p.next;
            }
        } else {
            hn = new Node(Integer.MIN_VALUE, ht, null, null);
        }
        ConcurrentHashMap.setTabAt(nextTab, i + bit, hn);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void internalClear() {
        long delta = 0L;
        int i = 0;
        AtomicReferenceArray tab = this.table;
        while (tab != null && i < tab.length()) {
            Node f = ConcurrentHashMap.tabAt(tab, i);
            if (f == null) {
                ++i;
                continue;
            }
            int fh = f.hash;
            if (fh == Integer.MIN_VALUE) {
                Object fk = f.key;
                if (fk instanceof TreeBin) {
                    TreeBin t = (TreeBin)fk;
                    t.acquire(0);
                    try {
                        if (ConcurrentHashMap.tabAt(tab, i) != f) continue;
                        Node p = t.first;
                        while (p != null) {
                            if (p.val != null) {
                                p.val = null;
                                --delta;
                            }
                            p = p.next;
                        }
                        t.first = null;
                        t.root = null;
                        ++i;
                        continue;
                    }
                    finally {
                        t.release(0);
                        continue;
                    }
                }
                tab = (AtomicReferenceArray)fk;
                continue;
            }
            if ((fh & 0x40000000) != 0) {
                this.counter.add(delta);
                delta = 0L;
                f.tryAwaitLock(tab, i);
                continue;
            }
            if (!f.casHash(fh, fh | 0x40000000)) continue;
            try {
                if (ConcurrentHashMap.tabAt(tab, i) != f) continue;
                Node e = f;
                while (e != null) {
                    if (e.val != null) {
                        e.val = null;
                        --delta;
                    }
                    e = e.next;
                }
                ConcurrentHashMap.setTabAt(tab, i, null);
                ++i;
            }
            finally {
                if (f.casHash(fh | 0x40000000, fh)) continue;
                Node.HASH_UPDATER.set(f, fh);
                Node node = f;
                synchronized (node) {
                    f.notifyAll();
                }
            }
        }
        if (delta != 0L) {
            this.counter.add(delta);
        }
        this.poolAccessor.clear();
    }

    public ConcurrentHashMap() {
        this.counter = new LongAdder();
        this.poolAccessor = UnboundedPool.UNBOUNDED_ACCESSOR;
    }

    public ConcurrentHashMap(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException();
        }
        int cap = initialCapacity >= 0x20000000 ? 0x40000000 : ConcurrentHashMap.tableSizeFor(initialCapacity + (initialCapacity >>> 1) + 1);
        this.counter = new LongAdder();
        SIZECTL_UPDATER.set(this, cap);
        this.poolAccessor = UnboundedPool.UNBOUNDED_ACCESSOR;
    }

    public ConcurrentHashMap(Map<? extends K, ? extends V> m) {
        this.counter = new LongAdder();
        SIZECTL_UPDATER.set(this, 16);
        this.poolAccessor = UnboundedPool.UNBOUNDED_ACCESSOR;
        this.internalPutAll(m);
    }

    public ConcurrentHashMap(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, 1);
    }

    public ConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
        long size;
        if (!(loadFactor > 0.0f) || initialCapacity < 0 || concurrencyLevel <= 0) {
            throw new IllegalArgumentException();
        }
        if (initialCapacity < concurrencyLevel) {
            initialCapacity = concurrencyLevel;
        }
        int cap = (size = (long)(1.0 + (double)((float)initialCapacity / loadFactor))) >= 0x40000000L ? 0x40000000 : ConcurrentHashMap.tableSizeFor((int)size);
        this.counter = new LongAdder();
        SIZECTL_UPDATER.set(this, cap);
        this.poolAccessor = UnboundedPool.UNBOUNDED_ACCESSOR;
    }

    protected void setPoolAccessor(PoolAccessor poolAccessor) {
        this.poolAccessor = poolAccessor;
    }

    public static <K> KeySetView<K, Boolean> newKeySet() {
        return new KeySetView<K, Boolean>(new ConcurrentHashMap(), Boolean.TRUE);
    }

    public static <K> KeySetView<K, Boolean> newKeySet(int initialCapacity) {
        return new KeySetView<K, Boolean>(new ConcurrentHashMap(initialCapacity), Boolean.TRUE);
    }

    @Override
    public boolean isEmpty() {
        return this.counter.sum() <= 0L;
    }

    @Override
    public int size() {
        long n = this.counter.sum();
        return n < 0L ? 0 : (n > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)n);
    }

    public long mappingCount() {
        long n = this.counter.sum();
        return n < 0L ? 0L : n;
    }

    @Override
    public V get(Object key) {
        if (key == null) {
            throw new NullPointerException();
        }
        return (V)this.internalGet(key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    public void recalculateSize(K k) {
        Object val = null;
        int previousSize = 0;
        int newSize = 0;
        long delta = Long.MIN_VALUE;
        boolean calculating = true;
        int h = ConcurrentHashMap.spread(k.hashCode());
        while (calculating) {
            AtomicReferenceArray tab = this.table;
            while (true) {
                boolean validated;
                block34: {
                    int i;
                    Node f;
                    if (tab == null || (f = ConcurrentHashMap.tabAt(tab, i = tab.length() - 1 & h)) == null) {
                        calculating = false;
                        break;
                    }
                    int fh = f.hash;
                    if (fh == Integer.MIN_VALUE) {
                        Object fk = f.key;
                        if (fk instanceof TreeBin) {
                            TreeBin t = (TreeBin)fk;
                            t.acquire(0);
                            try {
                                if (ConcurrentHashMap.tabAt(tab, i) != f) continue;
                                TreeNode p = t.getTreeNode(h, k, t.root);
                                if (p != null) {
                                    if (val == null) {
                                        val = p.val;
                                        previousSize = p.size;
                                    } else if (p.size == previousSize && p.val == val) {
                                        newSize = p.size = (int)((long)p.size + delta);
                                        calculating = false;
                                    } else {
                                        calculating = false;
                                    }
                                } else {
                                    calculating = false;
                                }
                                break;
                            }
                            finally {
                                t.release(0);
                                continue;
                            }
                        }
                        tab = (AtomicReferenceArray)fk;
                        continue;
                    }
                    if ((fh & 0x3FFFFFFF) != h && f.next == null) {
                        calculating = false;
                        break;
                    }
                    if ((fh & 0x40000000) != 0) {
                        this.checkForResize();
                        f.tryAwaitLock(tab, i);
                        continue;
                    }
                    if (!f.casHash(fh, fh | 0x40000000)) continue;
                    validated = false;
                    try {
                        if (ConcurrentHashMap.tabAt(tab, i) != f) break block34;
                        validated = true;
                        Node e = f;
                        do {
                            Object ek;
                            if ((e.hash & 0x3FFFFFFF) != h || (ek = e.key) != k && !k.equals(ek)) continue;
                            if (val == null) {
                                val = e.val;
                                previousSize = e.size;
                            } else if (e.size == previousSize && e.val == val) {
                                newSize = e.size = (int)((long)e.size + delta);
                                calculating = false;
                            } else {
                                calculating = false;
                            }
                            break;
                        } while ((e = e.next) != null);
                    }
                    finally {
                        if (!f.casHash(fh | 0x40000000, fh)) {
                            Node.HASH_UPDATER.set(f, fh);
                            Node node = f;
                            synchronized (node) {
                                f.notifyAll();
                            }
                        }
                    }
                }
                if (validated) break;
            }
            if (val != null) {
                if (delta == Long.MIN_VALUE) {
                    delta = this.poolAccessor.replace(previousSize, k, val, FAKE_TREE_NODE, true);
                    continue;
                }
                if (newSize == 0) {
                    this.poolAccessor.delete(delta);
                    calculating = false;
                    continue;
                }
                calculating = false;
                continue;
            }
            calculating = false;
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    public List<V> getRandomValues(int amount) {
        int tableStart;
        ArrayList<Object> sampled = new ArrayList<Object>(amount * 2);
        int randomHash = ThreadLocalRandom.current().nextInt();
        AtomicReferenceArray tab = this.table;
        if (tab == null) {
            return sampled;
        }
        int tableIndex = tableStart = randomHash & tab.length() - 1;
        block0: do {
            Node e = ConcurrentHashMap.tabAt(tab, tableIndex);
            while (e != null) {
                block8: {
                    if (e.hash == Integer.MIN_VALUE) {
                        Object ek = e.key;
                        if (ek instanceof TreeBin) {
                            e = ((TreeBin)ek).first;
                            if (e == null) continue block0;
                            sampled.add(e.val);
                            if (sampled.size() == amount) {
                                return sampled;
                            }
                            break block8;
                        } else {
                            tab = (AtomicReferenceArray)ek;
                            continue block0;
                        }
                    }
                    sampled.add(e.val);
                    if (sampled.size() == amount) {
                        return sampled;
                    }
                }
                e = e.next;
            }
        } while ((tableIndex = tableIndex + 1 & tab.length() - 1) != tableStart);
        return sampled;
    }

    public V getValueOrDefault(Object key, V defaultValue) {
        if (key == null) {
            throw new NullPointerException();
        }
        Object v = this.internalGet(key);
        return (V)(v == null ? defaultValue : v);
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            throw new NullPointerException();
        }
        return this.internalGet(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        Object v;
        if (value == null) {
            throw new NullPointerException();
        }
        Traverser it = new Traverser(this);
        while ((v = it.advance()) != null) {
            if (v != value && !value.equals(v)) continue;
            return true;
        }
        return false;
    }

    public boolean contains(Object value) {
        return this.containsValue(value);
    }

    @Override
    public V put(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException();
        }
        return (V)this.internalPut(key, value);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException();
        }
        return (V)this.internalPutIfAbsent(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        this.internalPutAll(m);
    }

    @Override
    public V remove(Object key) {
        if (key == null) {
            throw new NullPointerException();
        }
        return (V)this.internalReplace(key, null, null);
    }

    @Override
    public boolean remove(Object key, Object value) {
        if (key == null) {
            throw new NullPointerException();
        }
        if (value == null) {
            return false;
        }
        return this.internalReplace(key, null, value) != null;
    }

    protected boolean remove(Object key, Object value, RemovalCallback hook) {
        if (key == null) {
            throw new NullPointerException();
        }
        if (value == null) {
            return false;
        }
        return this.internalReplace(key, null, value, hook) != null;
    }

    protected V removeAndNotify(Object key, RemovalCallback hook) {
        return (V)this.internalReplace(key, null, null, hook);
    }

    protected V removeAndNotify(Object key, Object value, RemovalCallback hook) {
        return (V)this.internalReplace(key, null, value, hook);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        if (key == null || oldValue == null || newValue == null) {
            throw new NullPointerException();
        }
        return this.internalReplace(key, newValue, oldValue) != null;
    }

    @Override
    public V replace(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException();
        }
        return (V)this.internalReplace(key, value, null);
    }

    @Override
    public void clear() {
        this.internalClear();
    }

    public KeySetView<K, V> keySet() {
        KeySetView<K, V> ks = this.keySet;
        return ks != null ? ks : (this.keySet = new KeySetView(this, null));
    }

    public KeySetView<K, V> keySet(V mappedValue) {
        if (mappedValue == null) {
            throw new NullPointerException();
        }
        return new KeySetView(this, mappedValue);
    }

    public ValuesView<K, V> values() {
        ValuesView<K, V> vs = this.values;
        return vs != null ? vs : (this.values = new ValuesView(this));
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        EntrySetView<K, V> es = this.entrySet;
        return es != null ? es : (this.entrySet = new EntrySetView(this));
    }

    public Enumeration<K> keys() {
        return new KeyIterator(this);
    }

    public Enumeration<V> elements() {
        return new ValueIterator(this);
    }

    public Spliterator<K> keySpliterator() {
        return new KeyIterator(this);
    }

    public Spliterator<V> valueSpliterator() {
        return new ValueIterator(this);
    }

    public Spliterator<Map.Entry<K, V>> entrySpliterator() {
        return new EntryIterator(this);
    }

    @Override
    public int hashCode() {
        Object v;
        int h = 0;
        Traverser it = new Traverser(this);
        while ((v = it.advance()) != null) {
            h += it.nextKey.hashCode() ^ v.hashCode();
        }
        return h;
    }

    public String toString() {
        Traverser it = new Traverser(this);
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        Object v = it.advance();
        if (v != null) {
            while (true) {
                Object k;
                sb.append((k = it.nextKey) == this ? "(this Map)" : k);
                sb.append('=');
                sb.append(v == this ? "(this Map)" : v);
                v = it.advance();
                if (v == null) break;
                sb.append(',').append(' ');
            }
        }
        return sb.append('}').toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o != this) {
            Object val;
            if (!(o instanceof Map)) {
                return false;
            }
            Map m = (Map)o;
            Traverser it = new Traverser(this);
            while ((val = it.advance()) != null) {
                Object v = m.get(it.nextKey);
                if (v != null && (v == val || v.equals(val))) continue;
                return false;
            }
            for (Map.Entry e : m.entrySet()) {
                Object v;
                Object mv;
                Object mk = e.getKey();
                if (mk != null && (mv = e.getValue()) != null && (v = this.internalGet(mk)) != null && (mv == v || mv.equals(v))) continue;
                return false;
            }
        }
        return true;
    }

    static <K, V> AbstractMap.SimpleEntry<K, V> entryFor(K k, V v) {
        return new AbstractMap.SimpleEntry<K, V>(k, v);
    }

    protected static interface RemovalCallback {
        public void removed(Object var1, Object var2);
    }

    public static final class EntrySetView<K, V>
    extends CHMView<K, V>
    implements Set<Map.Entry<K, V>> {
        EntrySetView(ConcurrentHashMap<K, V> map) {
            super(map);
        }

        @Override
        public final boolean contains(Object o) {
            Object v;
            Object r;
            Map.Entry e;
            Object k;
            return o instanceof Map.Entry && (k = (e = (Map.Entry)o).getKey()) != null && (r = this.map.get(k)) != null && (v = e.getValue()) != null && (v == r || v.equals(r));
        }

        @Override
        public final boolean remove(Object o) {
            Object v;
            Map.Entry e;
            Object k;
            return o instanceof Map.Entry && (k = (e = (Map.Entry)o).getKey()) != null && (v = e.getValue()) != null && this.map.remove(k, v);
        }

        @Override
        public final Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator(this.map);
        }

        @Override
        public final boolean add(Map.Entry<K, V> e) {
            K key = e.getKey();
            V value = e.getValue();
            if (key == null || value == null) {
                throw new NullPointerException();
            }
            return this.map.internalPut(key, value) == null;
        }

        @Override
        public final boolean addAll(Collection<? extends Map.Entry<K, V>> c) {
            boolean added = false;
            for (Map.Entry<K, V> e : c) {
                if (!this.add(e)) continue;
                added = true;
            }
            return added;
        }

        @Override
        public boolean equals(Object o) {
            Set c;
            return o instanceof Set && ((c = (Set)o) == this || this.containsAll(c) && c.containsAll(this));
        }
    }

    public static final class ValuesView<K, V>
    extends CHMView<K, V>
    implements Collection<V> {
        ValuesView(ConcurrentHashMap<K, V> map) {
            super(map);
        }

        @Override
        public final boolean contains(Object o) {
            return this.map.containsValue(o);
        }

        @Override
        public final boolean remove(Object o) {
            if (o != null) {
                ValueIterator it = new ValueIterator(this.map);
                while (it.hasNext()) {
                    if (!o.equals(it.next())) continue;
                    it.remove();
                    return true;
                }
            }
            return false;
        }

        @Override
        public final Iterator<V> iterator() {
            return new ValueIterator(this.map);
        }

        @Override
        public final boolean add(V e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final boolean addAll(Collection<? extends V> c) {
            throw new UnsupportedOperationException();
        }
    }

    public static class KeySetView<K, V>
    extends CHMView<K, V>
    implements Set<K>,
    Serializable {
        private static final long serialVersionUID = 7249069246763182397L;
        private final V value;

        KeySetView(ConcurrentHashMap<K, V> map, V value) {
            super(map);
            this.value = value;
        }

        public V getMappedValue() {
            return this.value;
        }

        @Override
        public boolean contains(Object o) {
            return this.map.containsKey(o);
        }

        @Override
        public boolean remove(Object o) {
            return this.map.remove(o) != null;
        }

        @Override
        public Iterator<K> iterator() {
            return new KeyIterator(this.map);
        }

        @Override
        public boolean add(K e) {
            V v = this.value;
            if (v == null) {
                throw new UnsupportedOperationException();
            }
            if (e == null) {
                throw new NullPointerException();
            }
            return this.map.internalPutIfAbsent(e, v) == null;
        }

        @Override
        public boolean addAll(Collection<? extends K> c) {
            boolean added = false;
            V v = this.value;
            if (v == null) {
                throw new UnsupportedOperationException();
            }
            for (K e : c) {
                if (e == null) {
                    throw new NullPointerException();
                }
                if (this.map.internalPutIfAbsent(e, v) != null) continue;
                added = true;
            }
            return added;
        }

        @Override
        public boolean equals(Object o) {
            Set c;
            return o instanceof Set && ((c = (Set)o) == this || this.containsAll(c) && c.containsAll(this));
        }
    }

    static abstract class CHMView<K, V> {
        final ConcurrentHashMap<K, V> map;
        private static final String oomeMsg = "Required array size too large";

        CHMView(ConcurrentHashMap<K, V> map) {
            this.map = map;
        }

        public ConcurrentHashMap<K, V> getMap() {
            return this.map;
        }

        public final int size() {
            return this.map.size();
        }

        public final boolean isEmpty() {
            return this.map.isEmpty();
        }

        public final void clear() {
            this.map.clear();
        }

        public abstract Iterator<?> iterator();

        public abstract boolean contains(Object var1);

        public abstract boolean remove(Object var1);

        public final Object[] toArray() {
            long sz = this.map.mappingCount();
            if (sz > 0x7FFFFFF7L) {
                throw new OutOfMemoryError(oomeMsg);
            }
            int n = (int)sz;
            Object[] r = new Object[n];
            int i = 0;
            Iterator<?> it = this.iterator();
            while (it.hasNext()) {
                if (i == n) {
                    if (n >= 0x7FFFFFF7) {
                        throw new OutOfMemoryError(oomeMsg);
                    }
                    n = n >= 0x3FFFFFFB ? 0x7FFFFFF7 : (n += (n >>> 1) + 1);
                    r = Arrays.copyOf(r, n);
                }
                r[i++] = it.next();
            }
            return i == n ? r : Arrays.copyOf(r, i);
        }

        public final <T> T[] toArray(T[] a) {
            long sz = this.map.mappingCount();
            if (sz > 0x7FFFFFF7L) {
                throw new OutOfMemoryError(oomeMsg);
            }
            int m = (int)sz;
            T[] r = a.length >= m ? a : (Object[])Array.newInstance(a.getClass().getComponentType(), m);
            int n = r.length;
            int i = 0;
            Iterator<?> it = this.iterator();
            while (it.hasNext()) {
                if (i == n) {
                    if (n >= 0x7FFFFFF7) {
                        throw new OutOfMemoryError(oomeMsg);
                    }
                    n = n >= 0x3FFFFFFB ? 0x7FFFFFF7 : (n += (n >>> 1) + 1);
                    r = Arrays.copyOf(r, n);
                }
                r[i++] = it.next();
            }
            if (a == r && i < n) {
                r[i] = null;
                return r;
            }
            return i == n ? r : Arrays.copyOf(r, i);
        }

        public final int hashCode() {
            int h = 0;
            Iterator<?> it = this.iterator();
            while (it.hasNext()) {
                h += it.next().hashCode();
            }
            return h;
        }

        public final String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            Iterator<?> it = this.iterator();
            if (it.hasNext()) {
                while (true) {
                    Object e;
                    sb.append((Object)((e = it.next()) == this ? "(this Collection)" : e));
                    if (!it.hasNext()) break;
                    sb.append(',').append(' ');
                }
            }
            return sb.append(']').toString();
        }

        public final boolean containsAll(Collection<?> c) {
            if (c != this) {
                for (Object e : c) {
                    if (e != null && this.contains(e)) continue;
                    return false;
                }
            }
            return true;
        }

        public final boolean removeAll(Collection<?> c) {
            boolean modified = false;
            Iterator<?> it = this.iterator();
            while (it.hasNext()) {
                if (!c.contains(it.next())) continue;
                it.remove();
                modified = true;
            }
            return modified;
        }

        public final boolean retainAll(Collection<?> c) {
            boolean modified = false;
            Iterator<?> it = this.iterator();
            while (it.hasNext()) {
                if (c.contains(it.next())) continue;
                it.remove();
                modified = true;
            }
            return modified;
        }
    }

    static class Segment<K, V>
    implements Serializable {
        private static final long serialVersionUID = 2249069246763182397L;
        final float loadFactor;

        Segment(float lf) {
            this.loadFactor = lf;
        }
    }

    static final class MapEntry<K, V>
    implements Map.Entry<K, V> {
        final K key;
        V val;
        final ConcurrentHashMap<K, V> map;

        MapEntry(K key, V val, ConcurrentHashMap<K, V> map) {
            this.key = key;
            this.val = val;
            this.map = map;
        }

        @Override
        public final K getKey() {
            return this.key;
        }

        @Override
        public final V getValue() {
            return this.val;
        }

        @Override
        public final int hashCode() {
            return this.key.hashCode() ^ this.val.hashCode();
        }

        public final String toString() {
            return this.key + "=" + this.val;
        }

        @Override
        public final boolean equals(Object o) {
            Object v;
            Map.Entry e;
            Object k;
            return !(!(o instanceof Map.Entry) || (k = (e = (Map.Entry)o).getKey()) == null || (v = e.getValue()) == null || k != this.key && !k.equals(this.key) || v != this.val && !v.equals(this.val));
        }

        @Override
        public final V setValue(V value) {
            if (value == null) {
                throw new NullPointerException();
            }
            V v = this.val;
            this.val = value;
            this.map.put(this.key, value);
            return v;
        }
    }

    static final class EntryIterator<K, V>
    extends Traverser<K, V, Object>
    implements Spliterator<Map.Entry<K, V>> {
        EntryIterator(ConcurrentHashMap<K, V> map) {
            super(map);
        }

        EntryIterator(ConcurrentHashMap<K, V> map, Traverser<K, V, Object> it) {
            super(map, it, -1);
        }

        public EntryIterator<K, V> split() {
            if (this.nextKey != null) {
                throw new IllegalStateException();
            }
            return new EntryIterator<K, V>(this.map, this);
        }

        @Override
        public final Map.Entry<K, V> next() {
            Object v = this.nextVal;
            if (v == null && (v = this.advance()) == null) {
                throw new NoSuchElementException();
            }
            Object k = this.nextKey;
            this.nextVal = null;
            return new MapEntry<Object, Object>(k, v, this.map);
        }
    }

    static final class ValueIterator<K, V>
    extends Traverser<K, V, Object>
    implements Spliterator<V>,
    Enumeration<V> {
        ValueIterator(ConcurrentHashMap<K, V> map) {
            super(map);
        }

        ValueIterator(ConcurrentHashMap<K, V> map, Traverser<K, V, Object> it) {
            super(map, it, -1);
        }

        public ValueIterator<K, V> split() {
            if (this.nextKey != null) {
                throw new IllegalStateException();
            }
            return new ValueIterator<K, V>(this.map, this);
        }

        @Override
        public final V next() {
            Object v = this.nextVal;
            if (v == null && (v = this.advance()) == null) {
                throw new NoSuchElementException();
            }
            this.nextVal = null;
            return (V)v;
        }

        @Override
        public final V nextElement() {
            return this.next();
        }
    }

    static final class KeyIterator<K, V>
    extends Traverser<K, V, Object>
    implements Spliterator<K>,
    Enumeration<K> {
        KeyIterator(ConcurrentHashMap<K, V> map) {
            super(map);
        }

        KeyIterator(ConcurrentHashMap<K, V> map, Traverser<K, V, Object> it) {
            super(map, it, -1);
        }

        public KeyIterator<K, V> split() {
            if (this.nextKey != null) {
                throw new IllegalStateException();
            }
            return new KeyIterator<K, V>(this.map, this);
        }

        @Override
        public final K next() {
            if (this.nextVal == null && this.advance() == null) {
                throw new NoSuchElementException();
            }
            Object k = this.nextKey;
            this.nextVal = null;
            return (K)k;
        }

        @Override
        public final K nextElement() {
            return this.next();
        }
    }

    static class Traverser<K, V, R> {
        final ConcurrentHashMap<K, V> map;
        Node next;
        Object nextKey;
        Object nextVal;
        AtomicReferenceArray<Node> tab;
        int index;
        int baseIndex;
        int baseLimit;
        int baseSize;
        int batch;

        Traverser(ConcurrentHashMap<K, V> map) {
            this.map = map;
        }

        Traverser(ConcurrentHashMap<K, V> map, Traverser<K, V, ?> it, int batch) {
            this.batch = batch;
            this.map = map;
            if (this.map != null && it != null) {
                AtomicReferenceArray<Node> t = it.tab;
                if (t == null) {
                    t = it.tab = map.table;
                    if (it.tab != null) {
                        it.baseLimit = it.baseSize = t.length();
                    }
                }
                this.tab = t;
                this.baseSize = it.baseSize;
                int hi = this.baseLimit = it.baseLimit;
                this.index = this.baseIndex = hi + it.baseIndex + 1 >>> 1;
                it.baseLimit = this.baseIndex;
            }
        }

        final Object advance() {
            Node e = this.next;
            Object ev = null;
            block0: do {
                if (e != null) {
                    e = e.next;
                }
                while (e == null) {
                    int n;
                    int i;
                    int n2;
                    AtomicReferenceArray<Node> t = this.tab;
                    if (t != null) {
                        n2 = t.length();
                    } else {
                        ConcurrentHashMap<K, V> m = this.map;
                        if (m == null) break block0;
                        t = this.tab = m.table;
                        if (this.tab == null) break block0;
                        this.baseLimit = this.baseSize = t.length();
                        n2 = this.baseSize;
                    }
                    int b = this.baseIndex;
                    if (b >= this.baseLimit || (i = this.index) < 0 || i >= n2) break block0;
                    e = ConcurrentHashMap.tabAt(t, i);
                    if (e != null && e.hash == Integer.MIN_VALUE) {
                        Object ek = e.key;
                        if (ek instanceof TreeBin) {
                            e = ((TreeBin)ek).first;
                        } else {
                            this.tab = (AtomicReferenceArray)ek;
                            continue;
                        }
                    }
                    if ((i += this.baseSize) < n2) {
                        n = i;
                    } else {
                        n = b + 1;
                        this.baseIndex = this.baseIndex;
                    }
                    this.index = n;
                }
                this.nextKey = e.key;
            } while ((ev = e.val) == null);
            this.next = e;
            this.nextVal = ev;
            return this.nextVal;
        }

        public final void remove() {
            Object k = this.nextKey;
            if (k == null && (this.advance() == null || (k = this.nextKey) == null)) {
                throw new IllegalStateException();
            }
            this.map.internalReplace(k, null, null);
        }

        public final boolean hasNext() {
            return this.nextVal != null || this.advance() != null;
        }

        public final boolean hasMoreElements() {
            return this.hasNext();
        }

        public void compute() {
        }
    }

    static final class TreeBin
    extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 2249069246763182397L;
        transient TreeNode root;
        transient TreeNode first;

        TreeBin() {
        }

        @Override
        public final boolean isHeldExclusively() {
            return this.getState() > 0;
        }

        @Override
        public final boolean tryAcquire(int ignore) {
            if (this.compareAndSetState(0, 1)) {
                this.setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override
        public final boolean tryRelease(int ignore) {
            this.setExclusiveOwnerThread(null);
            this.setState(0);
            return true;
        }

        @Override
        public final int tryAcquireShared(int ignore) {
            int c;
            do {
                if ((c = this.getState()) <= 0) continue;
                return -1;
            } while (!this.compareAndSetState(c, c - 1));
            return 1;
        }

        @Override
        public final boolean tryReleaseShared(int ignore) {
            int c;
            while (!this.compareAndSetState(c = this.getState(), c + 1)) {
            }
            return c == -1;
        }

        private void rotateLeft(TreeNode p) {
            if (p != null) {
                TreeNode r = p.right;
                TreeNode rl = p.right = r.left;
                if (p.right != null) {
                    rl.parent = p;
                }
                TreeNode pp = r.parent = p.parent;
                if (r.parent == null) {
                    this.root = r;
                } else if (pp.left == p) {
                    pp.left = r;
                } else {
                    pp.right = r;
                }
                r.left = p;
                p.parent = r;
            }
        }

        private void rotateRight(TreeNode p) {
            if (p != null) {
                TreeNode l = p.left;
                TreeNode lr = p.left = l.right;
                if (p.left != null) {
                    lr.parent = p;
                }
                TreeNode pp = l.parent = p.parent;
                if (l.parent == null) {
                    this.root = l;
                } else if (pp.right == p) {
                    pp.right = l;
                } else {
                    pp.left = l;
                }
                l.right = p;
                p.parent = l;
            }
        }

        final TreeNode getTreeNode(int h, Object k, TreeNode p) {
            Class<?> c = k.getClass();
            while (p != null) {
                int dir;
                int ph = p.hash;
                if (ph == h) {
                    Object pk = p.key;
                    if (pk == k || k.equals(pk)) {
                        return p;
                    }
                    Class<?> pc = pk.getClass();
                    if (!(c == pc && k instanceof Comparable && (dir = ((Comparable)k).compareTo((Comparable)pk)) != 0 || (dir = c == pc ? 0 : c.getName().compareTo(pc.getName())) != 0)) {
                        TreeNode r = null;
                        TreeNode pr = p.right;
                        if (pr != null && (r = this.getTreeNode(h, k, pr)) != null) {
                            return r;
                        }
                        dir = -1;
                    }
                } else {
                    dir = h < ph ? -1 : 1;
                }
                p = dir > 0 ? p.right : p.left;
            }
            return null;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        final Object getValue(int h, Object k) {
            TreeNode r = null;
            int c = this.getState();
            Node e = this.first;
            while (e != null) {
                if (c <= 0 && this.compareAndSetState(c, c - 1)) {
                    try {
                        r = this.getTreeNode(h, k, this.root);
                        break;
                    }
                    finally {
                        this.releaseShared(0);
                    }
                }
                if ((e.hash & 0x3FFFFFFF) == h && k.equals(e.key)) {
                    r = e;
                    break;
                }
                c = this.getState();
                e = e.next;
            }
            return r == null ? null : r.val;
        }

        final TreeNode putTreeNode(int h, Object k, Object v) {
            return this.putTreeNode(h, k, v, 0);
        }

        final TreeNode putTreeNode(int h, Object k, Object v, int size) {
            TreeNode r;
            Class<?> c = k.getClass();
            TreeNode pp = this.root;
            TreeNode p = null;
            int dir = 0;
            while (pp != null) {
                p = pp;
                int ph = p.hash;
                if (ph == h) {
                    Object pk = p.key;
                    if (pk == k || k.equals(pk)) {
                        return p;
                    }
                    Class<?> pc = pk.getClass();
                    if (c != pc || !(k instanceof Comparable) || (dir = ((Comparable)k).compareTo((Comparable)pk)) == 0) {
                        TreeNode pr;
                        TreeNode s = null;
                        r = null;
                        dir = c == pc ? 0 : c.getName().compareTo(pc.getName());
                        if (dir == 0) {
                            pr = p.right;
                            if (pr != null && (r = this.getTreeNode(h, k, pr)) != null) {
                                return r;
                            }
                            dir = -1;
                        } else {
                            pr = p.right;
                            if (pr != null && h >= pr.hash) {
                                s = pr;
                            }
                        }
                        if (s != null && (r = this.getTreeNode(h, k, s)) != null) {
                            return r;
                        }
                    }
                } else {
                    dir = h < ph ? -1 : 1;
                }
                pp = dir > 0 ? p.right : p.left;
            }
            TreeNode f = this.first;
            TreeNode x = this.first = new TreeNode(h, k, v, f, size, p);
            if (p == null) {
                this.root = x;
            } else {
                TreeNode xpp;
                TreeNode xp;
                if (f != null) {
                    f.prev = x;
                }
                if (dir <= 0) {
                    p.left = x;
                } else {
                    p.right = x;
                }
                x.red = true;
                while (x != null && (xp = x.parent) != null && xp.red && (xpp = xp.parent) != null) {
                    TreeNode y;
                    TreeNode xppl = xpp.left;
                    if (xp == xppl) {
                        y = xpp.right;
                        if (y != null && y.red) {
                            y.red = false;
                            xp.red = false;
                            xpp.red = true;
                            x = xpp;
                            continue;
                        }
                        if (x == xp.right) {
                            x = xp;
                            this.rotateLeft(x);
                            xp = x.parent;
                            TreeNode treeNode = xpp = xp == null ? null : xp.parent;
                        }
                        if (xp == null) continue;
                        xp.red = false;
                        if (xpp == null) continue;
                        xpp.red = true;
                        this.rotateRight(xpp);
                        continue;
                    }
                    y = xppl;
                    if (y != null && y.red) {
                        y.red = false;
                        xp.red = false;
                        xpp.red = true;
                        x = xpp;
                        continue;
                    }
                    if (x == xp.left) {
                        x = xp;
                        this.rotateRight(x);
                        xp = x.parent;
                        TreeNode treeNode = xpp = xp == null ? null : xp.parent;
                    }
                    if (xp == null) continue;
                    xp.red = false;
                    if (xpp == null) continue;
                    xpp.red = true;
                    this.rotateLeft(xpp);
                }
                r = this.root;
                if (r != null && r.red) {
                    r.red = false;
                }
            }
            return null;
        }

        final void deleteTreeNode(TreeNode p) {
            TreeNode replacement;
            TreeNode next = (TreeNode)p.next;
            TreeNode pred = p.prev;
            if (pred == null) {
                this.first = next;
            } else {
                pred.next = next;
            }
            if (next != null) {
                next.prev = pred;
            }
            TreeNode pl = p.left;
            TreeNode pr = p.right;
            if (pl != null && pr != null) {
                TreeNode sl;
                TreeNode s = pr;
                while ((sl = s.left) != null) {
                    s = sl;
                }
                boolean c = s.red;
                s.red = p.red;
                p.red = c;
                TreeNode sr = s.right;
                TreeNode pp = p.parent;
                if (s == pr) {
                    p.parent = s;
                    s.right = p;
                } else {
                    TreeNode sp = s.parent;
                    p.parent = sp;
                    if (p.parent != null) {
                        if (s == sp.left) {
                            sp.left = p;
                        } else {
                            sp.right = p;
                        }
                    }
                    if ((s.right = pr) != null) {
                        pr.parent = s;
                    }
                }
                p.left = null;
                p.right = sr;
                if (p.right != null) {
                    sr.parent = p;
                }
                if ((s.left = pl) != null) {
                    pl.parent = s;
                }
                if ((s.parent = pp) == null) {
                    this.root = s;
                } else if (p == pp.left) {
                    pp.left = s;
                } else {
                    pp.right = s;
                }
                replacement = sr;
            } else {
                replacement = pl != null ? pl : pr;
            }
            TreeNode pp = p.parent;
            if (replacement == null) {
                if (pp == null) {
                    this.root = null;
                    return;
                }
                replacement = p;
            } else {
                replacement.parent = pp;
                if (pp == null) {
                    this.root = replacement;
                } else if (p == pp.left) {
                    pp.left = replacement;
                } else {
                    pp.right = replacement;
                }
                p.parent = null;
                p.right = null;
                p.left = null;
            }
            if (!p.red) {
                TreeNode x = replacement;
                while (x != null) {
                    TreeNode sr;
                    TreeNode sl;
                    TreeNode sib;
                    TreeNode xp;
                    if (x.red || (xp = x.parent) == null) {
                        x.red = false;
                        break;
                    }
                    TreeNode xpl = xp.left;
                    if (x == xpl) {
                        sib = xp.right;
                        if (sib != null && sib.red) {
                            sib.red = false;
                            xp.red = true;
                            this.rotateLeft(xp);
                            xp = x.parent;
                            TreeNode treeNode = sib = xp == null ? null : xp.right;
                        }
                        if (sib == null) {
                            x = xp;
                            continue;
                        }
                        sl = sib.left;
                        sr = sib.right;
                        if (!(sr != null && sr.red || sl != null && sl.red)) {
                            sib.red = true;
                            x = xp;
                            continue;
                        }
                        if (sr == null || !sr.red) {
                            if (sl != null) {
                                sl.red = false;
                            }
                            sib.red = true;
                            this.rotateRight(sib);
                            xp = x.parent;
                            TreeNode treeNode = sib = xp == null ? null : xp.right;
                        }
                        if (sib != null) {
                            sib.red = xp == null ? false : xp.red;
                            sr = sib.right;
                            if (sr != null) {
                                sr.red = false;
                            }
                        }
                        if (xp != null) {
                            xp.red = false;
                            this.rotateLeft(xp);
                        }
                        x = this.root;
                        continue;
                    }
                    sib = xpl;
                    if (sib != null && sib.red) {
                        sib.red = false;
                        xp.red = true;
                        this.rotateRight(xp);
                        xp = x.parent;
                        TreeNode treeNode = sib = xp == null ? null : xp.left;
                    }
                    if (sib == null) {
                        x = xp;
                        continue;
                    }
                    sl = sib.left;
                    sr = sib.right;
                    if (!(sl != null && sl.red || sr != null && sr.red)) {
                        sib.red = true;
                        x = xp;
                        continue;
                    }
                    if (sl == null || !sl.red) {
                        if (sr != null) {
                            sr.red = false;
                        }
                        sib.red = true;
                        this.rotateLeft(sib);
                        xp = x.parent;
                        TreeNode treeNode = sib = xp == null ? null : xp.left;
                    }
                    if (sib != null) {
                        sib.red = xp == null ? false : xp.red;
                        sl = sib.left;
                        if (sl != null) {
                            sl.red = false;
                        }
                    }
                    if (xp != null) {
                        xp.red = false;
                        this.rotateRight(xp);
                    }
                    x = this.root;
                }
            }
            if (p == replacement && (pp = p.parent) != null) {
                if (p == pp.left) {
                    pp.left = null;
                } else if (p == pp.right) {
                    pp.right = null;
                }
                p.parent = null;
            }
        }
    }

    protected static final class TreeNode
    extends Node {
        TreeNode parent;
        TreeNode left;
        TreeNode right;
        TreeNode prev;
        boolean red;

        public TreeNode(int hash, Object key, Object val, Node next, int size, TreeNode parent) {
            super(hash, key, val, next, size);
            this.parent = parent;
        }
    }

    protected static class Node {
        volatile int hash;
        final Object key;
        volatile Object val;
        volatile Node next;
        volatile int size;
        static final int MAX_SPINS = Runtime.getRuntime().availableProcessors() > 1 ? 64 : 1;
        static final AtomicIntegerFieldUpdater<Node> HASH_UPDATER = AtomicIntegerFieldUpdater.newUpdater(Node.class, "hash");

        public Node(int hash, Object key, Object val, Node next) {
            this(hash, key, val, next, 0);
        }

        public Node(int hash, Object key, Object val, Node next, int size) {
            HASH_UPDATER.set(this, hash);
            this.key = key;
            this.val = val;
            this.next = next;
            this.size = size;
        }

        final boolean casHash(int cmp, int val) {
            return HASH_UPDATER.compareAndSet(this, cmp, val);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        final void tryAwaitLock(AtomicReferenceArray<Node> tab, int i) {
            if (tab != null && i >= 0 && i < tab.length()) {
                int h;
                int r = ThreadLocalRandom.current().nextInt();
                int spins = MAX_SPINS;
                while (ConcurrentHashMap.tabAt(tab, i) == this && ((h = this.hash) & 0x40000000) != 0) {
                    if (spins >= 0) {
                        r ^= r << 1;
                        r ^= r >>> 3;
                        if ((r ^= r << 10) < 0 || --spins != 0) continue;
                        Thread.yield();
                        continue;
                    }
                    if (!this.casHash(h, h | 0xC0000000)) continue;
                    Node node = this;
                    synchronized (node) {
                        if (ConcurrentHashMap.tabAt(tab, i) == this && (this.hash & 0xC0000000) == -1073741824) {
                            try {
                                this.wait();
                            }
                            catch (InterruptedException ie) {
                                try {
                                    Thread.currentThread().interrupt();
                                }
                                catch (SecurityException securityException) {}
                            }
                        } else {
                            this.notifyAll();
                        }
                        break;
                    }
                }
            }
        }
    }

    public static interface Spliterator<T>
    extends Iterator<T> {
        public Spliterator<T> split();
    }
}

