/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.secure.internal;

import org.hibernate.event.spi.PreLoadEvent;
import org.hibernate.event.spi.PreLoadEventListener;
import org.hibernate.secure.internal.AbstractJaccSecurableEventListener;
import org.hibernate.secure.spi.PermissibleAction;

@Deprecated
public class JaccPreLoadEventListener
extends AbstractJaccSecurableEventListener
implements PreLoadEventListener {
    @Override
    public void onPreLoad(PreLoadEvent event) {
        this.performSecurityCheck(event.getSession(), event, PermissibleAction.READ);
    }
}

