/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.xni.Augmentations
 *  org.apache.xerces.xni.XMLDocumentHandler
 *  org.apache.xerces.xni.XMLString
 */
package org.cyberneko.html;

import java.util.ArrayList;
import java.util.List;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLString;
import org.cyberneko.html.HTMLAugmentations;

class LostText {
    private final List entries = new ArrayList();

    LostText() {
    }

    public void add(XMLString text, Augmentations augs) {
        if (!this.entries.isEmpty() || text.toString().trim().length() > 0) {
            this.entries.add(new Entry(text, augs));
        }
    }

    public void refeed(XMLDocumentHandler tagBalancer) {
        for (Entry entry : this.entries) {
            tagBalancer.characters(entry.text_, entry.augs_);
        }
        this.entries.clear();
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    static class Entry {
        private XMLString text_;
        private Augmentations augs_;

        public Entry(XMLString text, Augmentations augs) {
            char[] chars = new char[text.length];
            System.arraycopy(text.ch, text.offset, chars, 0, text.length);
            this.text_ = new XMLString(chars, 0, chars.length);
            if (augs != null) {
                this.augs_ = new HTMLAugmentations(augs);
            }
        }
    }
}

