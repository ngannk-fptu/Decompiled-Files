/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.util.regex.Pattern;
import org.springframework.lang.Nullable;

public class PatternEditor
extends PropertyEditorSupport {
    private final int flags;

    public PatternEditor() {
        this.flags = 0;
    }

    public PatternEditor(int flags) {
        this.flags = flags;
    }

    @Override
    public void setAsText(@Nullable String text) {
        this.setValue(text != null ? Pattern.compile(text, this.flags) : null);
    }

    @Override
    public String getAsText() {
        Pattern value = (Pattern)this.getValue();
        return value != null ? value.pattern() : "";
    }
}

