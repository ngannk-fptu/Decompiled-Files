/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.buf;

import java.io.Serializable;
import org.apache.tomcat.util.res.StringManager;

public abstract class AbstractChunk
implements Cloneable,
Serializable {
    private static final long serialVersionUID = 1L;
    protected static final StringManager sm = StringManager.getManager(AbstractChunk.class);
    public static final int ARRAY_MAX_SIZE = 0x7FFFFFF7;
    private int hashCode = 0;
    protected boolean hasHashCode = false;
    protected boolean isSet;
    private int limit = -1;
    protected int start;
    protected int end;

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return this.limit;
    }

    protected int getLimitInternal() {
        if (this.limit > 0) {
            return this.limit;
        }
        return 0x7FFFFFF7;
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

    public void setEnd(int i) {
        this.end = i;
    }

    public int getOffset() {
        return this.start;
    }

    public void setOffset(int off) {
        if (this.end < off) {
            this.end = off;
        }
        this.start = off;
    }

    public int getLength() {
        return this.end - this.start;
    }

    public boolean isNull() {
        if (this.end > 0) {
            return false;
        }
        return !this.isSet;
    }

    public int indexOf(String src, int srcOff, int srcLen, int myOff) {
        char first = src.charAt(srcOff);
        int srcEnd = srcOff + srcLen;
        block0: for (int i = myOff + this.start; i <= this.end - srcLen; ++i) {
            if (this.getBufferElement(i) != first) continue;
            int myPos = i + 1;
            int srcPos = srcOff + 1;
            while (srcPos < srcEnd) {
                if (this.getBufferElement(myPos++) == src.charAt(srcPos++)) continue;
                continue block0;
            }
            return i - this.start;
        }
        return -1;
    }

    public void recycle() {
        this.hasHashCode = false;
        this.isSet = false;
        this.start = 0;
        this.end = 0;
    }

    public int hashCode() {
        if (this.hasHashCode) {
            return this.hashCode;
        }
        int code = 0;
        this.hashCode = code = this.hash();
        this.hasHashCode = true;
        return code;
    }

    public int hash() {
        int code = 0;
        for (int i = this.start; i < this.end; ++i) {
            code = code * 37 + this.getBufferElement(i);
        }
        return code;
    }

    protected abstract int getBufferElement(int var1);
}

