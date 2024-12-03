/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.util.Currency;

public class CurrencyEditor
extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        this.setValue(Currency.getInstance(text));
    }

    @Override
    public String getAsText() {
        Currency value = (Currency)this.getValue();
        return value != null ? value.getCurrencyCode() : "";
    }
}

