/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.sql;

import java.util.Map;
import org.hibernate.sql.CaseFragment;

public class DerbyCaseFragment
extends CaseFragment {
    @Override
    public String toFragmentString() {
        StringBuilder buf = new StringBuilder(this.cases.size() * 15 + 10);
        buf.append("case");
        for (Map.Entry me : this.cases.entrySet()) {
            buf.append(" when ").append(me.getKey()).append(" is not null then ").append(me.getValue());
        }
        buf.append(" else -1");
        buf.append(" end");
        if (this.returnColumnName != null) {
            buf.append(" as ").append(this.returnColumnName);
        }
        return buf.toString();
    }
}

