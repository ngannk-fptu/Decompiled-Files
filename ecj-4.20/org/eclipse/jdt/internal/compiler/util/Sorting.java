/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.util;

import java.util.Arrays;
import java.util.Comparator;
import org.eclipse.jdt.internal.compiler.lookup.InferenceVariable;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class Sorting {
    public static ReferenceBinding[] sortTypes(ReferenceBinding[] types) {
        int len = types.length;
        ReferenceBinding[] unsorted = new ReferenceBinding[len];
        ReferenceBinding[] sorted = new ReferenceBinding[len];
        System.arraycopy(types, 0, unsorted, 0, len);
        int o = 0;
        int i = 0;
        while (i < len) {
            o = Sorting.sort(unsorted, i, sorted, o);
            ++i;
        }
        return sorted;
    }

    private static int sort(ReferenceBinding[] input, int i, ReferenceBinding[] output, int o) {
        if (input[i] == null) {
            return o;
        }
        ReferenceBinding superclass = input[i].superclass();
        o = Sorting.sortSuper(superclass, input, output, o);
        ReferenceBinding[] superInterfaces = input[i].superInterfaces();
        int j = 0;
        while (j < superInterfaces.length) {
            o = Sorting.sortSuper(superInterfaces[j], input, output, o);
            ++j;
        }
        output[o++] = input[i];
        input[i] = null;
        return o;
    }

    private static int sortSuper(ReferenceBinding superclass, ReferenceBinding[] input, ReferenceBinding[] output, int o) {
        if (superclass.id != 1) {
            int j = 0;
            j = 0;
            while (j < input.length) {
                if (TypeBinding.equalsEquals(input[j], superclass)) break;
                ++j;
            }
            if (j < input.length) {
                o = Sorting.sort(input, j, output, o);
            }
        }
        return o;
    }

    public static MethodBinding[] concreteFirst(MethodBinding[] methods, int length) {
        if (length == 0 || length > 0 && !methods[0].isAbstract()) {
            return methods;
        }
        MethodBinding[] copy = new MethodBinding[length];
        int idx = 0;
        int i = 0;
        while (i < length) {
            if (!methods[i].isAbstract()) {
                copy[idx++] = methods[i];
            }
            ++i;
        }
        i = 0;
        while (i < length) {
            if (methods[i].isAbstract()) {
                copy[idx++] = methods[i];
            }
            ++i;
        }
        return copy;
    }

    public static MethodBinding[] abstractFirst(MethodBinding[] methods, int length) {
        if (length == 0 || length > 0 && methods[0].isAbstract()) {
            return methods;
        }
        MethodBinding[] copy = new MethodBinding[length];
        int idx = 0;
        int i = 0;
        while (i < length) {
            if (methods[i].isAbstract()) {
                copy[idx++] = methods[i];
            }
            ++i;
        }
        i = 0;
        while (i < length) {
            if (!methods[i].isAbstract()) {
                copy[idx++] = methods[i];
            }
            ++i;
        }
        return copy;
    }

    public static void sortInferenceVariables(InferenceVariable[] variables) {
        Arrays.sort(variables, new Comparator<InferenceVariable>(){

            @Override
            public int compare(InferenceVariable iv1, InferenceVariable iv2) {
                return iv1.rank - iv2.rank;
            }
        });
    }
}

