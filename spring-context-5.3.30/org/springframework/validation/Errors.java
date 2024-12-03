/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.validation;

import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public interface Errors {
    public static final String NESTED_PATH_SEPARATOR = ".";

    public String getObjectName();

    public void setNestedPath(String var1);

    public String getNestedPath();

    public void pushNestedPath(String var1);

    public void popNestedPath() throws IllegalStateException;

    public void reject(String var1);

    public void reject(String var1, String var2);

    public void reject(String var1, @Nullable Object[] var2, @Nullable String var3);

    public void rejectValue(@Nullable String var1, String var2);

    public void rejectValue(@Nullable String var1, String var2, String var3);

    public void rejectValue(@Nullable String var1, String var2, @Nullable Object[] var3, @Nullable String var4);

    public void addAllErrors(Errors var1);

    public boolean hasErrors();

    public int getErrorCount();

    public List<ObjectError> getAllErrors();

    public boolean hasGlobalErrors();

    public int getGlobalErrorCount();

    public List<ObjectError> getGlobalErrors();

    @Nullable
    public ObjectError getGlobalError();

    public boolean hasFieldErrors();

    public int getFieldErrorCount();

    public List<FieldError> getFieldErrors();

    @Nullable
    public FieldError getFieldError();

    public boolean hasFieldErrors(String var1);

    public int getFieldErrorCount(String var1);

    public List<FieldError> getFieldErrors(String var1);

    @Nullable
    public FieldError getFieldError(String var1);

    @Nullable
    public Object getFieldValue(String var1);

    @Nullable
    public Class<?> getFieldType(String var1);
}

