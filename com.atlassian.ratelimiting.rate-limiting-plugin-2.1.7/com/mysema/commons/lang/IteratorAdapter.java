/*
 * Decompiled with CFR 0.152.
 */
package com.mysema.commons.lang;

import com.mysema.commons.lang.CloseableIterator;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IteratorAdapter<T>
implements CloseableIterator<T> {
    private final Iterator<T> iter;
    private final Closeable closeable;

    public IteratorAdapter(Iterator<T> iter) {
        this.iter = iter;
        this.closeable = iter instanceof Closeable ? (Closeable)((Object)iter) : null;
    }

    public IteratorAdapter(Iterator<T> iter, Closeable closeable) {
        this.iter = iter;
        this.closeable = closeable;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T> List<T> asList(Iterator<T> iter) {
        ArrayList<T> list = new ArrayList<T>();
        try {
            while (iter.hasNext()) {
                list.add(iter.next());
            }
        }
        finally {
            if (iter instanceof Closeable) {
                try {
                    ((Closeable)((Object)iter)).close();
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return list;
    }

    public List<T> asList() {
        return IteratorAdapter.asList(this.iter);
    }

    @Override
    public void close() {
        if (this.closeable != null) {
            try {
                this.closeable.close();
            }
            catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    @Override
    public boolean hasNext() {
        return this.iter.hasNext();
    }

    @Override
    public T next() {
        return this.iter.next();
    }

    @Override
    public void remove() {
        this.iter.remove();
    }
}

