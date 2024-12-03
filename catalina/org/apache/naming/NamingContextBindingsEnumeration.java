/*
 * Decompiled with CFR 0.152.
 */
package org.apache.naming;

import java.util.Iterator;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import org.apache.naming.NamingEntry;

public class NamingContextBindingsEnumeration
implements NamingEnumeration<Binding> {
    protected final Iterator<NamingEntry> iterator;
    private final Context ctx;

    public NamingContextBindingsEnumeration(Iterator<NamingEntry> entries, Context ctx) {
        this.iterator = entries;
        this.ctx = ctx;
    }

    @Override
    public Binding next() throws NamingException {
        return this.nextElementInternal();
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
    public Binding nextElement() {
        try {
            return this.nextElementInternal();
        }
        catch (NamingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private Binding nextElementInternal() throws NamingException {
        Object value;
        NamingEntry entry = this.iterator.next();
        if (entry.type == 2 || entry.type == 1) {
            try {
                value = this.ctx.lookup(new CompositeName(entry.name));
            }
            catch (NamingException e) {
                throw e;
            }
            catch (Exception e) {
                NamingException ne = new NamingException(e.getMessage());
                ne.initCause(e);
                throw ne;
            }
        } else {
            value = entry.value;
        }
        return new Binding(entry.name, value.getClass().getName(), value, true);
    }
}

