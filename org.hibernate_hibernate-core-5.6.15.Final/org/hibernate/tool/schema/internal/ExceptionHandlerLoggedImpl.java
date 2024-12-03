/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.tool.schema.internal;

import org.hibernate.tool.schema.spi.CommandAcceptanceException;
import org.hibernate.tool.schema.spi.ExceptionHandler;
import org.jboss.logging.Logger;

public class ExceptionHandlerLoggedImpl
implements ExceptionHandler {
    private static final Logger log = Logger.getLogger(ExceptionHandlerLoggedImpl.class);
    public static final ExceptionHandlerLoggedImpl INSTANCE = new ExceptionHandlerLoggedImpl();

    @Override
    public void handleException(CommandAcceptanceException exception) {
        log.warnf((Throwable)((Object)exception), "GenerationTarget encountered exception accepting command : %s", (Object)exception.getMessage());
    }
}

