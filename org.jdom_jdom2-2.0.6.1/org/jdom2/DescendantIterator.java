/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

import java.util.Iterator;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Parent;
import org.jdom2.internal.ArrayCopy;
import org.jdom2.util.IteratorIterable;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class DescendantIterator
implements IteratorIterable<Content> {
    private final Parent parent;
    private Object[] stack = new Object[16];
    private int ssize = 0;
    private Iterator<Content> current = null;
    private Iterator<Content> descending = null;
    private Iterator<Content> ascending = null;
    private boolean hasnext = true;

    DescendantIterator(Parent parent) {
        this.parent = parent;
        this.current = parent.getContent().iterator();
        this.hasnext = this.current.hasNext();
    }

    public DescendantIterator iterator() {
        return new DescendantIterator(this.parent);
    }

    @Override
    public boolean hasNext() {
        return this.hasnext;
    }

    @Override
    public Content next() {
        if (this.descending != null) {
            this.current = this.descending;
            this.descending = null;
        } else if (this.ascending != null) {
            this.current = this.ascending;
            this.ascending = null;
        }
        Content ret = this.current.next();
        if (ret instanceof Element && ((Element)ret).getContentSize() > 0) {
            this.descending = ((Element)ret).getContent().iterator();
            if (this.ssize >= this.stack.length) {
                this.stack = ArrayCopy.copyOf(this.stack, this.ssize + 16);
            }
            this.stack[this.ssize++] = this.current;
            return ret;
        }
        if (this.current.hasNext()) {
            return ret;
        }
        while (this.ssize > 0) {
            Iterator subit;
            this.ascending = subit = (Iterator)this.stack[--this.ssize];
            this.stack[this.ssize] = null;
            if (!this.ascending.hasNext()) continue;
            return ret;
        }
        this.ascending = null;
        this.hasnext = false;
        return ret;
    }

    @Override
    public void remove() {
        this.current.remove();
        this.descending = null;
        if (this.current.hasNext() || this.ascending != null) {
            return;
        }
        while (this.ssize > 0) {
            Iterator subit = (Iterator)this.stack[--this.ssize];
            this.stack[this.ssize] = null;
            this.ascending = subit;
            if (!this.ascending.hasNext()) continue;
            return;
        }
        this.ascending = null;
        this.hasnext = false;
    }
}

