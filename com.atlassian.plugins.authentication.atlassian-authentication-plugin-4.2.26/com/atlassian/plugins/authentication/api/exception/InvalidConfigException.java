/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 */
package com.atlassian.plugins.authentication.api.exception;

import com.atlassian.plugins.authentication.api.config.ValidationError;
import com.google.common.collect.Multimap;

public class InvalidConfigException
extends RuntimeException {
    private final Multimap<String, ValidationError> errorsOnFields;

    public InvalidConfigException(Multimap<String, ValidationError> errorsOnFields) {
        this.errorsOnFields = errorsOnFields;
    }

    public Multimap<String, ValidationError> getErrorsOnFields() {
        return this.errorsOnFields;
    }
}

