/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import org.springframework.lang.Nullable;

public class CharArrayPropertyEditor
extends PropertyEditorSupport {
    @Override
    public void setAsText(@Nullable String text) {
        this.setValue(text != null ? text.toCharArray() : null);
    }

    @Override
    public String getAsText() {
        char[] value = (char[])this.getValue();
        return value != null ? new String(value) : "";
    }
}

