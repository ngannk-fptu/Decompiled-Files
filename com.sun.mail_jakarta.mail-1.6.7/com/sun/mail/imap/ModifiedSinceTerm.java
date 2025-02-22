/*
 * Decompiled with CFR 0.152.
 */
package com.sun.mail.imap;

import com.sun.mail.imap.IMAPMessage;
import javax.mail.Message;
import javax.mail.search.SearchTerm;

public final class ModifiedSinceTerm
extends SearchTerm {
    private long modseq;
    private static final long serialVersionUID = 5151457469634727992L;

    public ModifiedSinceTerm(long modseq) {
        this.modseq = modseq;
    }

    public long getModSeq() {
        return this.modseq;
    }

    @Override
    public boolean match(Message msg) {
        long m;
        try {
            if (!(msg instanceof IMAPMessage)) {
                return false;
            }
            m = ((IMAPMessage)msg).getModSeq();
        }
        catch (Exception e) {
            return false;
        }
        return m >= this.modseq;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ModifiedSinceTerm)) {
            return false;
        }
        return this.modseq == ((ModifiedSinceTerm)obj).modseq;
    }

    public int hashCode() {
        return (int)this.modseq;
    }
}

