/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.secure.spi;

import org.hibernate.secure.spi.PermissibleAction;

@Deprecated
public class GrantedPermission {
    private final String role;
    private final String entityName;
    private final PermissibleAction action;

    public GrantedPermission(String role, String entityName, String action) {
        this.role = role;
        this.entityName = entityName;
        this.action = PermissibleAction.interpret(action);
    }

    public String getRole() {
        return this.role;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public PermissibleAction getPermissibleAction() {
        return this.action;
    }
}

