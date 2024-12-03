/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.sql;

import java.util.Map;
import org.hibernate.sql.CaseFragment;

public class HSQLCaseFragment
extends CaseFragment {
    @Override
    public String toFragmentString() {
        StringBuilder buf = new StringBuilder(this.cases.size() * 15 + 10);
        StringBuilder buf2 = new StringBuilder(this.cases.size());
        for (Map.Entry me : this.cases.entrySet()) {
            buf.append(" casewhen(").append(me.getKey()).append(" is not null").append(", ").append(me.getValue()).append(", ");
            buf2.append(")");
        }
        buf.append("-1");
        buf.append(buf2.toString());
        if (this.returnColumnName != null) {
            buf.append(" as ").append(this.returnColumnName);
        }
        return buf.toString();
    }
}

