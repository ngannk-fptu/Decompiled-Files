/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

final class Modifiers {
    static final int ENUM = 16384;
    static final int SYNTHETIC = 4096;
    static final int ANNOTATION = 8192;

    Modifiers() {
    }

    static boolean isSynthetic(int mod) {
        return (mod & 0x1000) != 0;
    }
}

