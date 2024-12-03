/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import org.springframework.util.StringUtils;

public class LocaleEditor
extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) {
        this.setValue(StringUtils.parseLocaleString(text));
    }

    @Override
    public String getAsText() {
        Object value = this.getValue();
        return value != null ? value.toString() : "";
    }
}

