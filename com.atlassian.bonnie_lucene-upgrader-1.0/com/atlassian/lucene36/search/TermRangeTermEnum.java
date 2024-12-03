/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.FilteredTermEnum;
import com.atlassian.lucene36.util.StringHelper;
import java.io.IOException;
import java.text.Collator;

public class TermRangeTermEnum
extends FilteredTermEnum {
    private Collator collator = null;
    private boolean endEnum = false;
    private String field;
    private String upperTermText;
    private String lowerTermText;
    private boolean includeLower;
    private boolean includeUpper;

    public TermRangeTermEnum(IndexReader reader, String field, String lowerTermText, String upperTermText, boolean includeLower, boolean includeUpper, Collator collator) throws IOException {
        this.collator = collator;
        this.upperTermText = upperTermText;
        this.lowerTermText = lowerTermText;
        this.includeLower = includeLower;
        this.includeUpper = includeUpper;
        this.field = StringHelper.intern(field);
        if (this.lowerTermText == null) {
            this.lowerTermText = "";
            this.includeLower = true;
        }
        if (this.upperTermText == null) {
            this.includeUpper = true;
        }
        String startTermText = collator == null ? this.lowerTermText : "";
        this.setEnum(reader.terms(new Term(this.field, startTermText)));
    }

    public float difference() {
        return 1.0f;
    }

    protected boolean endEnum() {
        return this.endEnum;
    }

    protected boolean termCompare(Term term) {
        if (this.collator == null) {
            boolean checkLower = false;
            if (!this.includeLower) {
                checkLower = true;
            }
            if (term != null && term.field() == this.field) {
                if (!checkLower || null == this.lowerTermText || term.text().compareTo(this.lowerTermText) > 0) {
                    int compare;
                    checkLower = false;
                    if (this.upperTermText != null && ((compare = this.upperTermText.compareTo(term.text())) < 0 || !this.includeUpper && compare == 0)) {
                        this.endEnum = true;
                        return false;
                    }
                    return true;
                }
            } else {
                this.endEnum = true;
                return false;
            }
            return false;
        }
        if (term != null && term.field() == this.field) {
            return (this.lowerTermText == null || (this.includeLower ? this.collator.compare(term.text(), this.lowerTermText) >= 0 : this.collator.compare(term.text(), this.lowerTermText) > 0)) && (this.upperTermText == null || (this.includeUpper ? this.collator.compare(term.text(), this.upperTermText) <= 0 : this.collator.compare(term.text(), this.upperTermText) < 0));
        }
        this.endEnum = true;
        return false;
    }
}

