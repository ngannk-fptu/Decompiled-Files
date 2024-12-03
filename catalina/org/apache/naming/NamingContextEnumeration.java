/*
 * Decompiled with CFR 0.152.
 */
package org.apache.naming;

import java.util.Iterator;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import org.apache.naming.NamingEntry;

public class NamingContextEnumeration
implements NamingEnumeration<NameClassPair> {
    protected final Iterator<NamingEntry> iterator;

    public NamingContextEnumeration(Iterator<NamingEntry> entries) {
        this.iterator = entries;
    }

    @Override
    public NameClassPair next() throws NamingException {
        return this.nextElement();
    }

    @Override
    public boolean hasMore() throws NamingException {
        return this.iterator.hasNext();
    }

    @Override
    public void close() throws NamingException {
    }

    @Override
    public boolean hasMoreElements() {
        return this.iterator.hasNext();
    }

    @Override
    public NameClassPair nextElement() {
        NamingEntry entry = this.iterator.next();
        return new NameClassPair(entry.name, entry.value.getClass().getName());
    }
}

