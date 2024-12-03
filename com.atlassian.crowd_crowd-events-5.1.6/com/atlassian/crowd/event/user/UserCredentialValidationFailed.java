/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.PasswordConstraint
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.event.user;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.PasswordConstraint;
import com.atlassian.crowd.event.DirectoryEvent;
import java.util.Collection;
import java.util.Objects;
import javax.annotation.Nullable;

public class UserCredentialValidationFailed
extends DirectoryEvent {
    @Nullable
    private final Collection<PasswordConstraint> failedConstraints;

    public UserCredentialValidationFailed(Object source, Directory directory, @Nullable Collection<PasswordConstraint> failedConstraints) {
        super(source, directory);
        this.failedConstraints = failedConstraints;
    }

    @Nullable
    public Collection<PasswordConstraint> getFailedConstraints() {
        return this.failedConstraints;
    }

    @Nullable
    public String failedReason() {
        return this.failedConstraints != null ? this.failedConstraints.toString() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        UserCredentialValidationFailed that = (UserCredentialValidationFailed)o;
        return Objects.equals(this.failedConstraints, that.failedConstraints);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.failedConstraints);
    }
}

