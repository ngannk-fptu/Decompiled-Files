/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.asm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minidev.asm.FieldFilter;

public class BasicFiledFilter
implements FieldFilter {
    public static final BasicFiledFilter SINGLETON = new BasicFiledFilter();

    @Override
    public boolean canUse(Field field) {
        return true;
    }

    @Override
    public boolean canUse(Field field, Method method) {
        return true;
    }

    @Override
    public boolean canRead(Field field) {
        return true;
    }

    @Override
    public boolean canWrite(Field field) {
        return true;
    }
}

