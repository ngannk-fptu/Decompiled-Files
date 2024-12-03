/*
 * Decompiled with CFR 0.152.
 */
package javax.mail.internet;

import javax.mail.internet.ParseException;

public class AddressException
extends ParseException {
    protected String ref = null;
    protected int pos = -1;
    private static final long serialVersionUID = 9134583443539323120L;

    public AddressException() {
    }

    public AddressException(String s) {
        super(s);
    }

    public AddressException(String s, String ref) {
        super(s);
        this.ref = ref;
    }

    public AddressException(String s, String ref, int pos) {
        super(s);
        this.ref = ref;
        this.pos = pos;
    }

    public String getRef() {
        return this.ref;
    }

    public int getPos() {
        return this.pos;
    }

    @Override
    public String toString() {
        String s = super.toString();
        if (this.ref == null) {
            return s;
        }
        s = s + " in string ``" + this.ref + "''";
        if (this.pos < 0) {
            return s;
        }
        return s + " at position " + this.pos;
    }
}

