/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.tags.form;

import java.beans.PropertyEditor;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.web.util.HtmlUtils;

abstract class ValueFormatter {
    ValueFormatter() {
    }

    public static String getDisplayString(@Nullable Object value, boolean htmlEscape) {
        String displayValue = ObjectUtils.getDisplayString(value);
        return htmlEscape ? HtmlUtils.htmlEscape(displayValue) : displayValue;
    }

    public static String getDisplayString(@Nullable Object value, @Nullable PropertyEditor propertyEditor, boolean htmlEscape) {
        if (propertyEditor != null && !(value instanceof String)) {
            try {
                propertyEditor.setValue(value);
                String text = propertyEditor.getAsText();
                if (text != null) {
                    return ValueFormatter.getDisplayString(text, htmlEscape);
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return ValueFormatter.getDisplayString(value, htmlEscape);
    }
}

