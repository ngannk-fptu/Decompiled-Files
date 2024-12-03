/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.AttributeCondition
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

    public String getValue() {
        return this.value;
    }
}

