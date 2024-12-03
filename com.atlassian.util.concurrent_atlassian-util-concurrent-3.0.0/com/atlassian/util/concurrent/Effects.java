/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.util.concurrent;

import com.atlassian.util.concurrent.Effect;

public class Effects {
    private static Effect<Object> NOOP = new Effect<Object>(){

        @Override
        public void apply(Object a) {
        }
    };

    private Effects() {
    }

    public static <E> Effect<E> noop() {
        Effect<Object> result = NOOP;
        return result;
    }
}

