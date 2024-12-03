/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.plugins.tasklist.macro.validator;

import com.atlassian.confluence.plugins.tasklist.macro.validator.AbstractValidator;
import com.atlassian.confluence.plugins.tasklist.macro.validator.ValidatedErrorType;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TasksReportValidator {
    private List<AbstractValidator> validators = new ArrayList<AbstractValidator>();
    private List<ValidatedErrorType> errors = new ArrayList<ValidatedErrorType>();

    public TasksReportValidator addValidator(AbstractValidator validator) {
        this.validators.add(validator);
        return this;
    }

    public TasksReportValidator addValidators(AbstractValidator ... validators) {
        this.validators.addAll(Arrays.asList(validators));
        return this;
    }

    public List<AbstractValidator> getValidators() {
        return this.validators;
    }

    public boolean validate() {
        for (AbstractValidator validator : this.validators) {
            if (validator.validate()) continue;
            this.errors.add(validator.getError());
        }
        return this.errors.isEmpty();
    }

    public List<ValidatedErrorType> getErrors() {
        return this.errors;
    }

    public TasksReportValidator clear() {
        this.errors = Lists.newArrayList();
        this.validators = Lists.newArrayList();
        return this;
    }
}

