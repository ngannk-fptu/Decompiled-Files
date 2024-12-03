/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.server.sei;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.Packet;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class Invoker {
    public abstract Object invoke(@NotNull Packet var1, @NotNull Method var2, Object ... var3) throws InvocationTargetException, IllegalAccessException;
}

