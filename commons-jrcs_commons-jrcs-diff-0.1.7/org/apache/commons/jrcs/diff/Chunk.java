/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.jrcs.diff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.jrcs.util.ToString;

public class Chunk
extends ToString {
    protected int anchor;
    protected int count;
    protected List chunk;

    public Chunk(int pos, int count) {
        this.anchor = pos;
        this.count = count >= 0 ? count : 0;
    }

    public Chunk(Object[] iseq, int pos, int count) {
        this(pos, count);
        this.chunk = Chunk.slice(iseq, pos, count);
    }

    public Chunk(Object[] iseq, int pos, int count, int offset) {
        this(offset, count);
        this.chunk = Chunk.slice(iseq, pos, count);
    }

    public Chunk(List iseq, int pos, int count) {
        this(pos, count);
        this.chunk = Chunk.slice(iseq, pos, count);
    }

    public Chunk(List iseq, int pos, int count, int offset) {
        this(offset, count);
        this.chunk = Chunk.slice(iseq, pos, count);
    }

    public int anchor() {
        return this.anchor;
    }

    public int size() {
        return this.count;
    }

    public int first() {
        return this.anchor();
    }

    public int last() {
        return this.anchor() + this.size() - 1;
    }

    public int rcsfrom() {
        return this.anchor + 1;
    }

    public int rcsto() {
        return this.anchor + this.count;
    }

    public List chunk() {
        return this.chunk;
    }

    public boolean verify(List target) {
        if (this.chunk == null) {
            return true;
        }
        if (this.last() > target.size()) {
            return false;
        }
        int i = 0;
        while (i < this.count) {
            if (!target.get(this.anchor + i).equals(this.chunk.get(i))) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public void applyDelete(List target) {
        int i = this.last();
        while (i >= this.first()) {
            target.remove(i);
            --i;
        }
    }

    public void applyAdd(int start, List target) {
        Iterator i = this.chunk.iterator();
        while (i.hasNext()) {
            target.add(start++, i.next());
        }
    }

    public void toString(StringBuffer s) {
        this.toString(s, "", "");
    }

    public StringBuffer toString(StringBuffer s, String prefix, String postfix) {
        if (this.chunk != null) {
            Iterator i = this.chunk.iterator();
            while (i.hasNext()) {
                s.append(prefix);
                s.append(i.next());
                s.append(postfix);
            }
        }
        return s;
    }

    public static List slice(List seq, int pos, int count) {
        if (count <= 0) {
            return new ArrayList(seq.subList(pos, pos));
        }
        return new ArrayList(seq.subList(pos, pos + count));
    }

    public static List slice(Object[] seq, int pos, int count) {
        return Chunk.slice(Arrays.asList(seq), pos, count);
    }

    public String rangeString() {
        StringBuffer result = new StringBuffer();
        this.rangeString(result);
        return result.toString();
    }

    public void rangeString(StringBuffer s) {
        this.rangeString(s, ",");
    }

    public void rangeString(StringBuffer s, String separ) {
        if (this.size() <= 1) {
            s.append(Integer.toString(this.rcsfrom()));
        } else {
            s.append(Integer.toString(this.rcsfrom()));
            s.append(separ);
            s.append(Integer.toString(this.rcsto()));
        }
    }
}

