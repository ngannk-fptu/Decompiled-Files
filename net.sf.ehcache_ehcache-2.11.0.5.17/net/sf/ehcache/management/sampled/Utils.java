/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.management.sampled;

class Utils {
    Utils() {
    }

    static RuntimeException newPlainException(RuntimeException e) {
        String type = e.getClass().getName();
        if (type.startsWith("java.") || type.startsWith("javax.")) {
            return e;
        }
        RuntimeException result = new RuntimeException(e.getMessage());
        result.setStackTrace(e.getStackTrace());
        return result;
    }
}

