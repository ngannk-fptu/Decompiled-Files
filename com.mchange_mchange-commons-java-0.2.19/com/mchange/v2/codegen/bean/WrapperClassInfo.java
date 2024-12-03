/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.codegen.bean;

import com.mchange.v2.codegen.bean.ClassInfo;

public abstract class WrapperClassInfo
implements ClassInfo {
    ClassInfo inner;

    public WrapperClassInfo(ClassInfo classInfo) {
        this.inner = classInfo;
    }

    @Override
    public String getPackageName() {
        return this.inner.getPackageName();
    }

    @Override
    public int getModifiers() {
        return this.inner.getModifiers();
    }

    @Override
    public String getClassName() {
        return this.inner.getClassName();
    }

    @Override
    public String getSuperclassName() {
        return this.inner.getSuperclassName();
    }

    @Override
    public String[] getInterfaceNames() {
        return this.inner.getInterfaceNames();
    }

    @Override
    public String[] getGeneralImports() {
        return this.inner.getGeneralImports();
    }

    @Override
    public String[] getSpecificImports() {
        return this.inner.getSpecificImports();
    }
}

