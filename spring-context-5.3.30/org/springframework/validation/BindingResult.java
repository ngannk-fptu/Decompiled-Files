/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.PropertyEditorRegistry
 *  org.springframework.lang.Nullable
 */
package org.springframework.validation;

import java.beans.PropertyEditor;
import java.util.Map;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.lang.Nullable;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

public interface BindingResult
extends Errors {
    public static final String MODEL_KEY_PREFIX = BindingResult.class.getName() + ".";

    @Nullable
    public Object getTarget();

    public Map<String, Object> getModel();

    @Nullable
    public Object getRawFieldValue(String var1);

    @Nullable
    public PropertyEditor findEditor(@Nullable String var1, @Nullable Class<?> var2);

    @Nullable
    public PropertyEditorRegistry getPropertyEditorRegistry();

    public String[] resolveMessageCodes(String var1);

    public String[] resolveMessageCodes(String var1, String var2);

    public void addError(ObjectError var1);

    default public void recordFieldValue(String field, Class<?> type, @Nullable Object value) {
    }

    default public void recordSuppressedField(String field) {
    }

    default public String[] getSuppressedFields() {
        return new String[0];
    }
}

