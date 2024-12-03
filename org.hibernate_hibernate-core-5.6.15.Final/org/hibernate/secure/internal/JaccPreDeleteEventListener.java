/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.secure.internal;

import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreDeleteEventListener;
import org.hibernate.secure.internal.AbstractJaccSecurableEventListener;
import org.hibernate.secure.spi.PermissibleAction;

@Deprecated
public class JaccPreDeleteEventListener
extends AbstractJaccSecurableEventListener
implements PreDeleteEventListener {
    @Override
    public boolean onPreDelete(PreDeleteEvent event) {
        this.performSecurityCheck(event, PermissibleAction.DELETE);
        return false;
    }
}

