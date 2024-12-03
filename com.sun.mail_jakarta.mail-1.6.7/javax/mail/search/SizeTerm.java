/*
 * Decompiled with CFR 0.152.
 */
package javax.mail.search;

import javax.mail.Message;
import javax.mail.search.IntegerComparisonTerm;

public final class SizeTerm
extends IntegerComparisonTerm {
    private static final long serialVersionUID = -2556219451005103709L;

    public SizeTerm(int comparison, int size) {
        super(comparison, size);
    }

    @Override
    public boolean match(Message msg) {
        int size;
        try {
            size = msg.getSize();
        }
        catch (Exception e) {
            return false;
        }
        if (size == -1) {
            return false;
        }
        return super.match(size);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SizeTerm)) {
            return false;
        }
        return super.equals(obj);
    }
}

