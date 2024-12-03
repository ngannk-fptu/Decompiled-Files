/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.VelocityContext
 *  org.apache.velocity.context.Context
 */
package com.atlassian.confluence.velocity.context;

import com.atlassian.confluence.velocity.context.DefaultValueStackProvider;
import java.util.HashSet;
import java.util.List;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

public final class ChainedVelocityContext
extends VelocityContext
implements DefaultValueStackProvider {
    public ChainedVelocityContext(Context delegate) {
        super(delegate);
    }

    public Object[] getKeys() {
        HashSet<Object> allKeys = new HashSet<Object>(List.of(this.internalGetKeys()));
        if (this.getChainedContext() != null) {
            allKeys.addAll(List.of(this.getChainedContext().getKeys()));
        }
        return allKeys.toArray();
    }
}

