/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.bundlerepository;

import java.util.Enumeration;
import java.util.Iterator;

public class IteratorToEnumeration
implements Enumeration {
    private Iterator m_iter = null;

    public IteratorToEnumeration(Iterator iter) {
        this.m_iter = iter;
    }

    public boolean hasMoreElements() {
        if (this.m_iter == null) {
            return false;
        }
        return this.m_iter.hasNext();
    }

    public Object nextElement() {
        if (this.m_iter == null) {
            return null;
        }
        return this.m_iter.next();
    }
}

