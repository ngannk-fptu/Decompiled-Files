/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.validator.ValidationError
 */
package com.atlassian.crowd.embedded.validator;

import com.atlassian.crowd.validator.ValidationError;
import java.util.function.Function;

public interface ValidationRule<T>
extends Function<T, ValidationError> {
}

