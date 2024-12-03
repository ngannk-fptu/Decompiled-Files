/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

public class MergedEnumeration<E>
implements Enumeration<E> {
    private final Enumeration<E> enumeration;

    public MergedEnumeration(Enumeration<E> ... enumerations) {
        ArrayList<E> list = new ArrayList<E>();
        for (Enumeration<E> element : enumerations) {
            while (element.hasMoreElements()) {
                E e = element.nextElement();
                list.add(e);
            }
        }
        this.enumeration = Collections.enumeration(list);
    }

    @Override
    public boolean hasMoreElements() {
        return this.enumeration.hasMoreElements();
    }

    @Override
    public E nextElement() {
        return this.enumeration.nextElement();
    }
}

