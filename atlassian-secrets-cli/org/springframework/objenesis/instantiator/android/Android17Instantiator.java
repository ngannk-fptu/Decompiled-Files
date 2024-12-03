/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.objenesis.instantiator.android;

import java.io.ObjectStreamClass;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;

@Instantiator(value=Typology.STANDARD)
public class Android17Instantiator<T>
implements ObjectInstantiator<T> {
    private final Class<T> type;
    private final Method newInstanceMethod;
    private final Integer objectConstructorId;

    public Android17Instantiator(Class<T> type) {
        this.type = type;
        this.newInstanceMethod = Android17Instantiator.getNewInstanceMethod();
        this.objectConstructorId = Android17Instantiator.findConstructorIdForJavaLangObjectConstructor();
    }

    @Override
    public T newInstance() {
        try {
            return this.type.cast(this.newInstanceMethod.invoke(null, this.type, this.objectConstructorId));
        }
        catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }

    private static Method getNewInstanceMethod() {
        try {
            Method newInstanceMethod = ObjectStreamClass.class.getDeclaredMethod("newInstance", Class.class, Integer.TYPE);
            newInstanceMethod.setAccessible(true);
            return newInstanceMethod;
        }
        catch (RuntimeException e) {
            throw new ObjenesisException(e);
        }
        catch (NoSuchMethodException e) {
            throw new ObjenesisException(e);
        }
    }

    private static Integer findConstructorIdForJavaLangObjectConstructor() {
        try {
            Method newInstanceMethod = ObjectStreamClass.class.getDeclaredMethod("getConstructorId", Class.class);
            newInstanceMethod.setAccessible(true);
            return (Integer)newInstanceMethod.invoke(null, Object.class);
        }
        catch (RuntimeException e) {
            throw new ObjenesisException(e);
        }
        catch (NoSuchMethodException e) {
            throw new ObjenesisException(e);
        }
        catch (IllegalAccessException e) {
            throw new ObjenesisException(e);
        }
        catch (InvocationTargetException e) {
            throw new ObjenesisException(e);
        }
    }
}

