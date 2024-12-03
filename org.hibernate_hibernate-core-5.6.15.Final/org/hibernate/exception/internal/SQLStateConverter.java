/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.exception.internal;

import org.hibernate.exception.internal.SQLStateConversionDelegate;
import org.hibernate.exception.internal.StandardSQLExceptionConverter;
import org.hibernate.exception.spi.ConversionContext;
import org.hibernate.exception.spi.SQLExceptionConverter;
import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;

@Deprecated
public class SQLStateConverter
extends StandardSQLExceptionConverter
implements SQLExceptionConverter {
    public SQLStateConverter(final ViolatedConstraintNameExtracter extracter) {
        ConversionContext conversionContext = new ConversionContext(){

            @Override
            public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
                return extracter;
            }
        };
        this.addDelegate(new SQLStateConversionDelegate(conversionContext));
    }
}

