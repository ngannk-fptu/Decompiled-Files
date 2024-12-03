/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface ClassMemberAccessPolicy {
    public boolean isMethodExposed(Method var1);

    public boolean isConstructorExposed(Constructor<?> var1);

    public boolean isFieldExposed(Field var1);
}

