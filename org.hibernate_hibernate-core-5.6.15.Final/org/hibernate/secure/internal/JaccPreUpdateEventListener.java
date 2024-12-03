/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.secure.internal;

import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.secure.internal.AbstractJaccSecurableEventListener;
import org.hibernate.secure.spi.PermissibleAction;

@Deprecated
public class JaccPreUpdateEventListener
extends AbstractJaccSecurableEventListener
implements PreUpdateEventListener {
    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        this.performSecurityCheck(event, PermissibleAction.UPDATE);
        return false;
    }
}

