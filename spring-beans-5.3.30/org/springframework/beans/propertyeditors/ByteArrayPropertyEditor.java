/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import org.springframework.lang.Nullable;

public class ByteArrayPropertyEditor
extends PropertyEditorSupport {
    @Override
    public void setAsText(@Nullable String text) {
        this.setValue(text != null ? text.getBytes() : null);
    }

    @Override
    public String getAsText() {
        byte[] value = (byte[])this.getValue();
        return value != null ? new String(value) : "";
    }
}

