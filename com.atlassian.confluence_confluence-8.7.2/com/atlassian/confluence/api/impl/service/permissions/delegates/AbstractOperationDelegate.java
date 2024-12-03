/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.permissions.OperationKey
 *  com.atlassian.confluence.api.model.permissions.Target
 *  com.atlassian.confluence.api.model.permissions.TargetType
 *  com.atlassian.confluence.api.model.permissions.spi.BaseOperationCheck
 *  com.atlassian.confluence.api.model.permissions.spi.OperationCheck
 *  com.atlassian.confluence.api.model.permissions.spi.OperationDelegate
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.user.User
 *  com.atlassian.util.profiling.Timers
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 */
package com.atlassian.confluence.api.impl.service.permissions.delegates;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.permissions.OperationKey;
import com.atlassian.confluence.api.model.permissions.Target;
import com.atlassian.confluence.api.model.permissions.TargetType;
import com.atlassian.confluence.api.model.permissions.spi.BaseOperationCheck;
import com.atlassian.confluence.api.model.permissions.spi.OperationCheck;
import com.atlassian.confluence.api.model.permissions.spi.OperationDelegate;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.internal.permissions.TargetResolver;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.user.User;
import com.atlassian.util.profiling.Timers;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
abstract class AbstractOperationDelegate
implements OperationDelegate {
    private final ConfluenceUserResolver confluenceUserResolver;
    protected final TargetResolver targetResolver;
    private final List<OperationCheck> allOperations;
    private final Map<OperationKey, OperationCheck> operationChecks;

    protected AbstractOperationDelegate(ConfluenceUserResolver confluenceUserResolver, TargetResolver targetResolver) {
        this.confluenceUserResolver = (ConfluenceUserResolver)Preconditions.checkNotNull((Object)confluenceUserResolver);
        this.targetResolver = (TargetResolver)Preconditions.checkNotNull((Object)targetResolver);
        this.allOperations = this.makeOperations();
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (OperationCheck operationCheck : this.allOperations) {
            builder.put((Object)operationCheck.getOperationKey(), (Object)operationCheck);
        }
        this.operationChecks = builder.build();
    }

    protected String getDebugString(OperationKey operationKey, String message, Target target, User user, Logger log) {
        if (!Timers.getConfiguration().isEnabled() && !log.isDebugEnabled()) {
            return "()";
        }
        return MessageFormat.format("(operation:{0}, user:{1}, {2}, message: {3})", operationKey.getValue(), user != null ? user.getName() : "anonymous", target != null ? target.toString() : "Null Target", message);
    }

    protected abstract List<OperationCheck> makeOperations();

    public final List<OperationCheck> getAllOperations() {
        return this.allOperations;
    }

    public final OperationCheck getOperation(OperationKey opKey) {
        Preconditions.checkNotNull((Object)opKey);
        return this.operationChecks.get(opKey);
    }

    @ParametersAreNonnullByDefault
    protected abstract class ConfluenceUserBaseOperationCheck
    extends BaseOperationCheck {
        protected ConfluenceUserBaseOperationCheck(OperationKey operationKey, TargetType expectedTargetType) {
            super(operationKey, expectedTargetType);
        }

        protected abstract ValidationResult canPerform(ConfluenceUser var1, Target var2);

        protected @NonNull Map<Target, ValidationResult> canPerformImpl(Person person, Iterable<Target> targets) {
            ConfluenceUser user = AbstractOperationDelegate.this.confluenceUserResolver.getExistingUserByPerson(person);
            ImmutableMap.Builder builder = ImmutableMap.builder();
            for (Target target : targets) {
                builder.put((Object)target, (Object)this.canPerform(user, target));
            }
            return builder.build();
        }

        protected abstract ValidationResult canPerformAccordingToState(ConfluenceUser var1, Target var2);

        protected final @NonNull Map<Target, ValidationResult> canPerformAccordingToStateImpl(Person person, Iterable<Target> targets) {
            ConfluenceUser user = AbstractOperationDelegate.this.confluenceUserResolver.getExistingUserByPerson(person);
            ImmutableMap.Builder builder = ImmutableMap.builder();
            for (Target target : targets) {
                builder.put((Object)target, (Object)this.canPerformAccordingToState(user, target));
            }
            return builder.build();
        }
    }
}

