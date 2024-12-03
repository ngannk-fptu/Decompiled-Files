/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PasswordConstraint
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.api.ValidatePasswordRequest
 *  com.atlassian.crowd.exception.InvalidCredentialException
 *  com.atlassian.crowd.model.DirectoryEntity
 *  com.atlassian.crowd.model.group.Group
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.directory.InternalDirectoryUtils;
import com.atlassian.crowd.embedded.api.PasswordConstraint;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.api.ValidatePasswordRequest;
import com.atlassian.crowd.exception.InvalidCredentialException;
import com.atlassian.crowd.model.DirectoryEntity;
import com.atlassian.crowd.model.group.Group;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class InternalDirectoryUtilsImpl
implements InternalDirectoryUtils {
    @Override
    public void validateDirectoryForEntity(DirectoryEntity entity, Long directoryId) {
        Validate.notNull((Object)entity, (String)"entity cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)entity.getDirectoryId(), (String)"directoryId of entity cannot be null", (Object[])new Object[0]);
        Validate.isTrue((entity.getDirectoryId() == directoryId.longValue() ? 1 : 0) != 0, (String)"directoryId does not match the directoryId of the InternalDirectory", (Object[])new Object[0]);
    }

    @Override
    public void validateUsername(String username) {
        if (StringUtils.isBlank((CharSequence)username)) {
            throw new IllegalArgumentException("A username must not be null or empty or blank");
        }
    }

    @Override
    public void validateCredential(User user, PasswordCredential credential, Set<PasswordConstraint> passwordConstraints, @Nullable String passwordComplexityMessage) throws InvalidCredentialException {
        Preconditions.checkNotNull((Object)user);
        Preconditions.checkNotNull((Object)credential);
        if (!credential.isEncryptedCredential()) {
            if (StringUtils.isBlank((CharSequence)credential.getCredential())) {
                throw new InvalidCredentialException("You cannot have an empty password");
            }
            ImmutableSet failedConstraints = ImmutableSet.copyOf((Iterable)Iterables.filter(passwordConstraints, InternalDirectoryUtilsImpl.validationFailed(user, credential)));
            if (!failedConstraints.isEmpty()) {
                throw new InvalidCredentialException("The new password does not meet the directory complexity requirements", StringUtils.stripToNull((String)passwordComplexityMessage), (Collection)failedConstraints);
            }
        }
    }

    @Override
    public void validateGroupName(Group group, String groupName) {
        if (StringUtils.isBlank((CharSequence)groupName)) {
            throw new IllegalArgumentException("A group name must not be null or empty or blank");
        }
    }

    private static Predicate<PasswordConstraint> validationFailed(final User user, final PasswordCredential password) {
        return new Predicate<PasswordConstraint>(){

            public boolean apply(PasswordConstraint constraint) {
                return !constraint.validate(new ValidatePasswordRequest(password, user));
            }
        };
    }
}

