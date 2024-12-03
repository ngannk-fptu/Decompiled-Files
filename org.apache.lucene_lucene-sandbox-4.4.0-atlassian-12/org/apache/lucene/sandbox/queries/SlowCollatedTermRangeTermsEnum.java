/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.FilteredTermsEnum
 *  org.apache.lucene.index.FilteredTermsEnum$AcceptStatus
 *  org.apache.lucene.index.TermsEnum
 *  org.apache.lucene.util.BytesRef
 */
package org.apache.lucene.sandbox.queries;

import java.text.Collator;
import org.apache.lucene.index.FilteredTermsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;

@Deprecated
public class SlowCollatedTermRangeTermsEnum
extends FilteredTermsEnum {
    private Collator collator;
    private String upperTermText;
    private String lowerTermText;
    private boolean includeLower;
    private boolean includeUpper;

    public SlowCollatedTermRangeTermsEnum(TermsEnum tenum, String lowerTermText, String upperTermText, boolean includeLower, boolean includeUpper, Collator collator) {
        super(tenum);
        this.collator = collator;
        this.upperTermText = upperTermText;
        this.lowerTermText = lowerTermText;
        this.includeLower = includeLower;
        this.includeUpper = includeUpper;
        if (this.lowerTermText == null) {
            this.lowerTermText = "";
            this.includeLower = true;
        }
        BytesRef startBytesRef = new BytesRef((CharSequence)"");
        this.setInitialSeekTerm(startBytesRef);
    }

    protected FilteredTermsEnum.AcceptStatus accept(BytesRef term) {
        if ((this.includeLower ? this.collator.compare(term.utf8ToString(), this.lowerTermText) >= 0 : this.collator.compare(term.utf8ToString(), this.lowerTermText) > 0) && (this.upperTermText == null || (this.includeUpper ? this.collator.compare(term.utf8ToString(), this.upperTermText) <= 0 : this.collator.compare(term.utf8ToString(), this.upperTermText) < 0))) {
            return FilteredTermsEnum.AcceptStatus.YES;
        }
        return FilteredTermsEnum.AcceptStatus.NO;
    }
}

