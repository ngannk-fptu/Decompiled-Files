/*
 * Decompiled with CFR 0.152.
 */
package net.sf.cglib.proxy;

import java.lang.reflect.Method;

public interface CallbackFilter {
    public int accept(Method var1);

    public boolean equals(Object var1);
}

