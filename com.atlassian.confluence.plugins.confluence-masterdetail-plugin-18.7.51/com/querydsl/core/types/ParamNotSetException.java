/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types;

import com.querydsl.core.types.ParamExpression;

public class ParamNotSetException
extends RuntimeException {
    private static final long serialVersionUID = 2019016965590576490L;

    public ParamNotSetException(ParamExpression<?> param) {
        super(param.getNotSetMessage());
    }
}

