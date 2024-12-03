/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.runtime.reflect;

import java.lang.reflect.Modifier;

class StringMaker {
    boolean shortTypeNames = true;
    boolean includeArgs = true;
    boolean includeThrows = false;
    boolean includeModifiers = false;
    boolean shortPrimaryTypeNames = false;
    boolean includeJoinPointTypeName = true;
    boolean includeEnclosingPoint = true;
    boolean shortKindName = true;
    int cacheOffset;
    static StringMaker shortStringMaker = new StringMaker();
    static StringMaker middleStringMaker;
    static StringMaker longStringMaker;

    StringMaker() {
    }

    String makeKindName(String name) {
        int dash = name.lastIndexOf(45);
        if (dash == -1) {
            return name;
        }
        return name.substring(dash + 1);
    }

    String makeModifiersString(int modifiers) {
        if (!this.includeModifiers) {
            return "";
        }
        String str = Modifier.toString(modifiers);
        if (str.length() == 0) {
            return "";
        }
        return str + " ";
    }

    String stripPackageName(String name) {
        int dot = name.lastIndexOf(46);
        if (dot == -1) {
            return name;
        }
        return name.substring(dot + 1);
    }

    String makeTypeName(Class type, String typeName, boolean shortName) {
        if (type == null) {
            return "ANONYMOUS";
        }
        if (type.isArray()) {
            Class<?> componentType = type.getComponentType();
            return this.makeTypeName(componentType, componentType.getName(), shortName) + "[]";
        }
        if (shortName) {
            return this.stripPackageName(typeName).replace('$', '.');
        }
        return typeName.replace('$', '.');
    }

    public String makeTypeName(Class type) {
        return this.makeTypeName(type, type.getName(), this.shortTypeNames);
    }

    public String makePrimaryTypeName(Class type, String typeName) {
        return this.makeTypeName(type, typeName, this.shortPrimaryTypeNames);
    }

    public void addTypeNames(StringBuffer buf, Class[] types) {
        for (int i = 0; i < types.length; ++i) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(this.makeTypeName(types[i]));
        }
    }

    public void addSignature(StringBuffer buf, Class[] types) {
        if (types == null) {
            return;
        }
        if (!this.includeArgs) {
            if (types.length == 0) {
                buf.append("()");
                return;
            }
            buf.append("(..)");
            return;
        }
        buf.append("(");
        this.addTypeNames(buf, types);
        buf.append(")");
    }

    public void addThrows(StringBuffer buf, Class[] types) {
        if (!this.includeThrows || types == null || types.length == 0) {
            return;
        }
        buf.append(" throws ");
        this.addTypeNames(buf, types);
    }

    static {
        StringMaker.shortStringMaker.shortTypeNames = true;
        StringMaker.shortStringMaker.includeArgs = false;
        StringMaker.shortStringMaker.includeThrows = false;
        StringMaker.shortStringMaker.includeModifiers = false;
        StringMaker.shortStringMaker.shortPrimaryTypeNames = true;
        StringMaker.shortStringMaker.includeJoinPointTypeName = false;
        StringMaker.shortStringMaker.includeEnclosingPoint = false;
        StringMaker.shortStringMaker.cacheOffset = 0;
        middleStringMaker = new StringMaker();
        StringMaker.middleStringMaker.shortTypeNames = true;
        StringMaker.middleStringMaker.includeArgs = true;
        StringMaker.middleStringMaker.includeThrows = false;
        StringMaker.middleStringMaker.includeModifiers = false;
        StringMaker.middleStringMaker.shortPrimaryTypeNames = false;
        StringMaker.shortStringMaker.cacheOffset = 1;
        longStringMaker = new StringMaker();
        StringMaker.longStringMaker.shortTypeNames = false;
        StringMaker.longStringMaker.includeArgs = true;
        StringMaker.longStringMaker.includeThrows = false;
        StringMaker.longStringMaker.includeModifiers = true;
        StringMaker.longStringMaker.shortPrimaryTypeNames = false;
        StringMaker.longStringMaker.shortKindName = false;
        StringMaker.longStringMaker.cacheOffset = 2;
    }
}

