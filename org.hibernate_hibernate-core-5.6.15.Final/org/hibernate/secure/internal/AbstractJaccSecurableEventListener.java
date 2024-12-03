/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.secure.internal;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.event.spi.AbstractPreDatabaseOperationEvent;
import org.hibernate.secure.internal.JaccSecurityListener;
import org.hibernate.secure.spi.JaccService;
import org.hibernate.secure.spi.PermissibleAction;
import org.hibernate.secure.spi.PermissionCheckEntityInformation;

@Deprecated
public abstract class AbstractJaccSecurableEventListener
implements JaccSecurityListener {
    private JaccService jaccService;

    protected void performSecurityCheck(AbstractPreDatabaseOperationEvent event, PermissibleAction action) {
        this.performSecurityCheck(event.getSession(), event, action);
    }

    protected void performSecurityCheck(SessionImplementor session, PermissionCheckEntityInformation entityInformation, PermissibleAction action) {
        if (this.jaccService == null) {
            this.jaccService = session.getFactory().getServiceRegistry().getService(JaccService.class);
        }
        this.jaccService.checkPermission(entityInformation, action);
    }
}

