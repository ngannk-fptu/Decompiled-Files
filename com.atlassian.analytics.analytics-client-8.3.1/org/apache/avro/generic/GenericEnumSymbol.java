/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.generic;

import org.apache.avro.generic.GenericContainer;

public interface GenericEnumSymbol<E extends GenericEnumSymbol<E>>
extends GenericContainer,
Comparable<E> {
    public String toString();
}

