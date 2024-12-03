/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 */
package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public class CustomBooleanEditor
extends PropertyEditorSupport {
    public static final String VALUE_TRUE = "true";
    public static final String VALUE_FALSE = "false";
    public static final String VALUE_ON = "on";
    public static final String VALUE_OFF = "off";
    public static final String VALUE_YES = "yes";
    public static final String VALUE_NO = "no";
    public static final String VALUE_1 = "1";
    public static final String VALUE_0 = "0";
    @Nullable
    private final String trueString;
    @Nullable
    private final String falseString;
    private final boolean allowEmpty;

    public CustomBooleanEditor(boolean allowEmpty) {
        this(null, null, allowEmpty);
    }

    public CustomBooleanEditor(@Nullable String trueString, @Nullable String falseString, boolean allowEmpty) {
        this.trueString = trueString;
        this.falseString = falseString;
        this.allowEmpty = allowEmpty;
    }

    @Override
    public void setAsText(@Nullable String text) throws IllegalArgumentException {
        String input;
        String string = input = text != null ? text.trim() : null;
        if (this.allowEmpty && !StringUtils.hasLength((String)input)) {
            this.setValue(null);
        } else if (this.trueString != null && this.trueString.equalsIgnoreCase(input)) {
            this.setValue(Boolean.TRUE);
        } else if (this.falseString != null && this.falseString.equalsIgnoreCase(input)) {
            this.setValue(Boolean.FALSE);
        } else if (this.trueString == null && (VALUE_TRUE.equalsIgnoreCase(input) || VALUE_ON.equalsIgnoreCase(input) || VALUE_YES.equalsIgnoreCase(input) || VALUE_1.equals(input))) {
            this.setValue(Boolean.TRUE);
        } else if (this.falseString == null && (VALUE_FALSE.equalsIgnoreCase(input) || VALUE_OFF.equalsIgnoreCase(input) || VALUE_NO.equalsIgnoreCase(input) || VALUE_0.equals(input))) {
            this.setValue(Boolean.FALSE);
        } else {
            throw new IllegalArgumentException("Invalid boolean value [" + text + "]");
        }
    }

    @Override
    public String getAsText() {
        if (Boolean.TRUE.equals(this.getValue())) {
            return this.trueString != null ? this.trueString : VALUE_TRUE;
        }
        if (Boolean.FALSE.equals(this.getValue())) {
            return this.falseString != null ? this.falseString : VALUE_FALSE;
        }
        return "";
    }
}

