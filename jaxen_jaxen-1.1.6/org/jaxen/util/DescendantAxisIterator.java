/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.jaxen.JaxenRuntimeException;
import org.jaxen.Navigator;
import org.jaxen.UnsupportedAxisException;

public class DescendantAxisIterator
implements Iterator {
    private ArrayList stack = new ArrayList();
    private Iterator children;
    private Navigator navigator;

    public DescendantAxisIterator(Object contextNode, Navigator navigator) throws UnsupportedAxisException {
        this(navigator, navigator.getChildAxisIterator(contextNode));
    }

    public DescendantAxisIterator(Navigator navigator, Iterator iterator) {
        this.navigator = navigator;
        this.children = iterator;
    }

    public boolean hasNext() {
        while (!this.children.hasNext()) {
            if (this.stack.isEmpty()) {
                return false;
            }
            this.children = (Iterator)this.stack.remove(this.stack.size() - 1);
        }
        return true;
    }

    public Object next() {
        try {
            if (this.hasNext()) {
                Object node = this.children.next();
                this.stack.add(this.children);
                this.children = this.navigator.getChildAxisIterator(node);
                return node;
            }
            throw new NoSuchElementException();
        }
        catch (UnsupportedAxisException e) {
            throw new JaxenRuntimeException(e);
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}

