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
import com.atlassian.confluence.api.model.permissions.Operation;
import com.atlassian.confluence.api.model.permissions.OperationKey;
import com.atlassian.confluence.api.model.permissions.Target;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

@ExperimentalSpi
public interface OperationCheck
extends Operation {
    @Override
    public @NonNull OperationKey getOperationKey();

    public @NonNull ValidationResult canPerform(Person var1, Target var2);

    public @NonNull ValidationResult canPerformAccordingToState(Person var1, Target var2);

    public @NonNull Map<Target, ValidationResult> canPerform(Person var1, Iterable<Target> var2);

    public @NonNull Map<Target, ValidationResult> canPerformAccordingToState(Person var1, Iterable<Target> var2);
}

