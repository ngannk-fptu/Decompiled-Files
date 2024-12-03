/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple.extend.form;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

class SizeLimitedDocument
extends PlainDocument {
    private static final long serialVersionUID = 1L;
    private int _maximumLength;

    public SizeLimitedDocument(int maximumLength) {
        this._maximumLength = maximumLength;
    }

    public int getMaximumLength() {
        return this._maximumLength;
    }

    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str == null) {
            return;
        }
        if (this.getLength() + str.length() <= this._maximumLength) {
            super.insertString(offset, str, attr);
        }
    }
}

