/*
 * Decompiled with CFR 0.152.
 */
package javax.mail.search;

import javax.mail.Message;
import javax.mail.search.SearchTerm;

public final class NotTerm
extends SearchTerm {
    private SearchTerm term;
    private static final long serialVersionUID = 7152293214217310216L;

    public NotTerm(SearchTerm t) {
        this.term = t;
    }

    public SearchTerm getTerm() {
        return this.term;
    }

    @Override
    public boolean match(Message msg) {
        return !this.term.match(msg);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof NotTerm)) {
            return false;
        }
        NotTerm nt = (NotTerm)obj;
        return nt.term.equals(this.term);
    }

    public int hashCode() {
        return this.term.hashCode() << 1;
    }
}

