/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Internal;

@Internal
public abstract class PropertyNode<T extends PropertyNode<T>>
implements Comparable<T>,
Duplicatable {
    public static final Comparator<PropertyNode<?>> EndComparator = Comparator.comparingInt(PropertyNode::getEnd);
    public static final Comparator<PropertyNode<?>> StartComparator = Comparator.comparingInt(PropertyNode::getStart);
    private static final Logger LOG = LogManager.getLogger(PropertyNode.class);
    protected Object _buf;
    private int _cpStart;
    private int _cpEnd;

    protected PropertyNode(PropertyNode<T> other) {
        this._buf = other._buf;
        this._cpStart = other._cpStart;
        this._cpEnd = other._cpEnd;
    }

    protected PropertyNode(int fcStart, int fcEnd, Object buf) {
        this._cpStart = fcStart;
        this._cpEnd = fcEnd;
        this._buf = buf;
        if (this._cpStart < 0) {
            LOG.atWarn().log("A property claimed to start before zero, at {}! Resetting it to zero, and hoping for the best", (Object)Unbox.box(this._cpStart));
            this._cpStart = 0;
        }
        if (this._cpEnd < this._cpStart) {
            LOG.atWarn().log("A property claimed to end ({}) before start! Resetting end to start, and hoping for the best", (Object)Unbox.box(this._cpEnd));
            this._cpEnd = this._cpStart;
        }
    }

    public int getStart() {
        return this._cpStart;
    }

    public void setStart(int start) {
        this._cpStart = start;
    }

    public int getEnd() {
        return this._cpEnd;
    }

    public void setEnd(int end) {
        this._cpEnd = end;
    }

    public void adjustForDelete(int start, int length) {
        int end = start + length;
        if (this._cpEnd > start) {
            if (this._cpStart < end) {
                this._cpEnd = end >= this._cpEnd ? start : this._cpEnd - length;
                this._cpStart = Math.min(start, this._cpStart);
            } else {
                this._cpEnd -= length;
                this._cpStart -= length;
            }
        }
    }

    protected boolean limitsAreEqual(Object o) {
        return ((PropertyNode)o).getStart() == this._cpStart && ((PropertyNode)o).getEnd() == this._cpEnd;
    }

    public int hashCode() {
        return Objects.hash(this._cpStart, this._buf);
    }

    public boolean equals(Object o) {
        if (!(o instanceof PropertyNode)) {
            return false;
        }
        if (this.limitsAreEqual(o)) {
            Object testBuf = ((PropertyNode)o)._buf;
            if (testBuf instanceof byte[] && this._buf instanceof byte[]) {
                return Arrays.equals((byte[])testBuf, (byte[])this._buf);
            }
            return this._buf.equals(testBuf);
        }
        return false;
    }

    @Override
    public abstract PropertyNode<?> copy();

    @Override
    public int compareTo(T o) {
        return Integer.compare(this._cpEnd, ((PropertyNode)o).getEnd());
    }
}

