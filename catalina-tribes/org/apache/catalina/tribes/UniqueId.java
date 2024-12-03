/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes;

import java.io.Serializable;
import org.apache.catalina.tribes.util.Arrays;

public final class UniqueId
implements Serializable {
    private static final long serialVersionUID = 1L;
    final byte[] id;

    public UniqueId() {
        this(null);
    }

    public UniqueId(byte[] id) {
        this.id = id;
    }

    public UniqueId(byte[] id, int offset, int length) {
        this.id = new byte[length];
        System.arraycopy(id, offset, this.id, 0, length);
    }

    public int hashCode() {
        if (this.id == null) {
            return 0;
        }
        return Arrays.hashCode(this.id);
    }

    public boolean equals(Object other) {
        boolean result = other instanceof UniqueId;
        if (result) {
            UniqueId uid = (UniqueId)other;
            result = this.id == null && uid.id == null ? true : (this.id == null && uid.id != null ? false : (this.id != null && uid.id == null ? false : Arrays.equals(this.id, uid.id)));
        }
        return result;
    }

    public byte[] getBytes() {
        return this.id;
    }

    public String toString() {
        return "UniqueId" + Arrays.toString(this.id);
    }
}

