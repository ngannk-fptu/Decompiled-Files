/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.config.validate;

import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.instrument.config.validate.Validated;
import java.util.stream.Collectors;

@Incubating(since="1.5.0")
public class ValidationException
extends IllegalStateException {
    private final Validated<?> validation;

    public ValidationException(Validated<?> validation) {
        super(validation.failures().stream().map(invalid -> invalid.getProperty() + " was '" + (invalid.getValue() == null ? "null" : invalid.getValue()) + "' but it " + invalid.getMessage()).collect(Collectors.joining("\n", validation.failures().size() > 1 ? "Multiple validation failures:\n" : "", "")));
        this.validation = validation;
    }

    public Validated<?> getValidation() {
        return this.validation;
    }
}

