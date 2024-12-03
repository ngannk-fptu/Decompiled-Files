/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.directive;

import org.apache.velocity.runtime.directive.Scope;

public class ForeachScope
extends Scope {
    protected int index = -1;
    protected boolean hasNext = false;

    public ForeachScope(Object owner, Object replaces) {
        super(owner, replaces);
    }

    public int getIndex() {
        return this.index;
    }

    public int getCount() {
        return this.index + 1;
    }

    public boolean hasNext() {
        return this.getHasNext();
    }

    public boolean getHasNext() {
        return this.hasNext;
    }

    public boolean isFirst() {
        return this.index < 1;
    }

    public boolean getFirst() {
        return this.isFirst();
    }

    public boolean isLast() {
        return !this.hasNext;
    }

    public boolean getLast() {
        return this.isLast();
    }
}

