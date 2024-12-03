/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.sql;

import java.util.Map;
import org.hibernate.sql.CaseFragment;

public class MckoiCaseFragment
extends CaseFragment {
    @Override
    public String toFragmentString() {
        StringBuilder buf = new StringBuilder(this.cases.size() * 15 + 10);
        StringBuilder buf2 = new StringBuilder(this.cases.size());
        for (Map.Entry me : this.cases.entrySet()) {
            buf.append(" if(").append(me.getKey()).append(" is not null").append(", ").append(me.getValue()).append(", ");
            buf2.append(")");
        }
        buf.append("null");
        buf.append((CharSequence)buf2);
        if (this.returnColumnName != null) {
            buf.append(" as ").append(this.returnColumnName);
        }
        return buf.toString();
    }
}

