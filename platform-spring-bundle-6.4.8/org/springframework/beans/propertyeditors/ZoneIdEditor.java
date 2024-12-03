/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.time.ZoneId;
import org.springframework.util.StringUtils;

public class ZoneIdEditor
extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.hasText(text)) {
            text = text.trim();
        }
        this.setValue(ZoneId.of(text));
    }

    @Override
    public String getAsText() {
        ZoneId value = (ZoneId)this.getValue();
        return value != null ? value.getId() : "";
    }
}

