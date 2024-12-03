/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.internal;

import java.util.Locale;
import org.hibernate.tool.schema.spi.CommandAcceptanceException;
import org.hibernate.tool.schema.spi.ExceptionHandler;
import org.hibernate.tool.schema.spi.SchemaManagementException;

public class ExceptionHandlerHaltImpl
implements ExceptionHandler {
    public static final ExceptionHandlerHaltImpl INSTANCE = new ExceptionHandlerHaltImpl();

    @Override
    public void handleException(CommandAcceptanceException exception) {
        throw new SchemaManagementException(String.format(Locale.ROOT, "Halting on error : %s", exception.getMessage()), (Throwable)((Object)exception));
    }
}

