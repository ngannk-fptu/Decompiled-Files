/*
 * Decompiled with CFR 0.152.
 */
package javax.validation;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;

public class ConstraintViolationException
extends ValidationException {
    private final Set<ConstraintViolation<?>> constraintViolations;

    public ConstraintViolationException(String message, Set<? extends ConstraintViolation<?>> constraintViolations) {
        super(message);
        this.constraintViolations = constraintViolations == null ? null : new HashSet(constraintViolations);
    }

    public ConstraintViolationException(Set<? extends ConstraintViolation<?>> constraintViolations) {
        this(constraintViolations != null ? ConstraintViolationException.toString(constraintViolations) : null, constraintViolations);
    }

    public Set<ConstraintViolation<?>> getConstraintViolations() {
        return this.constraintViolations;
    }

    private static String toString(Set<? extends ConstraintViolation<?>> constraintViolations) {
        return constraintViolations.stream().map(cv -> cv == null ? "null" : cv.getPropertyPath() + ": " + cv.getMessage()).collect(Collectors.joining(", "));
    }
}

