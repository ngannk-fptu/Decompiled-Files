/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.marketplace.client.api;

import com.google.common.base.Preconditions;
import java.lang.reflect.Method;
import java.util.Optional;

public interface EnumWithKey {
    public String getKey();

    public static class Parser<A extends EnumWithKey> {
        private final A[] values;

        private Parser(A[] values) {
            this.values = (EnumWithKey[])Preconditions.checkNotNull(values);
        }

        public A[] getValues() {
            return this.values;
        }

        public Optional<A> safeValueForKey(String key) {
            for (A v : this.values) {
                if (!v.getKey().equalsIgnoreCase(key)) continue;
                return Optional.of(v);
            }
            return Optional.empty();
        }

        public static <A extends EnumWithKey> Parser<A> forType(Class<A> enumClass) {
            try {
                Method method = enumClass.getDeclaredMethod("values", new Class[0]);
                EnumWithKey[] values = (EnumWithKey[])method.invoke(null, new Object[0]);
                return new Parser(values);
            }
            catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }
}

