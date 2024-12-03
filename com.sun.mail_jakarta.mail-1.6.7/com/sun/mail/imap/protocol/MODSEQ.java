/*
 * Decompiled with CFR 0.152.
 */
package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ParsingException;
import com.sun.mail.imap.protocol.FetchResponse;
import com.sun.mail.imap.protocol.Item;

public class MODSEQ
implements Item {
    static final char[] name = new char[]{'M', 'O', 'D', 'S', 'E', 'Q'};
    public int seqnum;
    public long modseq;

    public MODSEQ(FetchResponse r) throws ParsingException {
        this.seqnum = r.getNumber();
        r.skipSpaces();
        if (r.readByte() != 40) {
            throw new ParsingException("MODSEQ parse error");
        }
        this.modseq = r.readLong();
        if (!r.isNextNonSpace(')')) {
            throw new ParsingException("MODSEQ parse error");
        }
    }
}

