/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.api.service.permissions;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.permissions.Operation;
import com.atlassian.confluence.api.model.permissions.OperationCheckResult;
import com.atlassian.confluence.api.model.permissions.OperationDescription;
import com.atlassian.confluence.api.model.permissions.Target;
import com.atlassian.confluence.api.model.permissions.TargetType;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;

@ExperimentalApi
@ParametersAreNonnullByDefault
public interface OperationService {
    public @NonNull List<OperationDescription> getAllOperationsForType(TargetType var1);

    public @NonNull List<OperationCheckResult> getAvailableOperations(Person var1, Target var2);

    public @NonNull List<OperationCheckResult> getAvailableOperations(Target var1);

    public @NonNull ValidationResult canPerform(Person var1, Operation var2, Target var3);

    public @NonNull ValidationResult canPerformWithoutExemptions(Person var1, Operation var2, Target var3);

    public @NonNull Map<Target, ValidationResult> canPerform(Person var1, Operation var2, Iterable<Target> var3);

    public @NonNull Map<Target, ValidationResult> canPerformWithoutExemptions(Person var1, Operation var2, Iterable<Target> var3);

    public <T> T withExemption(Supplier<T> var1);
}

