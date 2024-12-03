/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.util.I18nHelper
 *  com.atlassian.crowd.validator.ValidationError
 */
package com.atlassian.crowd.embedded.validator;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.validator.ValidationRule;
import com.atlassian.crowd.embedded.validator.Validator;
import com.atlassian.crowd.util.I18nHelper;
import com.atlassian.crowd.validator.ValidationError;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class DirectoryValidator
implements Validator<Directory> {
    private final List<ValidationRule<Directory>> validationRules;

    public DirectoryValidator(I18nHelper i18nHelper) {
        this.validationRules = this.initializeValidators(i18nHelper);
    }

    @Override
    public List<ValidationError> validate(Directory entity) {
        return this.validationRules.stream().map(rule -> (ValidationError)rule.apply(entity)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    protected abstract List<ValidationRule<Directory>> initializeValidators(I18nHelper var1);
}

