/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.stax.events;

import com.sun.xml.fastinfoset.CommonResourceBundle;
import com.sun.xml.fastinfoset.stax.events.EmptyIterator;
import java.util.Iterator;

public class ReadIterator
implements Iterator {
    Iterator iterator = EmptyIterator.getInstance();

    public ReadIterator() {
    }

    public ReadIterator(Iterator iterator) {
        if (iterator != null) {
            this.iterator = iterator;
        }
    }

    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    public Object next() {
        return this.iterator.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.readonlyList"));
    }
}

