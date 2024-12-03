/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types;

import java.io.Serializable;

public interface Operator
extends Serializable {
    public String name();

    public Class<?> getType();
}

