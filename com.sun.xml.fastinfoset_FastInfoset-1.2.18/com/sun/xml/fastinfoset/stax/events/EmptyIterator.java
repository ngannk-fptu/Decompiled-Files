/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.stax.events;

import com.sun.xml.fastinfoset.CommonResourceBundle;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class EmptyIterator
implements Iterator {
    public static final EmptyIterator instance = new EmptyIterator();

    private EmptyIterator() {
    }

    public static EmptyIterator getInstance() {
        return instance;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    public Object next() throws NoSuchElementException {
        throw new NoSuchElementException();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.emptyIterator"));
    }
}

