/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.codegen.bean;

import com.mchange.v2.codegen.bean.ClassInfo;

public interface ResolvedClassInfo
extends ClassInfo {
    public Class[] getInterfaces();

    public Class[] getSuperclass();
}

