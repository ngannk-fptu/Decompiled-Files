/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

public class ElementsSequenceGenerator<T>
implements Iterator<T>,
Iterable<T> {
    private List<T> elements;
    private Random rng;

    public ElementsSequenceGenerator(Collection<T> elements) {
        this(elements, System.nanoTime());
    }

    public ElementsSequenceGenerator(Collection<T> elements, long seed) {
        this(elements, new Random(seed));
    }

    public ElementsSequenceGenerator(Collection<T> elements, Random rng) {
        this.elements = new ArrayList<T>(elements);
        this.rng = rng;
    }

    @Override
    public boolean hasNext() {
        return !this.elements.isEmpty();
    }

    @Override
    public T next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        int index = this.rng.nextInt(this.elements.size());
        T result = this.elements.get(index);
        this.elements.set(index, this.elements.get(this.elements.size() - 1));
        this.elements.remove(this.elements.size() - 1);
        return result;
    }

    @Override
    public Iterator<T> iterator() {
        return this;
    }
}

