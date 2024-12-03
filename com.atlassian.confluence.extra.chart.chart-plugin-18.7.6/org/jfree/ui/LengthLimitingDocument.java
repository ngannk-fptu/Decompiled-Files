/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class LengthLimitingDocument
extends PlainDocument {
    private int maxlen;

    public LengthLimitingDocument() {
        this(-1);
    }

    public LengthLimitingDocument(int maxlen) {
        this.maxlen = maxlen;
    }

    public void setMaxLength(int maxlen) {
        this.maxlen = maxlen;
    }

    public int getMaxLength() {
        return this.maxlen;
    }

    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (str == null) {
            return;
        }
        if (this.maxlen < 0) {
            super.insertString(offs, str, a);
        }
        char[] numeric = str.toCharArray();
        StringBuffer b = new StringBuffer();
        b.append(numeric, 0, Math.min(this.maxlen, numeric.length));
        super.insertString(offs, b.toString(), a);
    }
}

