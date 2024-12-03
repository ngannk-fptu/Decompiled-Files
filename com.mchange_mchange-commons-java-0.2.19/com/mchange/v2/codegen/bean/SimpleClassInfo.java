/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.codegen.bean;

import com.mchange.v2.codegen.bean.ClassInfo;

public class SimpleClassInfo
implements ClassInfo {
    String packageName;
    int modifiers;
    String className;
    String superclassName;
    String[] interfaceNames;
    String[] generalImports;
    String[] specificImports;

    @Override
    public String getPackageName() {
        return this.packageName;
    }

    @Override
    public int getModifiers() {
        return this.modifiers;
    }

    @Override
    public String getClassName() {
        return this.className;
    }

    @Override
    public String getSuperclassName() {
        return this.superclassName;
    }

    @Override
    public String[] getInterfaceNames() {
        return this.interfaceNames;
    }

    @Override
    public String[] getGeneralImports() {
        return this.generalImports;
    }

    @Override
    public String[] getSpecificImports() {
        return this.specificImports;
    }

    public SimpleClassInfo(String string, int n, String string2, String string3, String[] stringArray, String[] stringArray2, String[] stringArray3) {
        this.packageName = string;
        this.modifiers = n;
        this.className = string2;
        this.superclassName = string3;
        this.interfaceNames = stringArray;
        this.generalImports = stringArray2;
        this.specificImports = stringArray3;
    }
}

