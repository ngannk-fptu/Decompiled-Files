/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

@Deprecated
public interface ContextListener
extends ContextFactory.Listener {
    @Deprecated
    public void contextEntered(Context var1);

    @Deprecated
    public void contextExited(Context var1);
}

