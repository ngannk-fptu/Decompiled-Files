/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.reflection.java;

import org.hibernate.annotations.common.reflection.java.generics.TypeEnvironment;

@FunctionalInterface
interface XTypeConstruction<K, V> {
    public V createInstance(K var1, TypeEnvironment var2);
}

