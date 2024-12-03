/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import org.apache.poi.hwpf.model.PropertyNode;
import org.apache.poi.util.Internal;

@Internal
public final class GenericPropertyNode
extends PropertyNode<GenericPropertyNode> {
    public GenericPropertyNode(GenericPropertyNode other) {
        super(other);
    }

    public GenericPropertyNode(int start, int end, byte[] buf) {
        super(start, end, buf);
    }

    public byte[] getBytes() {
        return (byte[])this._buf;
    }

    public String toString() {
        return "GenericPropertyNode [" + this.getStart() + "; " + this.getEnd() + ") " + (this.getBytes() != null ? this.getBytes().length + " byte(s)" : "null");
    }

    @Override
    public GenericPropertyNode copy() {
        return new GenericPropertyNode(this);
    }
}

