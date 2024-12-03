/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.reflection.java;

import java.util.HashMap;
import java.util.Objects;
import org.hibernate.annotations.common.reflection.java.XTypeConstruction;
import org.hibernate.annotations.common.reflection.java.generics.TypeEnvironment;

final class TypeEnvironmentMap<K, V> {
    private HashMap<TypeEnvironment, ContextScope> rootMap;
    private final XTypeConstruction<K, V> constructionMethod;

    TypeEnvironmentMap(XTypeConstruction<K, V> constructionMethod) {
        Objects.requireNonNull(constructionMethod);
        this.constructionMethod = constructionMethod;
    }

    private <K, V> HashMap<TypeEnvironment, ContextScope> getOrInitRootMap() {
        if (this.rootMap == null) {
            this.rootMap = new HashMap(8, 0.5f);
        }
        return this.rootMap;
    }

    V getOrCompute(TypeEnvironment context, K subKey) {
        ContextScope contextualMap = this.getOrInitRootMap().computeIfAbsent(context, x$0 -> new ContextScope((TypeEnvironment)x$0));
        return (V)contextualMap.getOrCompute(subKey);
    }

    void clear() {
        HashMap<TypeEnvironment, ContextScope> m = this.rootMap;
        if (m != null) {
            this.rootMap = null;
            m.clear();
        }
    }

    private final class ContextScope
    extends HashMap<K, V> {
        private final TypeEnvironment context;

        private ContextScope(TypeEnvironment context) {
            super(64, 0.85f);
            this.context = context;
        }

        private V getOrCompute(K subKey) {
            return this.computeIfAbsent(subKey, this::buildObject);
        }

        private V buildObject(K subKey) {
            return TypeEnvironmentMap.this.constructionMethod.createInstance(subKey, this.context);
        }
    }
}

