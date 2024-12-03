/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public class CharacterEditor
extends PropertyEditorSupport {
    private static final String UNICODE_PREFIX = "\\u";
    private static final int UNICODE_LENGTH = 6;
    private final boolean allowEmpty;

    public CharacterEditor(boolean allowEmpty) {
        this.allowEmpty = allowEmpty;
    }

    @Override
    public void setAsText(@Nullable String text) throws IllegalArgumentException {
        if (this.allowEmpty && !StringUtils.hasLength(text)) {
            this.setValue(null);
        } else {
            if (text == null) {
                throw new IllegalArgumentException("null String cannot be converted to char type");
            }
            if (this.isUnicodeCharacterSequence(text)) {
                this.setAsUnicode(text);
            } else if (text.length() == 1) {
                this.setValue(Character.valueOf(text.charAt(0)));
            } else {
                throw new IllegalArgumentException("String [" + text + "] with length " + text.length() + " cannot be converted to char type: neither Unicode nor single character");
            }
        }
    }

    @Override
    public String getAsText() {
        Object value = this.getValue();
        return value != null ? value.toString() : "";
    }

    private boolean isUnicodeCharacterSequence(String sequence) {
        return sequence.startsWith(UNICODE_PREFIX) && sequence.length() == 6;
    }

    private void setAsUnicode(String text) {
        int code = Integer.parseInt(text.substring(UNICODE_PREFIX.length()), 16);
        this.setValue(Character.valueOf((char)code));
    }
}

