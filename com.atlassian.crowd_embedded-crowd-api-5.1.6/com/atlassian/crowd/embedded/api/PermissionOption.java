/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.embedded.api;

import com.atlassian.crowd.embedded.api.OperationType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum PermissionOption {
    READ_ONLY(OperationType.UPDATE_USER_ATTRIBUTE, OperationType.UPDATE_GROUP_ATTRIBUTE),
    READ_ONLY_LOCAL_GROUPS(OperationType.UPDATE_USER_ATTRIBUTE, OperationType.UPDATE_GROUP_ATTRIBUTE, OperationType.CREATE_GROUP, OperationType.UPDATE_GROUP, OperationType.DELETE_GROUP),
    READ_WRITE(OperationType.values());

    private final List<OperationType> operationTypes;

    private PermissionOption(OperationType ... operationTypes) {
        this.operationTypes = Arrays.asList(operationTypes);
    }

    public Set<OperationType> getAllowedOperations() {
        return new HashSet<OperationType>(this.operationTypes);
    }

    public static PermissionOption fromAllowedOperations(Set<OperationType> allowedOperations) {
        if (allowedOperations.containsAll(PermissionOption.READ_WRITE.operationTypes)) {
            return READ_WRITE;
        }
        if (allowedOperations.containsAll(PermissionOption.READ_ONLY_LOCAL_GROUPS.operationTypes)) {
            return READ_ONLY_LOCAL_GROUPS;
        }
        return READ_ONLY;
    }
}

