/*
 * Decompiled with CFR 0.152.
 */
package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ParsingException;
import com.sun.mail.iap.Response;
import com.sun.mail.imap.protocol.FLAGS;
import com.sun.mail.imap.protocol.IMAPResponse;
import java.util.ArrayList;
import java.util.List;
import javax.mail.Flags;

public class MailboxInfo {
    public Flags availableFlags = null;
    public Flags permanentFlags = null;
    public int total = -1;
    public int recent = -1;
    public int first = -1;
    public long uidvalidity = -1L;
    public long uidnext = -1L;
    public boolean uidNotSticky = false;
    public long highestmodseq = -1L;
    public int mode;
    public List<IMAPResponse> responses;

    public MailboxInfo(Response[] r) throws ParsingException {
        for (int i = 0; i < r.length; ++i) {
            String s;
            boolean handled;
            if (r[i] == null || !(r[i] instanceof IMAPResponse)) continue;
            IMAPResponse ir = (IMAPResponse)r[i];
            if (ir.keyEquals("EXISTS")) {
                this.total = ir.getNumber();
                r[i] = null;
                continue;
            }
            if (ir.keyEquals("RECENT")) {
                this.recent = ir.getNumber();
                r[i] = null;
                continue;
            }
            if (ir.keyEquals("FLAGS")) {
                this.availableFlags = new FLAGS(ir);
                r[i] = null;
                continue;
            }
            if (ir.keyEquals("VANISHED")) {
                if (this.responses == null) {
                    this.responses = new ArrayList<IMAPResponse>();
                }
                this.responses.add(ir);
                r[i] = null;
                continue;
            }
            if (ir.keyEquals("FETCH")) {
                if (this.responses == null) {
                    this.responses = new ArrayList<IMAPResponse>();
                }
                this.responses.add(ir);
                r[i] = null;
                continue;
            }
            if (ir.isUnTagged() && ir.isOK()) {
                ir.skipSpaces();
                if (ir.readByte() != 91) {
                    ir.reset();
                    continue;
                }
                handled = true;
                s = ir.readAtom();
                if (s.equalsIgnoreCase("UNSEEN")) {
                    this.first = ir.readNumber();
                } else if (s.equalsIgnoreCase("UIDVALIDITY")) {
                    this.uidvalidity = ir.readLong();
                } else if (s.equalsIgnoreCase("PERMANENTFLAGS")) {
                    this.permanentFlags = new FLAGS(ir);
                } else if (s.equalsIgnoreCase("UIDNEXT")) {
                    this.uidnext = ir.readLong();
                } else if (s.equalsIgnoreCase("HIGHESTMODSEQ")) {
                    this.highestmodseq = ir.readLong();
                } else {
                    handled = false;
                }
                if (handled) {
                    r[i] = null;
                    continue;
                }
                ir.reset();
                continue;
            }
            if (!ir.isUnTagged() || !ir.isNO()) continue;
            ir.skipSpaces();
            if (ir.readByte() != 91) {
                ir.reset();
                continue;
            }
            handled = true;
            s = ir.readAtom();
            if (s.equalsIgnoreCase("UIDNOTSTICKY")) {
                this.uidNotSticky = true;
            } else {
                handled = false;
            }
            if (handled) {
                r[i] = null;
                continue;
            }
            ir.reset();
        }
        if (this.permanentFlags == null) {
            this.permanentFlags = this.availableFlags != null ? new Flags(this.availableFlags) : new Flags();
        }
    }
}

