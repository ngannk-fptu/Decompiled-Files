/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.validator.ValidationError
 */
package com.atlassian.crowd.embedded.validator;

import com.atlassian.crowd.validator.ValidationError;
import java.util.List;

public interface Validator<T> {
    public List<ValidationError> validate(T var1);
}

