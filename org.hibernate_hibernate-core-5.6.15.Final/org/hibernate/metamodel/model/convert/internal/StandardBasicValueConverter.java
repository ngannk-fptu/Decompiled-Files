/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.metamodel.model.convert.internal;

import org.hibernate.metamodel.model.convert.spi.BasicValueConverter;

public class StandardBasicValueConverter<O, R>
implements BasicValueConverter<O, R> {
    public static final StandardBasicValueConverter INSTANCE = new StandardBasicValueConverter();

    private StandardBasicValueConverter() {
    }

    @Override
    public O toDomainValue(R relationalForm) {
        return (O)relationalForm;
    }

    @Override
    public R toRelationalValue(O domainForm) {
        return (R)domainForm;
    }
}

