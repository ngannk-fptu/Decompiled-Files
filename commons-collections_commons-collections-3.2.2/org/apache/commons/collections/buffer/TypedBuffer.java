/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.buffer;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.buffer.PredicatedBuffer;
import org.apache.commons.collections.functors.InstanceofPredicate;

public class TypedBuffer {
    public static Buffer decorate(Buffer buffer, Class type) {
        return new PredicatedBuffer(buffer, InstanceofPredicate.getInstance(type));
    }

    protected TypedBuffer() {
    }
}

