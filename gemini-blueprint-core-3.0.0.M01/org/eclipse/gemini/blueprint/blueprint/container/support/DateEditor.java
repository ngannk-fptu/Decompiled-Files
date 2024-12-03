/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.blueprint.container.support;

import java.beans.PropertyEditorSupport;
import java.util.Date;

public class DateEditor
extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        this.setValue(new Date(text));
    }

    @Override
    public String getAsText() {
        Date value = (Date)this.getValue();
        return value.toString();
    }
}

