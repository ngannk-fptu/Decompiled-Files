/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.servlet;

import java.io.Serializable;

public interface MessageEmit
extends Serializable {
    public void emit(String var1);

    public void emit(String var1, int var2);

    public void setExceptionPname(String var1);

    public void emit(Throwable var1);

    public void emit(String var1, Object var2);

    public void emit(String var1, Object var2, Object var3);

    public void emit(String var1, Object var2, Object var3, Object var4);

    public void clear();

    public boolean messagesEmitted();
}

