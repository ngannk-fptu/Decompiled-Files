/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.StringUtils
 */
package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.util.Currency;
import org.springframework.util.StringUtils;

public class CurrencyEditor
extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.hasText((String)text)) {
            text = text.trim();
        }
        this.setValue(Currency.getInstance(text));
    }

    @Override
    public String getAsText() {
        Currency value = (Currency)this.getValue();
        return value != null ? value.getCurrencyCode() : "";
    }
}

