/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.asm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import net.minidev.asm.ASMUtil;
import net.minidev.asm.FieldFilter;

public class Accessor {
    protected Field field;
    protected Method setter;
    protected Method getter;
    protected int index;
    protected Class<?> type;
    protected Type genericType;
    protected String fieldName;

    public int getIndex() {
        return this.index;
    }

    public boolean isPublic() {
        return this.setter == null && this.getter == null;
    }

    public boolean isEnum() {
        return this.type.isEnum();
    }

    public String getName() {
        return this.fieldName;
    }

    public Class<?> getType() {
        return this.type;
    }

    public Type getGenericType() {
        return this.genericType;
    }

    public boolean isUsable() {
        return this.field != null || this.getter != null || this.setter != null;
    }

    public boolean isReadable() {
        return this.field != null || this.getter != null;
    }

    public boolean isWritable() {
        return this.field != null || this.getter != null;
    }

    public Accessor(Class<?> c, Field field, FieldFilter filter) {
        this.fieldName = field.getName();
        int m = field.getModifiers();
        if ((m & 0x88) > 0) {
            return;
        }
        if ((m & 1) > 0) {
            this.field = field;
        }
        String name = ASMUtil.getSetterName(field.getName());
        try {
            this.setter = c.getDeclaredMethod(name, field.getType());
        }
        catch (Exception exception) {
            // empty catch block
        }
        boolean isBool = field.getType().equals(Boolean.TYPE);
        name = isBool ? ASMUtil.getIsName(field.getName()) : ASMUtil.getGetterName(field.getName());
        try {
            this.getter = c.getDeclaredMethod(name, new Class[0]);
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (this.getter == null && isBool) {
            try {
                this.getter = c.getDeclaredMethod(ASMUtil.getGetterName(field.getName()), new Class[0]);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (this.field == null && this.getter == null && this.setter == null) {
            return;
        }
        if (this.getter != null && !filter.canUse(field, this.getter)) {
            this.getter = null;
        }
        if (this.setter != null && !filter.canUse(field, this.setter)) {
            this.setter = null;
        }
        if (this.getter == null && this.setter == null && this.field == null) {
            return;
        }
        this.type = field.getType();
        this.genericType = field.getGenericType();
    }
}

