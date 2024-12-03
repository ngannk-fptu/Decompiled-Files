/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermLength;
import cz.vutbr.web.css.TermRect;
import cz.vutbr.web.csskit.TermImpl;
import java.util.ArrayList;
import java.util.List;

public class TermRectImpl
extends TermImpl<List<TermLength>>
implements TermRect {
    public TermRectImpl(TermLength a, TermLength b, TermLength c, TermLength d) {
        this.value = new ArrayList(4);
        ((List)this.value).add(a);
        ((List)this.value).add(b);
        ((List)this.value).add(c);
        ((List)this.value).add(d);
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder("rect");
        ret.append("(");
        for (int i = 0; i < 4; ++i) {
            Term v;
            if (i != 0) {
                ret.append(" ");
            }
            ret.append((v = (Term)((List)this.value).get(i)) == null ? "auto" : v.toString());
        }
        ret.append(")");
        return ret.toString();
    }
}

