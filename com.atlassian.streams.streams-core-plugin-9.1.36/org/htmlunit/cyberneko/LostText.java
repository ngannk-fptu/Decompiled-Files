/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko;

import java.util.ArrayList;
import java.util.List;
import org.htmlunit.cyberneko.xerces.xni.Augmentations;
import org.htmlunit.cyberneko.xerces.xni.XMLDocumentHandler;
import org.htmlunit.cyberneko.xerces.xni.XMLString;

class LostText {
    private final List<Entry> entries_ = new ArrayList<Entry>();

    LostText() {
    }

    public void add(XMLString text, Augmentations augs) {
        if (!this.entries_.isEmpty() || text.toString().trim().length() > 0) {
            this.entries_.add(new Entry(text, augs));
        }
    }

    public void refeed(XMLDocumentHandler tagBalancer) {
        for (Entry entry : new ArrayList<Entry>(this.entries_)) {
            tagBalancer.characters(entry.text_, entry.augs_);
        }
        this.entries_.clear();
    }

    public boolean isEmpty() {
        return this.entries_.isEmpty();
    }

    public void clear() {
        this.entries_.clear();
    }

    private static final class Entry {
        private final XMLString text_;
        private final Augmentations augs_;

        Entry(XMLString text, Augmentations augs) {
            this.text_ = text.clone();
            this.augs_ = augs;
        }
    }
}

