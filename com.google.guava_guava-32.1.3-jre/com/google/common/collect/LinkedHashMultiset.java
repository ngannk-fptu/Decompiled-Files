/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.J2ktIncompatible;
import com.google.common.collect.AbstractMapBasedMultiset;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multisets;
import com.google.common.collect.Serialization;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable=true, emulated=true)
public final class LinkedHashMultiset<E>
extends AbstractMapBasedMultiset<E> {
    @GwtIncompatible
    @J2ktIncompatible
    private static final long serialVersionUID = 0L;

    public static <E> LinkedHashMultiset<E> create() {
        return new LinkedHashMultiset<E>();
    }

    public static <E> LinkedHashMultiset<E> create(int distinctElements) {
        return new LinkedHashMultiset<E>(distinctElements);
    }

    public static <E> LinkedHashMultiset<E> create(Iterable<? extends E> elements) {
        LinkedHashMultiset<E> multiset = LinkedHashMultiset.create(Multisets.inferDistinctElements(elements));
        Iterables.addAll(multiset, elements);
        return multiset;
    }

    private LinkedHashMultiset() {
        super(new LinkedHashMap());
    }

    private LinkedHashMultiset(int distinctElements) {
        super(Maps.newLinkedHashMapWithExpectedSize(distinctElements));
    }

    @GwtIncompatible
    @J2ktIncompatible
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        Serialization.writeMultiset(this, stream);
    }

    @GwtIncompatible
    @J2ktIncompatible
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        int distinctElements = Serialization.readCount(stream);
        this.setBackingMap(new LinkedHashMap());
        Serialization.populateMultiset(this, stream, distinctElements);
    }
}

