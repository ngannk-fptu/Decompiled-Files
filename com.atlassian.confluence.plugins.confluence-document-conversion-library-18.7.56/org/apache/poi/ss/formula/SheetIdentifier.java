/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.NameIdentifier;

public class SheetIdentifier {
    private final String _bookName;
    private final NameIdentifier _sheetIdentifier;

    public SheetIdentifier(String bookName, NameIdentifier sheetIdentifier) {
        this._bookName = bookName;
        this._sheetIdentifier = sheetIdentifier;
    }

    public String getBookName() {
        return this._bookName;
    }

    public NameIdentifier getSheetIdentifier() {
        return this._sheetIdentifier;
    }

    protected void asFormulaString(StringBuilder sb) {
        if (this._bookName != null) {
            sb.append(" [").append(this._sheetIdentifier.getName()).append("]");
        }
        if (this._sheetIdentifier.isQuoted()) {
            sb.append('\'').append(this._sheetIdentifier.getName()).append("'");
        } else {
            sb.append(this._sheetIdentifier.getName());
        }
    }

    public String asFormulaString() {
        StringBuilder sb = new StringBuilder(32);
        this.asFormulaString(sb);
        return sb.toString();
    }

    public String toString() {
        return this.getClass().getName() + " [" + this.asFormulaString() + "]";
    }
}

