/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.sql;

import org.hibernate.sql.ConditionFragment;

public class DisjunctionFragment {
    private StringBuilder buffer = new StringBuilder();

    public DisjunctionFragment addCondition(ConditionFragment fragment) {
        this.addCondition(fragment.toFragmentString());
        return this;
    }

    public DisjunctionFragment addCondition(String fragment) {
        if (this.buffer.length() > 0) {
            this.buffer.append(" or ");
        }
        this.buffer.append('(').append(fragment).append(')');
        return this;
    }

    public String toFragmentString() {
        return this.buffer.toString();
    }
}

