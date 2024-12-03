/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.classfmt;

public class JavaBinaryNames {
    public static boolean isClinit(char[] selector) {
        return selector[0] == '<' && selector.length == 8;
    }

    public static boolean isConstructor(char[] selector) {
        return selector[0] == '<' && selector.length == 6;
    }
}

