/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.reflect.KFunction
 *  kotlin.reflect.KParameter
 *  kotlin.reflect.KParameter$Kind
 */
package org.springframework.data.mapping.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.Predicate;
import kotlin.reflect.KFunction;
import kotlin.reflect.KParameter;

public class KotlinDefaultMask {
    private final int[] defaulting;

    private KotlinDefaultMask(int[] defaulting) {
        this.defaulting = defaulting;
    }

    public void forEach(IntConsumer maskCallback) {
        for (int i : this.defaulting) {
            maskCallback.accept(i);
        }
    }

    public static int getMaskCount(int arguments) {
        return (arguments - 1) / 32 + 1;
    }

    public static KotlinDefaultMask from(KFunction<?> function, Predicate<KParameter> isPresent) {
        ArrayList<Integer> masks = new ArrayList<Integer>();
        int index = 0;
        int mask = 0;
        List parameters = function.getParameters();
        for (KParameter parameter : parameters) {
            if (index != 0 && index % 32 == 0) {
                masks.add(mask);
                mask = 0;
            }
            if (parameter.isOptional() && !isPresent.test(parameter)) {
                mask |= 1 << index % 32;
            }
            if (parameter.getKind() != KParameter.Kind.VALUE) continue;
            ++index;
        }
        masks.add(mask);
        return new KotlinDefaultMask(masks.stream().mapToInt(i -> i).toArray());
    }

    public int[] getDefaulting() {
        return this.defaulting;
    }
}

