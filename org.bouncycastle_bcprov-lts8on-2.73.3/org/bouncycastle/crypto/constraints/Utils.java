/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.constraints;

import java.util.Set;

class Utils {
    Utils() {
    }

    static void addAliases(Set<String> exceptions) {
        if (exceptions.contains("RC4")) {
            exceptions.add("ARC4");
        } else if (exceptions.contains("ARC4")) {
            exceptions.add("RC4");
        }
    }
}

