/*
 * Decompiled with CFR 0.152.
 */
package javax.mail.search;

import java.util.Date;
import javax.mail.Message;
import javax.mail.search.DateTerm;

public final class ReceivedDateTerm
extends DateTerm {
    private static final long serialVersionUID = -2756695246195503170L;

    public ReceivedDateTerm(int comparison, Date date) {
        super(comparison, date);
    }

    @Override
    public boolean match(Message msg) {
        Date d;
        try {
            d = msg.getReceivedDate();
        }
        catch (Exception e) {
            return false;
        }
        if (d == null) {
            return false;
        }
        return super.match(d);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ReceivedDateTerm)) {
            return false;
        }
        return super.equals(obj);
    }
}

