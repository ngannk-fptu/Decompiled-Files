/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.Chapter;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Section;
import com.lowagie.text.error_messages.MessageLocalization;

public class ChapterAutoNumber
extends Chapter {
    private static final long serialVersionUID = -9217457637987854167L;
    protected boolean numberSet = false;

    public ChapterAutoNumber(Paragraph para) {
        super(para, 0);
    }

    public ChapterAutoNumber(String title) {
        super(title, 0);
    }

    @Override
    public Section addSection(String title) {
        if (this.isAddedCompletely()) {
            throw new IllegalStateException(MessageLocalization.getComposedMessage("this.largeelement.has.already.been.added.to.the.document"));
        }
        return this.addSection(title, 2);
    }

    @Override
    public Section addSection(Paragraph title) {
        if (this.isAddedCompletely()) {
            throw new IllegalStateException(MessageLocalization.getComposedMessage("this.largeelement.has.already.been.added.to.the.document"));
        }
        return this.addSection(title, 2);
    }

    public int setAutomaticNumber(int number) {
        if (!this.numberSet) {
            super.setChapterNumber(++number);
            this.numberSet = true;
        }
        return number;
    }
}

