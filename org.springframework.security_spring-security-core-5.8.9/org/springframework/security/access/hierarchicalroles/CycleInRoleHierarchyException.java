/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.access.hierarchicalroles;

public class CycleInRoleHierarchyException
extends RuntimeException {
    private static final long serialVersionUID = -4970510612118296707L;

    public CycleInRoleHierarchyException() {
        super("Exception thrown because of a cycle in the role hierarchy definition!");
    }
}

