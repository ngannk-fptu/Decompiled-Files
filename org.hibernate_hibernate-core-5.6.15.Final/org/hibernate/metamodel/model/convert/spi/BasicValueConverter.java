/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.metamodel.model.convert.spi;

public interface BasicValueConverter<O, R> {
    public O toDomainValue(R var1);

    public R toRelationalValue(O var1);
}

