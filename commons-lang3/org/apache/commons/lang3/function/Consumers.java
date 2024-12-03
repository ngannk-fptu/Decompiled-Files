/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang3.function;

import java.util.function.Consumer;
import java.util.function.Function;

public class Consumers {
    private static final Consumer NOP = Function.identity()::apply;

    private Consumers() {
    }

    public static <T> Consumer<T> nop() {
        return NOP;
    }
}

