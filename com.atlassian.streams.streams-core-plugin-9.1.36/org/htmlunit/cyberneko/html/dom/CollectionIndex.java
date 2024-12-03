/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

class CollectionIndex {
    private int index_;

    void decrement() {
        --this.index_;
    }

    boolean isZero() {
        return this.index_ <= 0;
    }

    CollectionIndex(int index) {
        this.index_ = index;
    }
}

