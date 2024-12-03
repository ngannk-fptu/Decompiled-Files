/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.pool;

import net.sf.ehcache.pool.Size;

public interface SizeOfEngine {
    public Size sizeOf(Object var1, Object var2, Object var3);

    public SizeOfEngine copyWith(int var1, boolean var2);
}

