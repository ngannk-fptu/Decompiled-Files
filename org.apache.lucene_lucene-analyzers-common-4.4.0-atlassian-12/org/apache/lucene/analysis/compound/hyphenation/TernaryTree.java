/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.compound.hyphenation;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Stack;
import org.apache.lucene.analysis.compound.hyphenation.CharVector;

public class TernaryTree
implements Cloneable {
    protected char[] lo;
    protected char[] hi;
    protected char[] eq;
    protected char[] sc;
    protected CharVector kv;
    protected char root;
    protected char freenode;
    protected int length;
    protected static final int BLOCK_SIZE = 2048;

    TernaryTree() {
        this.init();
    }

    protected void init() {
        this.root = '\u0000';
        this.freenode = '\u0001';
        this.length = 0;
        this.lo = new char[2048];
        this.hi = new char[2048];
        this.eq = new char[2048];
        this.sc = new char[2048];
        this.kv = new CharVector();
    }

    public void insert(String key, char val) {
        int len = key.length() + 1;
        if (this.freenode + len > this.eq.length) {
            this.redimNodeArrays(this.eq.length + 2048);
        }
        char[] strkey = new char[len--];
        key.getChars(0, len, strkey, 0);
        strkey[len] = '\u0000';
        this.root = this.insert(this.root, strkey, 0, val);
    }

    public void insert(char[] key, int start, char val) {
        int len = TernaryTree.strlen(key) + 1;
        if (this.freenode + len > this.eq.length) {
            this.redimNodeArrays(this.eq.length + 2048);
        }
        this.root = this.insert(this.root, key, start, val);
    }

    private char insert(char p, char[] key, int start, char val) {
        char s;
        int len = TernaryTree.strlen(key, start);
        if (p == '\u0000') {
            char c = this.freenode;
            this.freenode = (char)(c + '\u0001');
            p = c;
            this.eq[p] = val;
            ++this.length;
            this.hi[p] = '\u0000';
            if (len > 0) {
                this.sc[p] = 65535;
                this.lo[p] = (char)this.kv.alloc(len + 1);
                TernaryTree.strcpy(this.kv.getArray(), this.lo[p], key, start);
            } else {
                this.sc[p] = '\u0000';
                this.lo[p] = '\u0000';
            }
            return p;
        }
        if (this.sc[p] == '\uffff') {
            char c = this.freenode;
            this.freenode = (char)(c + '\u0001');
            char pp = c;
            this.lo[pp] = this.lo[p];
            this.eq[pp] = this.eq[p];
            this.lo[p] = '\u0000';
            if (len > 0) {
                this.sc[p] = this.kv.get(this.lo[pp]);
                this.eq[p] = pp;
                char c2 = pp;
                this.lo[c2] = (char)(this.lo[c2] + '\u0001');
                if (this.kv.get(this.lo[pp]) == '\u0000') {
                    this.lo[pp] = '\u0000';
                    this.sc[pp] = '\u0000';
                    this.hi[pp] = '\u0000';
                } else {
                    this.sc[pp] = 65535;
                }
            } else {
                this.sc[pp] = 65535;
                this.hi[p] = pp;
                this.sc[p] = '\u0000';
                this.eq[p] = val;
                ++this.length;
                return p;
            }
        }
        if ((s = key[start]) < this.sc[p]) {
            this.lo[p] = this.insert(this.lo[p], key, start, val);
        } else if (s == this.sc[p]) {
            this.eq[p] = s != '\u0000' ? this.insert(this.eq[p], key, start + 1, val) : val;
        } else {
            this.hi[p] = this.insert(this.hi[p], key, start, val);
        }
        return p;
    }

    public static int strcmp(char[] a, int startA, char[] b, int startB) {
        while (a[startA] == b[startB]) {
            if (a[startA] == '\u0000') {
                return 0;
            }
            ++startA;
            ++startB;
        }
        return a[startA] - b[startB];
    }

    public static int strcmp(String str, char[] a, int start) {
        int i;
        int len = str.length();
        for (i = 0; i < len; ++i) {
            int d = str.charAt(i) - a[start + i];
            if (d != 0) {
                return d;
            }
            if (a[start + i] != '\u0000') continue;
            return d;
        }
        if (a[start + i] != '\u0000') {
            return -a[start + i];
        }
        return 0;
    }

    public static void strcpy(char[] dst, int di, char[] src, int si) {
        while (src[si] != '\u0000') {
            dst[di++] = src[si++];
        }
        dst[di] = '\u0000';
    }

    public static int strlen(char[] a, int start) {
        int len = 0;
        for (int i = start; i < a.length && a[i] != '\u0000'; ++i) {
            ++len;
        }
        return len;
    }

    public static int strlen(char[] a) {
        return TernaryTree.strlen(a, 0);
    }

    public int find(String key) {
        int len = key.length();
        char[] strkey = new char[len + 1];
        key.getChars(0, len, strkey, 0);
        strkey[len] = '\u0000';
        return this.find(strkey, 0);
    }

    public int find(char[] key, int start) {
        char p = this.root;
        int i = start;
        while (p != '\u0000') {
            if (this.sc[p] == '\uffff') {
                if (TernaryTree.strcmp(key, i, this.kv.getArray(), this.lo[p]) == 0) {
                    return this.eq[p];
                }
                return -1;
            }
            char c = key[i];
            int d = c - this.sc[p];
            if (d == 0) {
                if (c == '\u0000') {
                    return this.eq[p];
                }
                ++i;
                p = this.eq[p];
                continue;
            }
            if (d < 0) {
                p = this.lo[p];
                continue;
            }
            p = this.hi[p];
        }
        return -1;
    }

    public boolean knows(String key) {
        return this.find(key) >= 0;
    }

    private void redimNodeArrays(int newsize) {
        int len = newsize < this.lo.length ? newsize : this.lo.length;
        char[] na = new char[newsize];
        System.arraycopy(this.lo, 0, na, 0, len);
        this.lo = na;
        na = new char[newsize];
        System.arraycopy(this.hi, 0, na, 0, len);
        this.hi = na;
        na = new char[newsize];
        System.arraycopy(this.eq, 0, na, 0, len);
        this.eq = na;
        na = new char[newsize];
        System.arraycopy(this.sc, 0, na, 0, len);
        this.sc = na;
    }

    public int size() {
        return this.length;
    }

    public TernaryTree clone() {
        TernaryTree t = new TernaryTree();
        t.lo = (char[])this.lo.clone();
        t.hi = (char[])this.hi.clone();
        t.eq = (char[])this.eq.clone();
        t.sc = (char[])this.sc.clone();
        t.kv = this.kv.clone();
        t.root = this.root;
        t.freenode = this.freenode;
        t.length = this.length;
        return t;
    }

    protected void insertBalanced(String[] k, char[] v, int offset, int n) {
        if (n < 1) {
            return;
        }
        int m = n >> 1;
        this.insert(k[m + offset], v[m + offset]);
        this.insertBalanced(k, v, offset, m);
        this.insertBalanced(k, v, offset + m + 1, n - m - 1);
    }

    public void balance() {
        int i = 0;
        int n = this.length;
        String[] k = new String[n];
        char[] v = new char[n];
        Iterator iter = new Iterator();
        while (iter.hasMoreElements()) {
            v[i] = iter.getValue();
            k[i++] = iter.nextElement();
        }
        this.init();
        this.insertBalanced(k, v, 0, n);
    }

    public void trimToSize() {
        this.balance();
        this.redimNodeArrays(this.freenode);
        CharVector kx = new CharVector();
        kx.alloc(1);
        TernaryTree map = new TernaryTree();
        this.compact(kx, map, this.root);
        this.kv = kx;
        this.kv.trimToSize();
    }

    private void compact(CharVector kx, TernaryTree map, char p) {
        if (p == '\u0000') {
            return;
        }
        if (this.sc[p] == '\uffff') {
            int k = map.find(this.kv.getArray(), this.lo[p]);
            if (k < 0) {
                k = kx.alloc(TernaryTree.strlen(this.kv.getArray(), this.lo[p]) + 1);
                TernaryTree.strcpy(kx.getArray(), k, this.kv.getArray(), this.lo[p]);
                map.insert(kx.getArray(), k, (char)k);
            }
            this.lo[p] = (char)k;
        } else {
            this.compact(kx, map, this.lo[p]);
            if (this.sc[p] != '\u0000') {
                this.compact(kx, map, this.eq[p]);
            }
            this.compact(kx, map, this.hi[p]);
        }
    }

    public Enumeration<String> keys() {
        return new Iterator();
    }

    public void printStats(PrintStream out) {
        out.println("Number of keys = " + Integer.toString(this.length));
        out.println("Node count = " + Integer.toString(this.freenode));
        out.println("Key Array length = " + Integer.toString(this.kv.length()));
    }

    public class Iterator
    implements Enumeration<String> {
        int cur = -1;
        String curkey;
        Stack<Item> ns = new Stack();
        StringBuilder ks = new StringBuilder();

        public Iterator() {
            this.rewind();
        }

        public void rewind() {
            this.ns.removeAllElements();
            this.ks.setLength(0);
            this.cur = TernaryTree.this.root;
            this.run();
        }

        @Override
        public String nextElement() {
            String res = new String(this.curkey);
            this.cur = this.up();
            this.run();
            return res;
        }

        public char getValue() {
            if (this.cur >= 0) {
                return TernaryTree.this.eq[this.cur];
            }
            return '\u0000';
        }

        @Override
        public boolean hasMoreElements() {
            return this.cur != -1;
        }

        private int up() {
            Item i = new Item();
            int res = 0;
            if (this.ns.empty()) {
                return -1;
            }
            if (this.cur != 0 && TernaryTree.this.sc[this.cur] == '\u0000') {
                return TernaryTree.this.lo[this.cur];
            }
            boolean climb = true;
            block4: while (climb) {
                i = this.ns.pop();
                i.child = (char)(i.child + '\u0001');
                switch (i.child) {
                    case '\u0001': {
                        if (TernaryTree.this.sc[i.parent] != '\u0000') {
                            res = TernaryTree.this.eq[i.parent];
                            this.ns.push(i.clone());
                            this.ks.append(TernaryTree.this.sc[i.parent]);
                        } else {
                            i.child = (char)(i.child + '\u0001');
                            this.ns.push(i.clone());
                            res = TernaryTree.this.hi[i.parent];
                        }
                        climb = false;
                        continue block4;
                    }
                    case '\u0002': {
                        res = TernaryTree.this.hi[i.parent];
                        this.ns.push(i.clone());
                        if (this.ks.length() > 0) {
                            this.ks.setLength(this.ks.length() - 1);
                        }
                        climb = false;
                        continue block4;
                    }
                }
                if (this.ns.empty()) {
                    return -1;
                }
                climb = true;
            }
            return res;
        }

        private int run() {
            block9: {
                if (this.cur == -1) {
                    return -1;
                }
                boolean leaf = false;
                while (true) {
                    if (this.cur != 0) {
                        if (TernaryTree.this.sc[this.cur] == '\uffff') {
                            leaf = true;
                        } else {
                            this.ns.push(new Item((char)this.cur, '\u0000'));
                            if (TernaryTree.this.sc[this.cur] == '\u0000') {
                                leaf = true;
                            } else {
                                this.cur = TernaryTree.this.lo[this.cur];
                                continue;
                            }
                        }
                    }
                    if (leaf) break block9;
                    this.cur = this.up();
                    if (this.cur == -1) break;
                }
                return -1;
            }
            StringBuilder buf = new StringBuilder(this.ks.toString());
            if (TernaryTree.this.sc[this.cur] == '\uffff') {
                int p = TernaryTree.this.lo[this.cur];
                while (TernaryTree.this.kv.get(p) != '\u0000') {
                    buf.append(TernaryTree.this.kv.get(p++));
                }
            }
            this.curkey = buf.toString();
            return 0;
        }

        private class Item
        implements Cloneable {
            char parent;
            char child;

            public Item() {
                this.parent = '\u0000';
                this.child = '\u0000';
            }

            public Item(char p, char c) {
                this.parent = p;
                this.child = c;
            }

            public Item clone() {
                return new Item(this.parent, this.child);
            }
        }
    }
}

