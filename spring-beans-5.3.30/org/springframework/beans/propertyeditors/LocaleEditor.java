/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.StringUtils
 */
package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import org.springframework.util.StringUtils;

public class LocaleEditor
extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) {
        this.setValue(StringUtils.parseLocaleString((String)text));
    }

    @Override
    public String getAsText() {
        Object value = this.getValue();
        return value != null ? value.toString() : "";
    }
}

