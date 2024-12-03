/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.FilteredTermsEnum
 *  org.apache.lucene.index.FilteredTermsEnum$AcceptStatus
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.index.TermsEnum
 *  org.apache.lucene.util.BytesRef
 *  org.apache.lucene.util.StringHelper
 */
package org.apache.lucene.sandbox.queries.regex;

import org.apache.lucene.index.FilteredTermsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.sandbox.queries.regex.RegexCapabilities;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.StringHelper;

public class RegexTermsEnum
extends FilteredTermsEnum {
    private RegexCapabilities.RegexMatcher regexImpl;
    private final BytesRef prefixRef;

    public RegexTermsEnum(TermsEnum tenum, Term term, RegexCapabilities regexCap) {
        super(tenum);
        String text = term.text();
        this.regexImpl = regexCap.compile(text);
        String pre = this.regexImpl.prefix();
        if (pre == null) {
            pre = "";
        }
        this.prefixRef = new BytesRef((CharSequence)pre);
        this.setInitialSeekTerm(this.prefixRef);
    }

    protected FilteredTermsEnum.AcceptStatus accept(BytesRef term) {
        if (StringHelper.startsWith((BytesRef)term, (BytesRef)this.prefixRef)) {
            return this.regexImpl.match(term) ? FilteredTermsEnum.AcceptStatus.YES : FilteredTermsEnum.AcceptStatus.NO;
        }
        return FilteredTermsEnum.AcceptStatus.NO;
    }
}

