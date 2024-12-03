/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.scheduling.support;

import java.time.temporal.Temporal;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.support.CronField;
import org.springframework.util.Assert;

final class CompositeCronField
extends CronField {
    private final CronField[] fields;
    private final String value;

    private CompositeCronField(CronField.Type type, CronField[] fields, String value) {
        super(type);
        this.fields = fields;
        this.value = value;
    }

    public static CronField compose(CronField[] fields, CronField.Type type, String value) {
        Assert.notEmpty((Object[])fields, (String)"Fields must not be empty");
        Assert.hasLength((String)value, (String)"Value must not be empty");
        if (fields.length == 1) {
            return fields[0];
        }
        return new CompositeCronField(type, fields, value);
    }

    @Override
    @Nullable
    public <T extends Temporal & Comparable<? super T>> T nextOrSame(T temporal) {
        Object result = null;
        for (CronField field : this.fields) {
            T candidate = field.nextOrSame(temporal);
            if (result != null && (candidate == null || ((Comparable<Object>)candidate).compareTo(result) >= 0)) continue;
            result = candidate;
        }
        return result;
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompositeCronField)) {
            return false;
        }
        CompositeCronField other = (CompositeCronField)o;
        return this.type() == other.type() && this.value.equals(other.value);
    }

    public String toString() {
        return (Object)((Object)this.type()) + " '" + this.value + "'";
    }
}

