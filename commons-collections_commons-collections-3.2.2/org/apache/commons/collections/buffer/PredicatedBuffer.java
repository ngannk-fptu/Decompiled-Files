/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.buffer;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.collection.PredicatedCollection;

public class PredicatedBuffer
extends PredicatedCollection
implements Buffer {
    private static final long serialVersionUID = 2307609000539943581L;

    public static Buffer decorate(Buffer buffer, Predicate predicate) {
        return new PredicatedBuffer(buffer, predicate);
    }

    protected PredicatedBuffer(Buffer buffer, Predicate predicate) {
        super(buffer, predicate);
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

