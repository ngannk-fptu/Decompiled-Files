/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Table;

public final class TableIterator {
    Range _range;
    int _index;
    int _levelNum;

    TableIterator(Range range, int levelNum) {
        this._range = range;
        this._index = 0;
        this._levelNum = levelNum;
    }

    public TableIterator(Range range) {
        this(range, 1);
    }

    public boolean hasNext() {
        int numParagraphs = this._range.numParagraphs();
        while (this._index < numParagraphs) {
            Paragraph paragraph = this._range.getParagraph(this._index);
            if (paragraph.isInTable() && paragraph.getTableLevel() == this._levelNum) {
                return true;
            }
            ++this._index;
        }
        return false;
    }

    public Table next() {
        int numParagraphs = this._range.numParagraphs();
        int startIndex = this._index;
        int endIndex = this._index;
        while (this._index < numParagraphs) {
            Paragraph paragraph = this._range.getParagraph(this._index);
            if (!paragraph.isInTable() || paragraph.getTableLevel() < this._levelNum) {
                endIndex = this._index;
                break;
            }
            ++this._index;
        }
        return new Table(this._range.getParagraph(startIndex).getStartOffset(), this._range.getParagraph(endIndex - 1).getEndOffset(), this._range, this._levelNum);
    }
}

