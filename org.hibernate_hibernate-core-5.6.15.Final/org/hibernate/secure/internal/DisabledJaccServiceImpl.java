/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.secure.internal;

import org.hibernate.secure.spi.GrantedPermission;
import org.hibernate.secure.spi.JaccService;
import org.hibernate.secure.spi.PermissibleAction;
import org.hibernate.secure.spi.PermissionCheckEntityInformation;
import org.jboss.logging.Logger;

public class DisabledJaccServiceImpl
implements JaccService {
    private static final Logger log = Logger.getLogger(DisabledJaccServiceImpl.class);

    @Override
    public String getContextId() {
        return null;
    }

    @Override
    public void addPermission(GrantedPermission permissionDeclaration) {
        log.debug((Object)"Ignoring call to addPermission on disabled JACC service");
    }

    @Override
    public void checkPermission(PermissionCheckEntityInformation entityInformation, PermissibleAction action) {
        log.debug((Object)"Ignoring call to checkPermission on disabled JACC service");
    }
}

