/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.OperationType
 */
package com.atlassian.crowd.embedded.admin.crowd;

import com.atlassian.crowd.embedded.api.OperationType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum CrowdPermissionOption {
    READ_ONLY(OperationType.UPDATE_USER_ATTRIBUTE, OperationType.UPDATE_GROUP_ATTRIBUTE),
    READ_WRITE(OperationType.values());

    private final List<OperationType> operationTypes;

    private CrowdPermissionOption(OperationType ... operationTypes) {
        this.operationTypes = Arrays.asList(operationTypes);
    }

    public Set<OperationType> getAllowedOperations() {
        return new HashSet<OperationType>(this.operationTypes);
    }

    public static CrowdPermissionOption fromAllowedOperations(Set<OperationType> allowedOperations) {
        if (allowedOperations.containsAll(CrowdPermissionOption.READ_WRITE.operationTypes)) {
            return READ_WRITE;
        }
        return READ_ONLY;
    }
}

