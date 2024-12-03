/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.util;

import java.io.Serializable;

public class Flags
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 8481587558770237995L;
    private long flags = 0L;

    public Flags() {
    }

    public Flags(long flags) {
        this.flags = flags;
    }

    public long getFlags() {
        return this.flags;
    }

    public boolean isOn(long flag) {
        return (this.flags & flag) == flag;
    }

    public boolean isOff(long flag) {
        return (this.flags & flag) == 0L;
    }

    public void turnOn(long flag) {
        this.flags |= flag;
    }

    public void turnOff(long flag) {
        this.flags &= flag ^ 0xFFFFFFFFFFFFFFFFL;
    }

    public void turnOffAll() {
        this.flags = 0L;
    }

    public void clear() {
        this.flags = 0L;
    }

    public void turnOnAll() {
        this.flags = -1L;
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException("Couldn't clone Flags object.");
        }
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Flags)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        Flags f = (Flags)obj;
        return this.flags == f.flags;
    }

    public int hashCode() {
        return (int)this.flags;
    }

    public String toString() {
        StringBuilder bin = new StringBuilder(Long.toBinaryString(this.flags));
        for (int i = 64 - bin.length(); i > 0; --i) {
            bin.insert(0, "0");
        }
        return bin.toString();
    }
}

