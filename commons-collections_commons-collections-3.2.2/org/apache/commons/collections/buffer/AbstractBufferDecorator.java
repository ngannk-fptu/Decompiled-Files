/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.buffer;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.collection.AbstractCollectionDecorator;

public abstract class AbstractBufferDecorator
extends AbstractCollectionDecorator
implements Buffer {
    protected AbstractBufferDecorator() {
    }

    protected AbstractBufferDecorator(Buffer buffer) {
        super(buffer);
    }

    protected Buffer getBuffer() {
        return (Buffer)this.getCollection();
    }

    public Object get() {
        return this.getBuffer().get();
    }

    public Object remove() {
        return this.getBuffer().remove();
    }
}

