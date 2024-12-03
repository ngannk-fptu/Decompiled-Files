/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.google.template.soy.data;

import com.google.template.soy.data.SoyAbstractValue;
import com.google.template.soy.data.SoyRecord;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueProvider;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class SoyAbstractRecord
extends SoyAbstractValue
implements SoyRecord {
    @Override
    public SoyValue getField(String name) {
        SoyValueProvider valueProvider = this.getFieldProvider(name);
        return valueProvider != null ? valueProvider.resolve() : null;
    }
}

