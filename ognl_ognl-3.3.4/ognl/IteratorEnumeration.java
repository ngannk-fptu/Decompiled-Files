/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.util.Enumeration;
import java.util.Iterator;

public class IteratorEnumeration
implements Enumeration {
    private Iterator it;

    public IteratorEnumeration(Iterator it) {
        this.it = it;
    }

    @Override
    public boolean hasMoreElements() {
        return this.it.hasNext();
    }

    public Object nextElement() {
        return this.it.next();
    }
}

