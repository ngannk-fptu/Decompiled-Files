/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.core.types.Constant;
import com.querydsl.core.types.ConstantImpl;

public final class Null {
    public static final Null DEFAULT = new Null();
    public static final Constant<Null> CONSTANT = ConstantImpl.create(DEFAULT);

    private Null() {
    }
}

