/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.secure.spi;

import org.hibernate.secure.spi.GrantedPermission;
import org.hibernate.secure.spi.PermissibleAction;
import org.hibernate.secure.spi.PermissionCheckEntityInformation;
import org.hibernate.service.Service;

@Deprecated
public interface JaccService
extends Service {
    public String getContextId();

    public void addPermission(GrantedPermission var1);

    public void checkPermission(PermissionCheckEntityInformation var1, PermissibleAction var2);
}

