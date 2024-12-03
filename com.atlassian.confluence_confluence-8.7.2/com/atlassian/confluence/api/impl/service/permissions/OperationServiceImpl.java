/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.permissions.Operation
 *  com.atlassian.confluence.api.model.permissions.OperationCheckResult
 *  com.atlassian.confluence.api.model.permissions.OperationDescription
 *  com.atlassian.confluence.api.model.permissions.OperationKey
 *  com.atlassian.confluence.api.model.permissions.Target
 *  com.atlassian.confluence.api.model.permissions.TargetType
 *  com.atlassian.confluence.api.model.permissions.spi.OperationCheck
 *  com.atlassian.confluence.api.model.permissions.spi.OperationDelegate
 *  com.atlassian.confluence.api.model.permissions.spi.UnsupportedTargetException
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResults
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.permissions.OperationService
 *  com.atlassian.fugue.Either
 *  com.google.common.base.Function
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.api.impl.service.permissions;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.api.impl.service.content.factory.PersonFactory;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.permissions.Operation;
import com.atlassian.confluence.api.model.permissions.OperationCheckResult;
import com.atlassian.confluence.api.model.permissions.OperationDescription;
import com.atlassian.confluence.api.model.permissions.OperationKey;
import com.atlassian.confluence.api.model.permissions.Target;
import com.atlassian.confluence.api.model.permissions.TargetType;
import com.atlassian.confluence.api.model.permissions.spi.OperationCheck;
import com.atlassian.confluence.api.model.permissions.spi.OperationDelegate;
import com.atlassian.confluence.api.model.permissions.spi.UnsupportedTargetException;
import com.atlassian.confluence.api.model.validation.SimpleValidationResults;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.permissions.OperationService;
import com.atlassian.confluence.impl.security.delegate.ScopesRequestCacheDelegate;
import com.atlassian.confluence.internal.accessmode.AccessModeManager;
import com.atlassian.confluence.internal.security.ThreadLocalPermissionsCacheInternal;
import com.atlassian.confluence.security.PermissionCheckExemptions;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.fugue.Either;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
public class OperationServiceImpl
implements OperationService {
    private final Map<TargetType, OperationDelegate> operationDelegates;
    private final PersonFactory personFactory;
    private final ConfluenceUserResolver confluenceUserResolver;
    private PermissionCheckExemptions permissionCheckExemptions;
    private final AccessModeManager accessModeManager;
    private final ScopesRequestCacheDelegate scopesRequestCacheDelegate;

    public OperationServiceImpl(Map<TargetType, OperationDelegate> operationDelegates, PersonFactory personFactory, ConfluenceUserResolver confluenceUserResolver, PermissionCheckExemptions permissionCheckExemptions, AccessModeManager accessModeManager, ScopesRequestCacheDelegate scopesRequestCacheDelegate) {
        this.permissionCheckExemptions = permissionCheckExemptions;
        this.operationDelegates = ImmutableMap.copyOf(operationDelegates);
        this.personFactory = personFactory;
        this.confluenceUserResolver = confluenceUserResolver;
        this.accessModeManager = accessModeManager;
        this.scopesRequestCacheDelegate = scopesRequestCacheDelegate;
    }

    public @NonNull List<OperationDescription> getAllOperationsForType(TargetType targetType) {
        OperationServiceImpl.throwBadRequestIfNull(targetType, "targetType");
        return this.makeOperationDescriptions(this.getAllOperationChecksForType(targetType), targetType);
    }

    public @NonNull List<OperationCheckResult> getAvailableOperations(Person person, Target target) {
        OperationServiceImpl.throwBadRequestIfNull(person, "person");
        OperationServiceImpl.throwBadRequestIfNull(target, "target");
        TargetType targetType = target.getTargetType();
        boolean exempt = this.isExempt(person);
        Iterable categorizedResults = this.getAllOperationChecksForType(targetType).stream().map(operationCheck -> {
            ValidationResult result;
            OperationKey operationKey = operationCheck.getOperationKey();
            try {
                result = !this.operationAllowedInReadOnlyAccessMode(operationKey) ? SimpleValidationResults.forbiddenResult((String)("Operation " + operationKey + " is not allowed in read only access mode"), (Object[])new Object[0]) : (!this.scopesRequestCacheDelegate.hasPermission(operationKey.getValue(), (Object)target) ? SimpleValidationResults.forbiddenResult((String)("Operation " + operationKey + " is not allowed with current OAuth 2 request."), (Object[])new Object[0]) : (exempt ? operationCheck.canPerformAccordingToState(person, target) : operationCheck.canPerform(person, target)));
            }
            catch (UnsupportedTargetException e) {
                return CategorizedResult.unsupportedTarget(operationCheck);
            }
            return CategorizedResult.of(operationCheck, result);
        }).collect(Collectors.toList());
        Iterable permittedResults = Iterables.filter((Iterable)categorizedResults, (Predicate)new CategoryPredicate(ResultCategory.PERMITTED));
        if (!Iterables.isEmpty((Iterable)permittedResults)) {
            return this.makeOperationCheckResults(permittedResults, target);
        }
        if (Iterables.isEmpty((Iterable)Iterables.filter((Iterable)categorizedResults, (Predicate)new CategoryPredicate(ResultCategory.NOT_PERMITTED_BUT_TARGET_VISIBLE)))) {
            throw new NotFoundException("Target does not exist: " + target);
        }
        return ImmutableList.of();
    }

    public @NonNull List<OperationCheckResult> getAvailableOperations(Target target) {
        OperationServiceImpl.throwBadRequestIfNull(target, "target");
        return this.getAvailableOperations(this.getCurrentUserAsPerson(), target);
    }

    public final @NonNull ValidationResult canPerform(Person person, Operation operation, Target target) {
        OperationServiceImpl.throwBadRequestIfNull(person, "person");
        if (this.isExempt(person)) {
            return this.canPerformAccordingToFunction(person, operation, Collections.singleton(target), operationCheck -> (arg_0, arg_1) -> ((OperationCheck)operationCheck).canPerformAccordingToState(arg_0, arg_1)).get(target);
        }
        return this.canPerformAccordingToFunction(person, operation, Collections.singleton(target), operationCheck -> (arg_0, arg_1) -> ((OperationCheck)operationCheck).canPerform(arg_0, arg_1)).get(target);
    }

    public @NonNull ValidationResult canPerformWithoutExemptions(Person person, Operation operation, Target target) {
        return this.canPerformAccordingToFunction(person, operation, Collections.singleton(target), operationCheck -> (arg_0, arg_1) -> ((OperationCheck)operationCheck).canPerform(arg_0, arg_1)).get(target);
    }

    private @NonNull Map<Target, ValidationResult> canPerformAccordingToFunction(Person person, Operation operation, Iterable<Target> targets, java.util.function.Function<OperationCheck, BiFunction<Person, Iterable<Target>, Map<Target, ValidationResult>>> operationCheckMethod) {
        OperationServiceImpl.throwBadRequestIfNull(person, "person");
        OperationServiceImpl.throwBadRequestIfNull(operation, "operation");
        OperationServiceImpl.throwBadRequestIfNull(targets, "targets");
        Iterable<Target> uniqueTargets = this.sanitiseTargetIterable(targets);
        OperationKey operationKey = operation.getOperationKey();
        if (!this.operationAllowedInReadOnlyAccessMode(operationKey)) {
            return this.buildForbiddenValidationResult("Operation " + operationKey + " is not allowed in read only access mode.", uniqueTargets);
        }
        if (!this.scopesRequestCacheDelegate.hasPermission(operationKey.getValue(), null)) {
            return this.buildForbiddenValidationResult("Operation " + operationKey + " is not allowed with current OAuth 2 request.", uniqueTargets);
        }
        TargetType firstTargetType = Objects.requireNonNull((Target)Iterables.getFirst(uniqueTargets, null)).getTargetType();
        Either result = this.callOperationCheck(firstTargetType, operation, operationCheck -> {
            try {
                return (Map)((BiFunction)operationCheckMethod.apply((OperationCheck)operationCheck)).apply(person, uniqueTargets);
            }
            catch (UnsupportedTargetException e) {
                throw new BadRequestException(e.getMessage());
            }
        });
        return (Map)result.right().on(input -> {
            ValidationResult universalResult = (ValidationResult)result.left().get();
            ImmutableMap.Builder builder = ImmutableMap.builder();
            for (Target target : uniqueTargets) {
                builder.put((Object)target, (Object)universalResult);
            }
            return builder.build();
        });
    }

    private ImmutableMap<Target, ValidationResult> buildForbiddenValidationResult(String message, Iterable<Target> uniqueTargets) {
        ValidationResult forbiddenResult = SimpleValidationResults.forbiddenResult((String)message, (Object[])new Object[0]);
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (Target target : uniqueTargets) {
            builder.put((Object)target, (Object)forbiddenResult);
        }
        return builder.build();
    }

    public final @NonNull Map<Target, ValidationResult> canPerform(Person person, Operation operation, Iterable<Target> targets) {
        OperationServiceImpl.throwBadRequestIfNull(person, "person");
        if (this.isExempt(person)) {
            return this.canPerformAccordingToFunction(person, operation, targets, operationCheck -> (arg_0, arg_1) -> ((OperationCheck)operationCheck).canPerformAccordingToState(arg_0, arg_1));
        }
        return this.canPerformAccordingToFunction(person, operation, targets, operationCheck -> (arg_0, arg_1) -> ((OperationCheck)operationCheck).canPerform(arg_0, arg_1));
    }

    private Iterable<Target> sanitiseTargetIterable(Iterable<Target> targets) {
        OperationServiceImpl.throwBadRequestIfNull(targets, "targets");
        if (Iterables.isEmpty(targets)) {
            throw new BadRequestException("At least one target must be supplied");
        }
        TargetType firstTargetType = OperationServiceImpl.throwBadRequestIfNull((Target)Iterables.getFirst(targets, null), "targets").getTargetType();
        for (Target target : Iterables.skip(targets, (int)1)) {
            OperationServiceImpl.throwBadRequestIfNull(target, "items in target list");
            if (firstTargetType.equals((Object)target.getTargetType())) continue;
            throw new BadRequestException("Targets of differing TargetTypes (" + firstTargetType + ", " + target.getTargetType() + "). First that was different: " + target);
        }
        return ImmutableSet.copyOf(targets);
    }

    public @NonNull Map<Target, ValidationResult> canPerformWithoutExemptions(Person person, Operation operation, Iterable<Target> targets) {
        return this.canPerformAccordingToFunction(person, operation, targets, operationCheck -> (arg_0, arg_1) -> ((OperationCheck)operationCheck).canPerform(arg_0, arg_1));
    }

    public <T> T withExemption(Supplier<T> task) {
        if (ThreadLocalPermissionsCacheInternal.hasTemporaryPermissionExemption()) {
            return task.get();
        }
        ThreadLocalPermissionsCacheInternal.enableTemporaryPermissionExemption();
        try {
            T t = task.get();
            return t;
        }
        finally {
            ThreadLocalPermissionsCacheInternal.disableTemporaryPermissionExemption();
        }
    }

    private boolean isExempt(Person person) {
        return this.permissionCheckExemptions.isExempt(this.confluenceUserResolver.getExistingUserByPerson(person));
    }

    private @NonNull List<OperationCheck> getAllOperationChecksForType(@NonNull TargetType targetType) {
        Either either = this.getOperationDelegate(targetType).map(operationDelegate -> {
            List allOperations = operationDelegate.getAllOperations();
            Preconditions.checkState((allOperations != null && !allOperations.isEmpty() ? 1 : 0) != 0, (String)"%s for %s %s provides no operations", (Object)operationDelegate.getClass().getName(), (Object)TargetType.class.getSimpleName(), (Object)targetType);
            return allOperations;
        });
        if (either.isRight()) {
            return (List)either.right().get();
        }
        throw ((ValidationResult)either.left().get()).throwIfInvalid(null);
    }

    private @NonNull List<OperationDescription> makeOperationDescriptions(Iterable<OperationCheck> operationChecks, TargetType targetType) {
        return ImmutableList.builder().addAll((Iterable)StreamSupport.stream(operationChecks.spliterator(), false).map(operationCheck -> OperationDescription.builder().operationKey(operationCheck.getOperationKey()).targetType(targetType).build()).collect(Collectors.toList())).build();
    }

    private @NonNull List<OperationCheckResult> makeOperationCheckResults(Iterable<CategorizedResult> categorizedResults, Target target) {
        return ImmutableList.builder().addAll((Iterable)StreamSupport.stream(categorizedResults.spliterator(), false).map(categorizedResult -> OperationCheckResult.builder().operationKey(categorizedResult.getOperationCheck().getOperationKey()).targetType(target.getTargetType()).build()).collect(Collectors.toList())).build();
    }

    private Either<ValidationResult, OperationDelegate> getOperationDelegate(TargetType targetType) {
        OperationDelegate operationDelegate = this.operationDelegates.get(targetType);
        if (operationDelegate == null) {
            if (TargetType.BUILT_IN.contains(targetType)) {
                return Either.left((Object)SimpleValidationResults.notImplementedResult((String)("No " + OperationDelegate.class.getSimpleName() + " is implemented yet for " + TargetType.class.getSimpleName() + " " + targetType), (Object[])new Object[0]));
            }
            return Either.left((Object)SimpleValidationResults.forbiddenResult((String)("No " + OperationDelegate.class.getSimpleName() + " is currently installed to support " + TargetType.class.getSimpleName() + " " + targetType), (Object[])new Object[0]));
        }
        return Either.right((Object)operationDelegate);
    }

    private <T> @NonNull Either<ValidationResult, T> callOperationCheck(TargetType targetType, Operation operation, Function<OperationCheck, T> callback) {
        return this.getOperationDelegate(targetType).flatMap(operationDelegate -> {
            OperationKey operationKey = operation.getOperationKey();
            OperationCheck operationCheck = operationDelegate.getOperation(operationKey);
            if (operationCheck == null) {
                return Either.left((Object)SimpleValidationResults.notFoundResult((String)(OperationDelegate.class.getSimpleName() + " does not support operation " + operationKey), (Object[])new Object[0]));
            }
            return Either.right((Object)callback.apply((Object)operationCheck));
        });
    }

    private static <T> T throwBadRequestIfNull(@Nullable T argument, String name) {
        if (argument == null) {
            throw new BadRequestException(name + " must not be null");
        }
        return argument;
    }

    private Person getCurrentUserAsPerson() {
        return this.personFactory.forCurrentUser();
    }

    @VisibleForTesting
    boolean operationAllowedInReadOnlyAccessMode(OperationKey operationKey) {
        return !this.accessModeManager.shouldEnforceReadOnlyAccess() || OperationKey.READ_ONLY_WHITELIST.contains(operationKey);
    }

    private static class CategoryPredicate
    implements Predicate<CategorizedResult> {
        private final ResultCategory resultCategory;

        CategoryPredicate(ResultCategory resultCategory) {
            this.resultCategory = resultCategory;
        }

        public boolean apply(@Nullable CategorizedResult categorizedResult) {
            assert (categorizedResult != null);
            return categorizedResult.getResultCategory() == this.resultCategory;
        }
    }

    private static class CategorizedResult {
        private final OperationCheck operationCheck;
        private final ResultCategory resultCategory;

        private CategorizedResult(OperationCheck operationCheck, ResultCategory resultCategory) {
            this.operationCheck = operationCheck;
            this.resultCategory = resultCategory;
        }

        public static CategorizedResult of(OperationCheck operationCheck, ValidationResult validationResult) {
            return new CategorizedResult(operationCheck, ResultCategory.valueOf(validationResult));
        }

        static CategorizedResult unsupportedTarget(OperationCheck operationCheck) {
            return new CategorizedResult(operationCheck, ResultCategory.UNSUPPORTED_TARGET);
        }

        @NonNull OperationCheck getOperationCheck() {
            return this.operationCheck;
        }

        @NonNull ResultCategory getResultCategory() {
            return this.resultCategory;
        }

        public String toString() {
            return MoreObjects.toStringHelper((Object)this).add("operation", (Object)this.operationCheck.getOperationKey()).add("category", (Object)this.resultCategory).toString();
        }
    }

    private static enum ResultCategory {
        PERMITTED,
        NOT_PERMITTED_BUT_TARGET_VISIBLE,
        NOT_FOUND,
        UNSUPPORTED_TARGET;


        public static ResultCategory valueOf(ValidationResult result) {
            try {
                result.throwIfNotSuccessful(null);
                return PERMITTED;
            }
            catch (NotFoundException e) {
                return NOT_FOUND;
            }
            catch (RuntimeException e) {
                return NOT_PERMITTED_BUT_TARGET_VISIBLE;
            }
        }
    }
}

