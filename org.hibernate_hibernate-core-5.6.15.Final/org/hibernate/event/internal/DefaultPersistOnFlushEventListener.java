/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import org.hibernate.engine.spi.CascadingAction;
import org.hibernate.engine.spi.CascadingActions;
import org.hibernate.event.internal.DefaultPersistEventListener;

public class DefaultPersistOnFlushEventListener
extends DefaultPersistEventListener {
    @Override
    protected CascadingAction getCascadeAction() {
        return CascadingActions.PERSIST_ON_FLUSH;
    }
}

