/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.utility;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Invoker {
    public Object newInstance(Constructor<?> var1, Object[] var2) throws InstantiationException, IllegalAccessException, InvocationTargetException;

    @MaybeNull
    public Object invoke(Method var1, @MaybeNull Object var2, @MaybeNull Object[] var3) throws IllegalAccessException, InvocationTargetException;
}

