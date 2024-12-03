/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.util.Comparator;
import org.apache.lucene.index.FilteredTermsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;

public class TermRangeTermsEnum
extends FilteredTermsEnum {
    private final boolean includeLower;
    private final boolean includeUpper;
    private final BytesRef lowerBytesRef;
    private final BytesRef upperBytesRef;
    private final Comparator<BytesRef> termComp;

    public TermRangeTermsEnum(TermsEnum tenum, BytesRef lowerTerm, BytesRef upperTerm, boolean includeLower, boolean includeUpper) {
        super(tenum);
        if (lowerTerm == null) {
            this.lowerBytesRef = new BytesRef();
            this.includeLower = true;
        } else {
            this.lowerBytesRef = lowerTerm;
            this.includeLower = includeLower;
        }
        if (upperTerm == null) {
            this.includeUpper = true;
            this.upperBytesRef = null;
        } else {
            this.includeUpper = includeUpper;
            this.upperBytesRef = upperTerm;
        }
        this.setInitialSeekTerm(this.lowerBytesRef);
        this.termComp = this.getComparator();
    }

    @Override
    protected FilteredTermsEnum.AcceptStatus accept(BytesRef term) {
        int cmp;
        if (!this.includeLower && term.equals(this.lowerBytesRef)) {
            return FilteredTermsEnum.AcceptStatus.NO;
        }
        if (this.upperBytesRef != null && ((cmp = this.termComp.compare(this.upperBytesRef, term)) < 0 || !this.includeUpper && cmp == 0)) {
            return FilteredTermsEnum.AcceptStatus.END;
        }
        return FilteredTermsEnum.AcceptStatus.YES;
    }
}

