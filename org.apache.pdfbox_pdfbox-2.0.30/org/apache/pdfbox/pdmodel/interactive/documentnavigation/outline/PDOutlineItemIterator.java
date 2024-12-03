/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

class PDOutlineItemIterator
implements Iterator<PDOutlineItem> {
    private PDOutlineItem currentItem;
    private final PDOutlineItem startingItem;

    PDOutlineItemIterator(PDOutlineItem startingItem) {
        this.startingItem = startingItem;
    }

    @Override
    public boolean hasNext() {
        if (this.startingItem == null) {
            return false;
        }
        if (this.currentItem == null) {
            return true;
        }
        PDOutlineItem sibling = this.currentItem.getNextSibling();
        return sibling != null && !this.startingItem.equals(sibling);
    }

    @Override
    public PDOutlineItem next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        this.currentItem = this.currentItem == null ? this.startingItem : this.currentItem.getNextSibling();
        return this.currentItem;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

