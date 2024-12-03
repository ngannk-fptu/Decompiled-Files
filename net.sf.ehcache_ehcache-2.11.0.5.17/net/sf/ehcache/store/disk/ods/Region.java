/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store.disk.ods;

import net.sf.ehcache.store.disk.ods.AATreeSet;

public class Region
extends AATreeSet.AbstractTreeNode<Comparable>
implements Comparable<Comparable> {
    private long start;
    private long end;
    private long contiguous;

    public Region(long value) {
        this(value, value);
    }

    public Region(long start, long end) {
        this.start = start;
        this.end = end;
        this.updateContiguous();
    }

    public Region(Region r) {
        this(r.start(), r.end());
    }

    public long contiguous() {
        if (this.getLeft().getPayload() == null && this.getRight().getPayload() == null) {
            return this.size();
        }
        return this.contiguous;
    }

    private void updateContiguous() {
        Region left = (Region)this.getLeft().getPayload();
        Region right = (Region)this.getRight().getPayload();
        long leftContiguous = left == null ? 0L : left.contiguous();
        long rightContiguous = right == null ? 0L : right.contiguous();
        this.contiguous = Math.max(this.size(), Math.max(leftContiguous, rightContiguous));
    }

    @Override
    public void setLeft(AATreeSet.Node<Comparable> l) {
        super.setLeft(l);
        this.updateContiguous();
    }

    @Override
    public void setRight(AATreeSet.Node<Comparable> r) {
        super.setRight(r);
        this.updateContiguous();
    }

    public String toString() {
        return "Range(" + this.start + "," + this.end + ") contiguous:" + this.contiguous();
    }

    public long size() {
        return this.isNull() ? 0L : this.end - this.start + 1L;
    }

    protected boolean isNull() {
        return this.start > this.end;
    }

    protected Region remove(Region r) throws IllegalArgumentException {
        if (r.start < this.start || r.end > this.end) {
            throw new IllegalArgumentException("Ranges : Illegal value passed to remove : " + this + " remove called for : " + r);
        }
        if (this.start == r.start) {
            this.start = r.end + 1L;
            this.updateContiguous();
            return null;
        }
        if (this.end == r.end) {
            this.end = r.start - 1L;
            this.updateContiguous();
            return null;
        }
        Region newRegion = new Region(r.end + 1L, this.end);
        this.end = r.start - 1L;
        this.updateContiguous();
        return newRegion;
    }

    protected void merge(Region r) throws IllegalArgumentException {
        if (this.start == r.end + 1L) {
            this.start = r.start;
        } else if (this.end == r.start - 1L) {
            this.end = r.end;
        } else {
            throw new IllegalArgumentException("Ranges : Merge called on non contiguous values : [this]:" + this + " and " + r);
        }
        this.updateContiguous();
    }

    @Override
    public int compareTo(Comparable other) {
        if (other instanceof Region) {
            return this.compareTo((Region)other);
        }
        if (other instanceof Long) {
            return this.compareTo((Long)other);
        }
        throw new AssertionError((Object)("Unusual Type " + other.getClass()));
    }

    @Override
    private int compareTo(Region r) {
        if (this.start > r.start || this.end > r.end) {
            return 1;
        }
        if (this.start < r.start || this.end < r.end) {
            return -1;
        }
        return 0;
    }

    @Override
    private int compareTo(Long l) {
        if (l > this.end) {
            return -1;
        }
        if (l < this.start) {
            return 1;
        }
        return 0;
    }

    @Override
    public void swapPayload(AATreeSet.Node<Comparable> other) {
        if (!(other instanceof Region)) {
            throw new AssertionError();
        }
        Region r = (Region)other;
        long temp = this.start;
        this.start = r.start;
        r.start = temp;
        temp = this.end;
        this.end = r.end;
        r.end = temp;
        this.updateContiguous();
    }

    @Override
    public Region getPayload() {
        return this;
    }

    public long start() {
        return this.start;
    }

    public long end() {
        return this.end;
    }
}

