/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalSpi
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.api.model.permissions.spi;

import com.atlassian.annotations.ExperimentalSpi;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.permissions.OperationKey;
import com.atlassian.confluence.api.model.permissions.Target;
import com.atlassian.confluence.api.model.permissions.TargetType;
import com.atlassian.confluence.api.model.permissions.spi.OperationCheck;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.StreamSupport;
import org.checkerframework.checker.nullness.qual.NonNull;

@ExperimentalSpi
public abstract class BaseOperationCheck
implements OperationCheck {
    private final OperationKey operationKey;
    private final TargetType expectedTargetType;

    protected BaseOperationCheck(OperationKey operationKey, TargetType expectedTargetType) {
        this.operationKey = Objects.requireNonNull(operationKey);
        this.expectedTargetType = Objects.requireNonNull(expectedTargetType);
    }

    @Override
    public final @NonNull OperationKey getOperationKey() {
        return this.operationKey;
    }

    @Override
    public final @NonNull ValidationResult canPerform(Person person, Target target) {
        Map<Target, ValidationResult> map = this.canPerform(person, Collections.singleton(target));
        ValidationResult result = map.get(target);
        if (result == null || map.size() != 1) {
            throw new IllegalStateException(String.format("OperationCheck subclass returned unexpected map contents for singleton canPerform: %s", map));
        }
        return result;
    }

    @Override
    public final @NonNull ValidationResult canPerformAccordingToState(Person person, Target target) {
        Map<Target, ValidationResult> map = this.canPerformAccordingToState(person, Collections.singleton(target));
        ValidationResult result = map.get(target);
        if (result == null || map.size() != 1) {
            throw new IllegalStateException(String.format("OperationCheck subclass returned unexpected map contents for singleton canPerform: %s", map));
        }
        return result;
    }

    @Override
    public final @NonNull Map<Target, ValidationResult> canPerform(Person person, Iterable<Target> targets) {
        this.validateCanPerformParams(person, targets);
        for (Target target : targets) {
            Objects.requireNonNull(target);
            if (this.expectedTargetType.equals(target.getTargetType())) continue;
            throw new IllegalArgumentException(String.format("Unsupported TargetType '%s' for operation '%s' on target: %s", target.getTargetType(), this.getOperationKey(), target));
        }
        return this.canPerformImpl(person, targets);
    }

    @Override
    public final @NonNull Map<Target, ValidationResult> canPerformAccordingToState(Person person, Iterable<Target> targets) {
        this.validateCanPerformParams(person, targets);
        for (Target target : targets) {
            Objects.requireNonNull(target);
            if (this.expectedTargetType.equals(target.getTargetType())) continue;
            throw new IllegalArgumentException(String.format("Unsupported TargetType '%s' for operation '%s' on target: %s", target.getTargetType(), this.getOperationKey(), target));
        }
        return this.canPerformAccordingToStateImpl(person, targets);
    }

    private void validateCanPerformParams(Person person, Iterable<Target> targets) {
        Objects.requireNonNull(person);
        Objects.requireNonNull(targets);
        Target firstTarget = StreamSupport.stream(targets.spliterator(), false).findFirst().orElse(null);
        if (firstTarget == null) {
            throw new IllegalArgumentException("At least one target must be supplied");
        }
        boolean isAllTargetHaveSameClass = StreamSupport.stream(targets.spliterator(), false).allMatch(target -> firstTarget.getClass().equals(target.getClass()));
        if (!isAllTargetHaveSameClass) {
            throw new IllegalArgumentException("All targets must belong to the same concrete Target class");
        }
    }

    protected abstract @NonNull Map<Target, ValidationResult> canPerformImpl(@NonNull Person var1, @NonNull Iterable<Target> var2);

    protected abstract @NonNull Map<Target, ValidationResult> canPerformAccordingToStateImpl(@NonNull Person var1, @NonNull Iterable<Target> var2);
}

