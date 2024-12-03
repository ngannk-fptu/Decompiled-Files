/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.sac;

import org.apache.batik.css.engine.sac.ExtendedCondition;
import org.w3c.css.sac.AttributeCondition;

public abstract class AbstractAttributeCondition
implements AttributeCondition,
ExtendedCondition {
    protected String value;

    protected AbstractAttributeCondition(String value) {
        this.value = value;
    }

    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        AbstractAttributeCondition c = (AbstractAttributeCondition)obj;
        return c.value.equals(this.value);
    }

    public int hashCode() {
        return this.value == null ? -1 : this.value.hashCode();
    }

    @Override
    public int getSpecificity() {
        return 256;
    }

    @Override
    public String getValue() {
        return this.value;
    }
}

