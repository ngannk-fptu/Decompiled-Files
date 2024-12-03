/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.Key;

public interface RehashableKeys {
    public void rehashKeys();

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Keys {
        public static boolean needsRehashing(Key<?> key) {
            if (!key.hasAttributes()) {
                return false;
            }
            int newHashCode = key.getTypeLiteral().hashCode() * 31 + ((Object)key.getAnnotation()).hashCode();
            return key.hashCode() != newHashCode;
        }

        public static <T> Key<T> rehash(Key<T> key) {
            if (key.hasAttributes()) {
                return Key.get(key.getTypeLiteral(), key.getAnnotation());
            }
            return key;
        }

        private Keys() {
        }
    }
}

