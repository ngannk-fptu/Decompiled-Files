/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2;

import java.util.Iterator;
import java.util.NoSuchElementException;

class PrefixedKeysIterator
implements Iterator<String> {
    private final Iterator<String> iterator;
    private final String prefix;
    private String nextElement;
    private boolean nextElementSet;

    public PrefixedKeysIterator(Iterator<String> wrappedIterator, String keyPrefix) {
        this.iterator = wrappedIterator;
        this.prefix = keyPrefix;
    }

    @Override
    public boolean hasNext() {
        return this.nextElementSet || this.setNextElement();
    }

    @Override
    public String next() {
        if (!this.nextElementSet && !this.setNextElement()) {
            throw new NoSuchElementException();
        }
        this.nextElementSet = false;
        return this.nextElement;
    }

    @Override
    public void remove() {
        if (this.nextElementSet) {
            throw new IllegalStateException("remove() cannot be called");
        }
        this.iterator.remove();
    }

    private boolean setNextElement() {
        while (this.iterator.hasNext()) {
            String key = this.iterator.next();
            if (!key.startsWith(this.prefix + ".") && !key.equals(this.prefix)) continue;
            this.nextElement = key;
            this.nextElementSet = true;
            return true;
        }
        return false;
    }
}

