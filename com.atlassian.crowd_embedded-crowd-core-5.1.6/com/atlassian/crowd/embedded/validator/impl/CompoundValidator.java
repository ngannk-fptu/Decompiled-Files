/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.validator.ValidationError
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.crowd.embedded.validator.impl;

import com.atlassian.crowd.embedded.validator.Validator;
import com.atlassian.crowd.validator.ValidationError;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CompoundValidator<T>
implements Validator<T> {
    private final List<Validator<T>> validators;

    public CompoundValidator(List<Validator<T>> validators) {
        if (validators == null || validators.size() == 0) {
            throw new IllegalArgumentException("At least one validator instance is required");
        }
        this.validators = ImmutableList.copyOf(validators);
    }

    @Override
    public List<ValidationError> validate(T entity) {
        return this.validators.stream().map(validator -> validator.validate(entity)).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public List<Validator<T>> getValidators() {
        return this.validators;
    }
}

