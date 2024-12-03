/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import org.apache.poi.hwpf.model.PropertyNode;

@Deprecated
public abstract class BytePropertyNode<T extends BytePropertyNode<T>>
extends PropertyNode<T> {
    private final int startBytes;
    private final int endBytes;

    protected BytePropertyNode(BytePropertyNode other) {
        super(other);
        this.startBytes = other.startBytes;
        this.endBytes = other.endBytes;
    }

    protected BytePropertyNode(int charStart, int charEnd, Object buf) {
        super(charStart, charEnd, buf);
        if (charStart > charEnd) {
            throw new IllegalArgumentException("charStart (" + charStart + ") > charEnd (" + charEnd + ")");
        }
        this.startBytes = -1;
        this.endBytes = -1;
    }

    @Deprecated
    public int getStartBytes() {
        return this.startBytes;
    }

    @Deprecated
    public int getEndBytes() {
        return this.endBytes;
    }
}

