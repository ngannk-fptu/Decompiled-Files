/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.StringUtils
 */
package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.util.UUID;
import org.springframework.util.StringUtils;

public class UUIDEditor
extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.hasText((String)text)) {
            this.setValue(UUID.fromString(text.trim()));
        } else {
            this.setValue(null);
        }
    }

    @Override
    public String getAsText() {
        UUID value = (UUID)this.getValue();
        return value != null ? value.toString() : "";
    }
}

