/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.sql;

import java.util.Iterator;
import java.util.Map;
import org.hibernate.sql.CaseFragment;

public class ANSICaseFragment
extends CaseFragment {
    @Override
    public String toFragmentString() {
        StringBuilder buf = new StringBuilder(this.cases.size() * 15 + 10).append("case");
        Iterator iterator = this.cases.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry o;
            Map.Entry me = o = iterator.next();
            buf.append(" when ").append(me.getKey()).append(" is not null then ").append(me.getValue());
        }
        buf.append(" end");
        if (this.returnColumnName != null) {
            buf.append(" as ").append(this.returnColumnName);
        }
        return buf.toString();
    }
}

