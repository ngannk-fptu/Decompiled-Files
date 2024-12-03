/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;

public class CompoundEnumeration
implements Enumeration {
    private Enumeration[] m_enums = null;
    private int index = 0;

    public CompoundEnumeration(Enumeration[] enums) {
        this.m_enums = enums;
    }

    @Override
    public boolean hasMoreElements() {
        if (this.currentEnumeration() == null) {
            return false;
        }
        return this.currentEnumeration().hasMoreElements();
    }

    private Enumeration findNextEnumeration(boolean moveCursor) {
        return this.findNextEnumeration(this.index, moveCursor);
    }

    private Enumeration findNextEnumeration(int cursor, boolean moveCursor) {
        int next = cursor + 1;
        if (next < this.m_enums.length) {
            if (this.m_enums[next] != null && this.m_enums[next].hasMoreElements()) {
                if (moveCursor) {
                    this.index = next;
                }
                return this.m_enums[next];
            }
            return this.findNextEnumeration(next, moveCursor);
        }
        return null;
    }

    public Object nextElement() {
        if (this.currentEnumeration() != null) {
            return this.currentEnumeration().nextElement();
        }
        throw new NoSuchElementException("No more elements");
    }

    private Enumeration currentEnumeration() {
        if (this.m_enums != null && this.index < this.m_enums.length) {
            Enumeration e = this.m_enums[this.index];
            if (e == null || !e.hasMoreElements()) {
                e = this.findNextEnumeration(true);
            }
            return e;
        }
        return null;
    }
}

