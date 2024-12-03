/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.secure.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.hibernate.secure.spi.GrantedPermission;

@Deprecated
public class JaccPermissionDeclarations {
    private final String contextId;
    private List<GrantedPermission> permissionDeclarations;

    public JaccPermissionDeclarations(String contextId) {
        this.contextId = contextId;
    }

    public String getContextId() {
        return this.contextId;
    }

    public void addPermissionDeclaration(GrantedPermission permissionDeclaration) {
        if (this.permissionDeclarations == null) {
            this.permissionDeclarations = new ArrayList<GrantedPermission>();
        }
        this.permissionDeclarations.add(permissionDeclaration);
    }

    public void addPermissionDeclarations(Collection<GrantedPermission> permissionDeclarations) {
        if (this.permissionDeclarations == null) {
            this.permissionDeclarations = new ArrayList<GrantedPermission>();
        }
        this.permissionDeclarations.addAll(permissionDeclarations);
    }

    public Collection<GrantedPermission> getPermissionDeclarations() {
        return this.permissionDeclarations;
    }
}

