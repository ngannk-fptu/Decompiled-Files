/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.geom;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class RectListManager
implements Collection {
    Rectangle[] rects = null;
    int size = 0;
    Rectangle bounds = null;
    public static Comparator comparator = new RectXComparator();

    public void dump() {
        System.err.println("RLM: " + this + " Sz: " + this.size);
        System.err.println("Bounds: " + this.getBounds());
        for (int i = 0; i < this.size; ++i) {
            Rectangle r = this.rects[i];
            System.err.println("  [" + r.x + ", " + r.y + ", " + r.width + ", " + r.height + ']');
        }
    }

    public RectListManager(Collection rects) {
        this.rects = new Rectangle[rects.size()];
        Iterator i = rects.iterator();
        int j = 0;
        while (i.hasNext()) {
            this.rects[j++] = (Rectangle)i.next();
        }
        this.size = this.rects.length;
        Arrays.sort(this.rects, comparator);
    }

    public RectListManager(Rectangle[] rects) {
        this(rects, 0, rects.length);
    }

    public RectListManager(Rectangle[] rects, int off, int sz) {
        this.size = sz;
        this.rects = new Rectangle[sz];
        System.arraycopy(rects, off, this.rects, 0, sz);
        Arrays.sort(this.rects, comparator);
    }

    public RectListManager(RectListManager rlm) {
        this(rlm.rects);
    }

    public RectListManager(Rectangle rect) {
        this();
        this.add(rect);
    }

    public RectListManager() {
        this.rects = new Rectangle[10];
        this.size = 0;
    }

    public RectListManager(int capacity) {
        this.rects = new Rectangle[capacity];
    }

    public Rectangle getBounds() {
        if (this.bounds != null) {
            return this.bounds;
        }
        if (this.size == 0) {
            return null;
        }
        this.bounds = new Rectangle(this.rects[0]);
        for (int i = 1; i < this.size; ++i) {
            Rectangle r = this.rects[i];
            if (r.x < this.bounds.x) {
                this.bounds.width = this.bounds.x + this.bounds.width - r.x;
                this.bounds.x = r.x;
            }
            if (r.y < this.bounds.y) {
                this.bounds.height = this.bounds.y + this.bounds.height - r.y;
                this.bounds.y = r.y;
            }
            if (r.x + r.width > this.bounds.x + this.bounds.width) {
                this.bounds.width = r.x + r.width - this.bounds.x;
            }
            if (r.y + r.height <= this.bounds.y + this.bounds.height) continue;
            this.bounds.height = r.y + r.height - this.bounds.y;
        }
        return this.bounds;
    }

    public Object clone() throws CloneNotSupportedException {
        return this.copy();
    }

    public RectListManager copy() {
        return new RectListManager(this.rects);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public void clear() {
        Arrays.fill(this.rects, null);
        this.size = 0;
        this.bounds = null;
    }

    @Override
    public Iterator iterator() {
        return new RLMIterator();
    }

    public ListIterator listIterator() {
        return new RLMIterator();
    }

    @Override
    public Object[] toArray() {
        Object[] ret = new Rectangle[this.size];
        System.arraycopy(this.rects, 0, ret, 0, this.size);
        return ret;
    }

    public Object[] toArray(Object[] a) {
        Class<?> t = a.getClass().getComponentType();
        if (t != Object.class && t != Rectangle.class) {
            Arrays.fill(a, null);
            return a;
        }
        if (a.length < this.size) {
            a = new Rectangle[this.size];
        }
        System.arraycopy(this.rects, 0, a, 0, this.size);
        Arrays.fill(a, this.size, a.length, null);
        return a;
    }

    public boolean add(Object o) {
        this.add((Rectangle)o);
        return true;
    }

    public void add(Rectangle rect) {
        this.add(rect, 0, this.size - 1);
    }

    protected void add(Rectangle rect, int l, int r) {
        this.ensureCapacity(this.size + 1);
        int idx = l;
        while (l <= r) {
            for (idx = (l + r) / 2; this.rects[idx] == null && idx < r; ++idx) {
            }
            if (this.rects[idx] == null) {
                r = (l + r) / 2;
                idx = (l + r) / 2;
                if (l > r) {
                    idx = l;
                }
                while (this.rects[idx] == null && idx > l) {
                    --idx;
                }
                if (this.rects[idx] == null) {
                    this.rects[idx] = rect;
                    return;
                }
            }
            if (rect.x == this.rects[idx].x) break;
            if (rect.x < this.rects[idx].x) {
                if (idx == 0 || this.rects[idx - 1] != null && rect.x >= this.rects[idx - 1].x) break;
                r = idx - 1;
                continue;
            }
            if (idx == this.size - 1) {
                ++idx;
                break;
            }
            if (this.rects[idx + 1] != null && rect.x <= this.rects[idx + 1].x) {
                ++idx;
                break;
            }
            l = idx + 1;
        }
        if (idx < this.size) {
            System.arraycopy(this.rects, idx, this.rects, idx + 1, this.size - idx);
        }
        this.rects[idx] = rect;
        ++this.size;
        this.bounds = null;
    }

    public boolean addAll(Collection c) {
        if (c instanceof RectListManager) {
            this.add((RectListManager)c);
        } else {
            this.add(new RectListManager(c));
        }
        return c.size() != 0;
    }

    @Override
    public boolean contains(Object o) {
        int i;
        Rectangle rect = (Rectangle)o;
        int l = 0;
        int r = this.size - 1;
        int idx = 0;
        while (l <= r) {
            idx = l + r >>> 1;
            if (rect.x == this.rects[idx].x) break;
            if (rect.x < this.rects[idx].x) {
                if (idx == 0 || rect.x >= this.rects[idx - 1].x) break;
                r = idx - 1;
                continue;
            }
            if (idx == this.size - 1) {
                ++idx;
                break;
            }
            if (rect.x <= this.rects[idx + 1].x) {
                ++idx;
                break;
            }
            l = idx + 1;
        }
        if (this.rects[idx].x != rect.x) {
            return false;
        }
        for (i = idx; i >= 0; --i) {
            if (this.rects[idx].equals(rect)) {
                return true;
            }
            if (this.rects[idx].x != rect.x) break;
        }
        for (i = idx + 1; i < this.size; ++i) {
            if (this.rects[idx].equals(rect)) {
                return true;
            }
            if (this.rects[idx].x != rect.x) break;
        }
        return false;
    }

    public boolean containsAll(Collection c) {
        if (c instanceof RectListManager) {
            return this.containsAll((RectListManager)c);
        }
        return this.containsAll(new RectListManager(c));
    }

    public boolean containsAll(RectListManager rlm) {
        int xChange = 0;
        int i = 0;
        for (int j = 0; j < rlm.size; ++j) {
            i = xChange;
            while (this.rects[i].x < rlm.rects[j].x) {
                if (++i != this.size) continue;
                return false;
            }
            xChange = i;
            int x = this.rects[i].x;
            while (!rlm.rects[j].equals(this.rects[i])) {
                if (++i == this.size) {
                    return false;
                }
                if (x == this.rects[i].x) continue;
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean remove(Object o) {
        return this.remove((Rectangle)o);
    }

    public boolean remove(Rectangle rect) {
        int i;
        int l = 0;
        int r = this.size - 1;
        int idx = 0;
        while (l <= r) {
            idx = l + r >>> 1;
            if (rect.x == this.rects[idx].x) break;
            if (rect.x < this.rects[idx].x) {
                if (idx == 0 || rect.x >= this.rects[idx - 1].x) break;
                r = idx - 1;
                continue;
            }
            if (idx == this.size - 1) {
                ++idx;
                break;
            }
            if (rect.x <= this.rects[idx + 1].x) {
                ++idx;
                break;
            }
            l = idx + 1;
        }
        if (this.rects[idx].x != rect.x) {
            return false;
        }
        for (i = idx; i >= 0; --i) {
            if (this.rects[idx].equals(rect)) {
                System.arraycopy(this.rects, idx + 1, this.rects, idx, this.size - idx);
                --this.size;
                this.bounds = null;
                return true;
            }
            if (this.rects[idx].x != rect.x) break;
        }
        for (i = idx + 1; i < this.size; ++i) {
            if (this.rects[idx].equals(rect)) {
                System.arraycopy(this.rects, idx + 1, this.rects, idx, this.size - idx);
                --this.size;
                this.bounds = null;
                return true;
            }
            if (this.rects[idx].x != rect.x) break;
        }
        return false;
    }

    public boolean removeAll(Collection c) {
        if (c instanceof RectListManager) {
            return this.removeAll((RectListManager)c);
        }
        return this.removeAll(new RectListManager(c));
    }

    public boolean removeAll(RectListManager rlm) {
        int j;
        int xChange = 0;
        boolean ret = false;
        int i = 0;
        block0: for (j = 0; j < rlm.size; ++j) {
            i = xChange;
            while ((this.rects[i] == null || this.rects[i].x < rlm.rects[j].x) && ++i != this.size) {
            }
            if (i == this.size) break;
            xChange = i;
            int x = this.rects[i].x;
            while (true) {
                if (this.rects[i] == null) {
                    if (++i != this.size) continue;
                    continue block0;
                }
                if (rlm.rects[j].equals(this.rects[i])) {
                    this.rects[i] = null;
                    ret = true;
                }
                if (++i == this.size || x != this.rects[i].x) continue block0;
            }
        }
        if (ret) {
            j = 0;
            for (i = 0; i < this.size; ++i) {
                if (this.rects[i] == null) continue;
                this.rects[j++] = this.rects[i];
            }
            this.size = j;
            this.bounds = null;
        }
        return ret;
    }

    public boolean retainAll(Collection c) {
        if (c instanceof RectListManager) {
            return this.retainAll((RectListManager)c);
        }
        return this.retainAll(new RectListManager(c));
    }

    public boolean retainAll(RectListManager rlm) {
        int j;
        int xChange = 0;
        boolean ret = false;
        int i = 0;
        block0: for (j = 0; j < this.size; ++j) {
            i = xChange;
            while (rlm.rects[i].x < this.rects[j].x && ++i != rlm.size) {
            }
            if (i == rlm.size) {
                ret = true;
                for (int k = j; k < this.size; ++k) {
                    this.rects[k] = null;
                }
                this.size = j;
                break;
            }
            xChange = i;
            int x = rlm.rects[i].x;
            while (!this.rects[j].equals(rlm.rects[i])) {
                if (++i != rlm.size && x == rlm.rects[i].x) continue;
                this.rects[j] = null;
                ret = true;
                continue block0;
            }
        }
        if (ret) {
            j = 0;
            for (i = 0; i < this.size; ++i) {
                if (this.rects[i] == null) continue;
                this.rects[j++] = this.rects[i];
            }
            this.size = j;
            this.bounds = null;
        }
        return ret;
    }

    public void add(RectListManager rlm) {
        if (rlm.size == 0) {
            return;
        }
        Rectangle[] dst = this.rects;
        if (this.rects.length < this.size + rlm.size) {
            dst = new Rectangle[this.size + rlm.size];
        }
        if (this.size == 0) {
            System.arraycopy(rlm.rects, 0, dst, this.size, rlm.size);
            this.size = rlm.size;
            this.bounds = null;
            return;
        }
        Rectangle[] src1 = rlm.rects;
        int src1Sz = rlm.size;
        int src1I = src1Sz - 1;
        Rectangle[] src2 = this.rects;
        int src2Sz = this.size;
        int src2I = src2Sz - 1;
        int x1 = src1[src1I].x;
        int x2 = src2[src2I].x;
        for (int dstI = this.size + rlm.size - 1; dstI >= 0; --dstI) {
            if (x1 <= x2) {
                dst[dstI] = src2[src2I];
                if (src2I == 0) {
                    System.arraycopy(src1, 0, dst, 0, src1I + 1);
                    break;
                }
                x2 = src2[--src2I].x;
                continue;
            }
            dst[dstI] = src1[src1I];
            if (src1I == 0) {
                System.arraycopy(src2, 0, dst, 0, src2I + 1);
                break;
            }
            x1 = src1[--src1I].x;
        }
        this.rects = dst;
        this.size += rlm.size;
        this.bounds = null;
    }

    public void mergeRects(int overhead, int lineOverhead) {
        int j;
        Rectangle r;
        int i;
        if (this.size == 0) {
            return;
        }
        Rectangle[] splits = new Rectangle[4];
        for (i = 0; i < this.size; ++i) {
            r = this.rects[i];
            if (r == null) continue;
            int cost1 = overhead + r.height * lineOverhead + r.height * r.width;
            block1: do {
                int maxX = r.x + r.width + overhead / r.height;
                for (j = i + 1; j < this.size; ++j) {
                    Rectangle cr = this.rects[j];
                    if (cr == null || cr == r) continue;
                    if (cr.x >= maxX) {
                        j = this.size;
                        continue block1;
                    }
                    int cost2 = overhead + cr.height * lineOverhead + cr.height * cr.width;
                    Rectangle mr = r.union(cr);
                    int cost3 = overhead + mr.height * lineOverhead + mr.height * mr.width;
                    if (cost3 <= cost1 + cost2) {
                        r = this.rects[i] = mr;
                        this.rects[j] = null;
                        cost1 = cost3;
                        j = -1;
                        continue block1;
                    }
                    if (!r.intersects(cr)) continue;
                    this.splitRect(cr, r, splits);
                    int splitCost = 0;
                    int l = 0;
                    for (int k = 0; k < 4; ++k) {
                        if (splits[k] == null) continue;
                        Rectangle sr = splits[k];
                        if (k < 3) {
                            splits[l++] = sr;
                        }
                        splitCost += overhead + sr.height * lineOverhead + sr.height * sr.width;
                    }
                    if (splitCost >= cost2) continue;
                    if (l == 0) {
                        this.rects[j] = null;
                        if (splits[3] == null) continue;
                        this.add(splits[3], j, this.size - 1);
                        continue;
                    }
                    this.rects[j] = splits[0];
                    if (l > 1) {
                        this.insertRects(splits, 1, j + 1, l - 1);
                    }
                    if (splits[3] == null) continue;
                    this.add(splits[3], j, this.size - 1);
                }
            } while (j != this.size);
        }
        j = 0;
        float area = 0.0f;
        for (i = 0; i < this.size; ++i) {
            if (this.rects[i] == null) continue;
            r = this.rects[i];
            this.rects[j++] = r;
            area += (float)(overhead + r.height * lineOverhead + r.height * r.width);
        }
        this.size = j;
        this.bounds = null;
        r = this.getBounds();
        if (r == null) {
            return;
        }
        if ((float)(overhead + r.height * lineOverhead + r.height * r.width) < area) {
            this.rects[0] = r;
            this.size = 1;
        }
    }

    public void subtract(RectListManager rlm, int overhead, int lineOverhead) {
        int jMin = 0;
        Rectangle[] splits = new Rectangle[4];
        block0: for (int i = 0; i < this.size; ++i) {
            Rectangle r = this.rects[i];
            int cost = overhead + r.height * lineOverhead + r.height * r.width;
            for (int j = jMin; j < rlm.size; ++j) {
                Rectangle sr = rlm.rects[j];
                if (sr.x + sr.width < r.x) {
                    if (j != jMin) continue;
                    ++jMin;
                    continue;
                }
                if (sr.x > r.x + r.width) continue block0;
                if (!r.intersects(sr)) continue;
                this.splitRect(r, sr, splits);
                int splitCost = 0;
                for (int k = 0; k < 4; ++k) {
                    Rectangle tmpR = splits[k];
                    if (tmpR == null) continue;
                    splitCost += overhead + tmpR.height * lineOverhead + tmpR.height * tmpR.width;
                }
                if (splitCost >= cost) continue;
                int l = 0;
                for (int k = 0; k < 3; ++k) {
                    if (splits[k] == null) continue;
                    splits[l++] = splits[k];
                }
                if (l == 0) {
                    this.rects[i].width = 0;
                    if (splits[3] == null) continue block0;
                    this.add(splits[3], i, this.size - 1);
                    continue block0;
                }
                this.rects[i] = r = splits[0];
                cost = overhead + r.height * lineOverhead + r.height * r.width;
                if (l > 1) {
                    this.insertRects(splits, 1, i + 1, l - 1);
                }
                if (splits[3] == null) continue;
                this.add(splits[3], i + l, this.size - 1);
            }
        }
        int j = 0;
        for (int i = 0; i < this.size; ++i) {
            if (this.rects[i].width == 0) {
                this.rects[i] = null;
                continue;
            }
            this.rects[j++] = this.rects[i];
        }
        this.size = j;
        this.bounds = null;
    }

    protected void splitRect(Rectangle r, Rectangle sr, Rectangle[] splits) {
        int rx0 = r.x;
        int rx1 = rx0 + r.width - 1;
        int ry0 = r.y;
        int ry1 = ry0 + r.height - 1;
        int srx0 = sr.x;
        int srx1 = srx0 + sr.width - 1;
        int sry0 = sr.y;
        int sry1 = sry0 + sr.height - 1;
        if (ry0 < sry0 && ry1 >= sry0) {
            splits[0] = new Rectangle(rx0, ry0, r.width, sry0 - ry0);
            ry0 = sry0;
        } else {
            splits[0] = null;
        }
        if (ry0 <= sry1 && ry1 > sry1) {
            splits[1] = new Rectangle(rx0, sry1 + 1, r.width, ry1 - sry1);
            ry1 = sry1;
        } else {
            splits[1] = null;
        }
        splits[2] = rx0 < srx0 && rx1 >= srx0 ? new Rectangle(rx0, ry0, srx0 - rx0, ry1 - ry0 + 1) : null;
        splits[3] = rx0 <= srx1 && rx1 > srx1 ? new Rectangle(srx1 + 1, ry0, rx1 - srx1, ry1 - ry0 + 1) : null;
    }

    protected void insertRects(Rectangle[] rects, int srcPos, int dstPos, int len) {
        if (len == 0) {
            return;
        }
        this.ensureCapacity(this.size + len);
        for (int i = this.size - 1; i >= dstPos; --i) {
            this.rects[i + len] = this.rects[i];
        }
        System.arraycopy(rects, srcPos, this.rects, dstPos, len);
        this.size += len;
    }

    public void ensureCapacity(int sz) {
        int nSz;
        if (sz <= this.rects.length) {
            return;
        }
        for (nSz = this.rects.length + (this.rects.length >> 1) + 1; nSz < sz; nSz += (nSz >> 1) + 1) {
        }
        Rectangle[] nRects = new Rectangle[nSz];
        System.arraycopy(this.rects, 0, nRects, 0, this.size);
        this.rects = nRects;
    }

    private class RLMIterator
    implements ListIterator {
        int idx = 0;
        boolean removeOk = false;
        boolean forward = true;

        RLMIterator() {
        }

        @Override
        public boolean hasNext() {
            return this.idx < RectListManager.this.size;
        }

        @Override
        public int nextIndex() {
            return this.idx;
        }

        @Override
        public Object next() {
            if (this.idx >= RectListManager.this.size) {
                throw new NoSuchElementException("No Next Element");
            }
            this.forward = true;
            this.removeOk = true;
            return RectListManager.this.rects[this.idx++];
        }

        @Override
        public boolean hasPrevious() {
            return this.idx > 0;
        }

        @Override
        public int previousIndex() {
            return this.idx - 1;
        }

        public Object previous() {
            if (this.idx <= 0) {
                throw new NoSuchElementException("No Previous Element");
            }
            this.forward = false;
            this.removeOk = true;
            return RectListManager.this.rects[--this.idx];
        }

        @Override
        public void remove() {
            if (!this.removeOk) {
                throw new IllegalStateException("remove can only be called directly after next/previous");
            }
            if (this.forward) {
                --this.idx;
            }
            if (this.idx != RectListManager.this.size - 1) {
                System.arraycopy(RectListManager.this.rects, this.idx + 1, RectListManager.this.rects, this.idx, RectListManager.this.size - (this.idx + 1));
            }
            --RectListManager.this.size;
            RectListManager.this.rects[RectListManager.this.size] = null;
            this.removeOk = false;
        }

        public void set(Object o) {
            Rectangle r = (Rectangle)o;
            if (!this.removeOk) {
                throw new IllegalStateException("set can only be called directly after next/previous");
            }
            if (this.forward) {
                --this.idx;
            }
            if (this.idx + 1 < RectListManager.this.size && RectListManager.this.rects[this.idx + 1].x < r.x) {
                throw new UnsupportedOperationException("RectListManager entries must be sorted");
            }
            if (this.idx >= 0 && RectListManager.this.rects[this.idx - 1].x > r.x) {
                throw new UnsupportedOperationException("RectListManager entries must be sorted");
            }
            RectListManager.this.rects[this.idx] = r;
            this.removeOk = false;
        }

        public void add(Object o) {
            Rectangle r = (Rectangle)o;
            if (this.idx < RectListManager.this.size && RectListManager.this.rects[this.idx].x < r.x) {
                throw new UnsupportedOperationException("RectListManager entries must be sorted");
            }
            if (this.idx != 0 && RectListManager.this.rects[this.idx - 1].x > r.x) {
                throw new UnsupportedOperationException("RectListManager entries must be sorted");
            }
            RectListManager.this.ensureCapacity(RectListManager.this.size + 1);
            if (this.idx != RectListManager.this.size) {
                System.arraycopy(RectListManager.this.rects, this.idx, RectListManager.this.rects, this.idx + 1, RectListManager.this.size - this.idx);
            }
            RectListManager.this.rects[this.idx] = r;
            ++this.idx;
            this.removeOk = false;
        }
    }

    private static class RectXComparator
    implements Comparator,
    Serializable {
        RectXComparator() {
        }

        public final int compare(Object o1, Object o2) {
            return ((Rectangle)o1).x - ((Rectangle)o2).x;
        }
    }
}

