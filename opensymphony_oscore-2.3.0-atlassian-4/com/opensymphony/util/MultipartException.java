/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MultipartException
extends Exception {
    protected List errors = new ArrayList();

    public synchronized String[] getArray() {
        String[] result = new String[this.errors.size()];
        Iterator i = this.getIterator();
        int c = 0;
        while (i.hasNext()) {
            result[c] = (String)i.next();
            ++c;
        }
        return result;
    }

    public Iterator getIterator() {
        return this.errors.iterator();
    }

    public List getList() {
        return Collections.unmodifiableList(this.errors);
    }

    public void add(String msg) {
        this.errors.add(msg);
    }

    public void add(Throwable exception) {
        this.add(exception.getMessage());
    }

    public boolean hasErrors() {
        return this.errors.size() > 0;
    }

    public Iterator list() {
        return this.getIterator();
    }
}

