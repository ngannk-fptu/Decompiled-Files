/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.values;

import java.util.AbstractList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.xmlbeans.XmlObject;

public class JavaListXmlObject<T extends XmlObject>
extends AbstractList<T> {
    private final Function<Integer, T> getter;
    private final BiConsumer<Integer, T> setter;
    private final Function<Integer, T> adder;
    private final Consumer<Integer> remover;
    private final Supplier<Integer> sizer;

    public JavaListXmlObject(Function<Integer, T> getter, BiConsumer<Integer, T> setter, Function<Integer, T> adder, Consumer<Integer> remover, Supplier<Integer> sizer) {
        this.getter = getter;
        this.setter = setter;
        this.adder = adder;
        this.remover = remover;
        this.sizer = sizer;
    }

    @Override
    public T get(int index) {
        if (this.getter == null) {
            throw new IllegalStateException("XmlBean generated using partial methods - no getter available");
        }
        return (T)((XmlObject)this.getter.apply(index));
    }

    @Override
    public T set(int index, T element) {
        if (this.setter == null) {
            throw new IllegalStateException("XmlBean generated using partial methods - no setter available");
        }
        Object old = this.get(index);
        this.setter.accept(index, (Integer)element);
        return (T)old;
    }

    @Override
    public void add(int index, T t) {
        if (this.adder == null) {
            throw new IllegalStateException("XmlBean generated using partial methods - no add method available");
        }
        ((XmlObject)this.adder.apply(index)).set((XmlObject)t);
    }

    @Override
    public T remove(int index) {
        if (this.remover == null) {
            throw new IllegalStateException("XmlBean generated using partial methods - no remove method available");
        }
        Object old = this.get(index);
        this.remover.accept(index);
        return (T)old;
    }

    @Override
    public int size() {
        if (this.sizer == null) {
            throw new IllegalStateException("XmlBean generated using partial methods - no size-of method available");
        }
        return this.sizer.get();
    }
}

