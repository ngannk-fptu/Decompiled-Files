/*
 * Decompiled with CFR 0.152.
 */
package javax.mail.search;

import java.util.Locale;
import javax.mail.Message;
import javax.mail.search.StringTerm;

public final class HeaderTerm
extends StringTerm {
    private String headerName;
    private static final long serialVersionUID = 8342514650333389122L;

    public HeaderTerm(String headerName, String pattern) {
        super(pattern);
        this.headerName = headerName;
    }

    public String getHeaderName() {
        return this.headerName;
    }

    @Override
    public boolean match(Message msg) {
        String[] headers;
        try {
            headers = msg.getHeader(this.headerName);
        }
        catch (Exception e) {
            return false;
        }
        if (headers == null) {
            return false;
        }
        for (int i = 0; i < headers.length; ++i) {
            if (!super.match(headers[i])) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HeaderTerm)) {
            return false;
        }
        HeaderTerm ht = (HeaderTerm)obj;
        return ht.headerName.equalsIgnoreCase(this.headerName) && super.equals(ht);
    }

    @Override
    public int hashCode() {
        return this.headerName.toLowerCase(Locale.ENGLISH).hashCode() + super.hashCode();
    }
}

