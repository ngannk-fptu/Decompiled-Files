/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.core.ReferenceByIdMarshaller;

public class SequenceGenerator
implements ReferenceByIdMarshaller.IDGenerator {
    private int counter;

    public SequenceGenerator(int startsAt) {
        this.counter = startsAt;
    }

    public String next(Object item) {
        return String.valueOf(this.counter++);
    }
}

