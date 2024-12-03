/*
 * Decompiled with CFR 0.152.
 */
package javax.mail.internet;

import com.sun.mail.util.PropUtil;
import javax.mail.internet.HeaderTokenizer;
import javax.mail.internet.ParameterList;
import javax.mail.internet.ParseException;

public class ContentDisposition {
    private static final boolean contentDispositionStrict = PropUtil.getBooleanSystemProperty("mail.mime.contentdisposition.strict", true);
    private String disposition;
    private ParameterList list;

    public ContentDisposition() {
    }

    public ContentDisposition(String disposition, ParameterList list) {
        this.disposition = disposition;
        this.list = list;
    }

    public ContentDisposition(String s) throws ParseException {
        block6: {
            String rem;
            HeaderTokenizer h = new HeaderTokenizer(s, "()<>@,;:\\\"\t []/?=");
            HeaderTokenizer.Token tk = h.next();
            if (tk.getType() != -1) {
                if (contentDispositionStrict) {
                    throw new ParseException("Expected disposition, got " + tk.getValue());
                }
            } else {
                this.disposition = tk.getValue();
            }
            if ((rem = h.getRemainder()) != null) {
                try {
                    this.list = new ParameterList(rem);
                }
                catch (ParseException px) {
                    if (!contentDispositionStrict) break block6;
                    throw px;
                }
            }
        }
    }

    public String getDisposition() {
        return this.disposition;
    }

    public String getParameter(String name) {
        if (this.list == null) {
            return null;
        }
        return this.list.get(name);
    }

    public ParameterList getParameterList() {
        return this.list;
    }

    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }

    public void setParameter(String name, String value) {
        if (this.list == null) {
            this.list = new ParameterList();
        }
        this.list.set(name, value);
    }

    public void setParameterList(ParameterList list) {
        this.list = list;
    }

    public String toString() {
        if (this.disposition == null) {
            return "";
        }
        if (this.list == null) {
            return this.disposition;
        }
        StringBuilder sb = new StringBuilder(this.disposition);
        sb.append(this.list.toString(sb.length() + 21));
        return sb.toString();
    }
}

