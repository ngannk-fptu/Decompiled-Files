/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.exception.spi;

import org.hibernate.exception.spi.ConversionContext;
import org.hibernate.exception.spi.SQLExceptionConversionDelegate;

public abstract class AbstractSQLExceptionConversionDelegate
implements SQLExceptionConversionDelegate {
    private final ConversionContext conversionContext;

    protected AbstractSQLExceptionConversionDelegate(ConversionContext conversionContext) {
        this.conversionContext = conversionContext;
    }

    protected ConversionContext getConversionContext() {
        return this.conversionContext;
    }
}

