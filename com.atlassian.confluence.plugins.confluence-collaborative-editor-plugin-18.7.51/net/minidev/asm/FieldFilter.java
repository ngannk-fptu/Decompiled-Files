/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.asm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface FieldFilter {
    public boolean canUse(Field var1);

    public boolean canUse(Field var1, Method var2);

    public boolean canRead(Field var1);

    public boolean canWrite(Field var1);
}

