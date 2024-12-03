/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.message2;

import com.ibm.icu.text.FormattedValue;

@Deprecated
public class FormattedPlaceholder {
    private final FormattedValue formattedValue;
    private final Object inputValue;

    @Deprecated
    public FormattedPlaceholder(Object inputValue, FormattedValue formattedValue) {
        if (formattedValue == null) {
            throw new IllegalAccessError("Should not try to wrap a null formatted value");
        }
        this.inputValue = inputValue;
        this.formattedValue = formattedValue;
    }

    @Deprecated
    public Object getInput() {
        return this.inputValue;
    }

    @Deprecated
    public FormattedValue getFormattedValue() {
        return this.formattedValue;
    }

    @Deprecated
    public String toString() {
        return this.formattedValue.toString();
    }
}

