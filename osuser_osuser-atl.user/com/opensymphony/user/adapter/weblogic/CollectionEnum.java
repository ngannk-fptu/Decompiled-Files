/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user.adapter.weblogic;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

public class CollectionEnum
implements Enumeration {
    private Iterator iter;

    CollectionEnum(Collection c) {
        this.iter = c.iterator();
    }

    public boolean hasMoreElements() {
        return this.iter.hasNext();
    }

    public Object nextElement() {
        return this.iter.next();
    }
}

