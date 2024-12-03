/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import org.apache.lucene.index.FilteredTermsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;

public final class SingleTermsEnum
extends FilteredTermsEnum {
    private final BytesRef singleRef;

    public SingleTermsEnum(TermsEnum tenum, BytesRef termText) {
        super(tenum);
        this.singleRef = termText;
        this.setInitialSeekTerm(termText);
    }

    @Override
    protected FilteredTermsEnum.AcceptStatus accept(BytesRef term) {
        return term.equals(this.singleRef) ? FilteredTermsEnum.AcceptStatus.YES : FilteredTermsEnum.AcceptStatus.END;
    }
}

