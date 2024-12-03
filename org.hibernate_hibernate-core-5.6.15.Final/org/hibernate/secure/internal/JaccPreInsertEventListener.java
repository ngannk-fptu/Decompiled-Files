/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.secure.internal;

import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.secure.internal.AbstractJaccSecurableEventListener;
import org.hibernate.secure.spi.PermissibleAction;

@Deprecated
public class JaccPreInsertEventListener
extends AbstractJaccSecurableEventListener
implements PreInsertEventListener {
    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        this.performSecurityCheck(event, PermissibleAction.INSERT);
        return false;
    }
}

