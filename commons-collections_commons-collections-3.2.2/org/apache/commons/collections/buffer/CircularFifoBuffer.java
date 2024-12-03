/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.buffer;

import java.util.Collection;
import org.apache.commons.collections.buffer.BoundedFifoBuffer;

public class CircularFifoBuffer
extends BoundedFifoBuffer {
    private static final long serialVersionUID = -8423413834657610406L;

    public CircularFifoBuffer() {
        super(32);
    }

    public CircularFifoBuffer(int size) {
        super(size);
    }

    public CircularFifoBuffer(Collection coll) {
        super(coll);
    }

    public boolean add(Object element) {
        if (this.isFull()) {
            this.remove();
        }
        return super.add(element);
    }
}

