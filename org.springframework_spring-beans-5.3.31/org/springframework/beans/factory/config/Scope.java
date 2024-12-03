/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.beans.factory.config;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.lang.Nullable;

public interface Scope {
    public Object get(String var1, ObjectFactory<?> var2);

    @Nullable
    public Object remove(String var1);

    public void registerDestructionCallback(String var1, Runnable var2);

    @Nullable
    public Object resolveContextualObject(String var1);

    @Nullable
    public String getConversationId();
}

