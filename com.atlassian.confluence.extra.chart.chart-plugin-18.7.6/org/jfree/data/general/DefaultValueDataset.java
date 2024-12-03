/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.general;

import java.io.Serializable;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.ValueDataset;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class DefaultValueDataset
extends AbstractDataset
implements ValueDataset,
Cloneable,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = 8137521217249294891L;
    private Number value;

    public DefaultValueDataset() {
        this(null);
    }

    public DefaultValueDataset(double value) {
        this(new Double(value));
    }

    public DefaultValueDataset(Number value) {
        this.value = value;
    }

    public Number getValue() {
        return this.value;
    }

    public void setValue(Number value) {
        this.value = value;
        this.notifyListeners(new DatasetChangeEvent(this, this));
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ValueDataset) {
            ValueDataset vd = (ValueDataset)obj;
            return ObjectUtilities.equal(this.value, vd.getValue());
        }
        return false;
    }

    public int hashCode() {
        return this.value != null ? this.value.hashCode() : 0;
    }
}

