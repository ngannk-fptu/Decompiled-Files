/*
 * Decompiled with CFR 0.152.
 */
package net.sf.cglib.core;

import net.sf.cglib.core.Predicate;

public interface NamingPolicy {
    public String getClassName(String var1, String var2, Object var3, Predicate var4);

    public boolean equals(Object var1);
}

