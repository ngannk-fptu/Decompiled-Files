/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 */
package com.atlassian.crowd.directory.ldap.validator;

import com.atlassian.crowd.directory.ldap.validator.Validator;
import com.atlassian.crowd.embedded.api.Directory;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConnectorValidator {
    private final List<Validator> validators;

    public ConnectorValidator(List<Validator> validators) {
        this.validators = validators;
    }

    public Set<String> getErrors(Directory directory) {
        HashSet<String> errors = new HashSet<String>();
        for (Validator validator : this.validators) {
            String error = validator.getError(directory);
            if (error == null) continue;
            errors.add(error);
        }
        return errors;
    }
}

