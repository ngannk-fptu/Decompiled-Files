/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.context;

import org.apache.velocity.context.Context;
import org.apache.velocity.context.InternalContextAdapter;

public interface InternalWrapperContext {
    public Context getInternalUserContext();

    public InternalContextAdapter getBaseContext();

    public Object localPut(String var1, Object var2);
}

