/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.client.rest.resource.validator;

import com.atlassian.oauth2.common.rest.validator.ErrorCollection;
import java.util.Objects;
import javax.annotation.Nonnull;

public class ValidationException
extends Exception {
    private final ErrorCollection errorCollection;

    public ValidationException(@Nonnull ErrorCollection errorCollection) {
        this.errorCollection = Objects.requireNonNull(errorCollection);
    }

    @Nonnull
    public ErrorCollection getErrorCollection() {
        return this.errorCollection;
    }
}

