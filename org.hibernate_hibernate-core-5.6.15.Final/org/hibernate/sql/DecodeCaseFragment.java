/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.sql;

import java.util.Iterator;
import java.util.Map;
import org.hibernate.sql.CaseFragment;

public class DecodeCaseFragment
extends CaseFragment {
    @Override
    public String toFragmentString() {
        StringBuilder buf = new StringBuilder(this.cases.size() * 15 + 10).append("decode(");
        Iterator iter = this.cases.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry me = iter.next();
            if (iter.hasNext()) {
                buf.append(", ").append(me.getKey()).append(", ").append(me.getValue());
                continue;
            }
            buf.insert(7, me.getKey()).append(", ").append(me.getValue());
        }
        buf.append(')');
        if (this.returnColumnName != null) {
            buf.append(" as ").append(this.returnColumnName);
        }
        return buf.toString();
    }
}

