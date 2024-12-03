/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.activeobjects.ao;

import com.atlassian.activeobjects.internal.Prefix;
import com.google.common.base.Preconditions;
import net.java.ao.atlassian.TablePrefix;

public final class AtlassianTablePrefix
implements TablePrefix {
    private final Prefix prefix;

    public AtlassianTablePrefix(Prefix prefix) {
        this.prefix = (Prefix)Preconditions.checkNotNull((Object)prefix);
    }

    @Override
    public String prepend(String s) {
        return this.prefix.prepend(s);
    }
}

