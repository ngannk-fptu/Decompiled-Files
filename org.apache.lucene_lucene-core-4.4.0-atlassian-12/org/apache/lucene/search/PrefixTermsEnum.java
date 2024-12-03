/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import org.apache.lucene.index.FilteredTermsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.StringHelper;

public class PrefixTermsEnum
extends FilteredTermsEnum {
    private final BytesRef prefixRef;

    public PrefixTermsEnum(TermsEnum tenum, BytesRef prefixText) {
        super(tenum);
        this.prefixRef = prefixText;
        this.setInitialSeekTerm(this.prefixRef);
    }

    @Override
    protected FilteredTermsEnum.AcceptStatus accept(BytesRef term) {
        if (StringHelper.startsWith(term, this.prefixRef)) {
            return FilteredTermsEnum.AcceptStatus.YES;
        }
        return FilteredTermsEnum.AcceptStatus.END;
    }
}

