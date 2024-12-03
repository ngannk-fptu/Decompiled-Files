/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.internal;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.tool.schema.spi.CommandAcceptanceException;
import org.hibernate.tool.schema.spi.ExceptionHandler;

public class ExceptionHandlerCollectingImpl
implements ExceptionHandler {
    private final List<CommandAcceptanceException> exceptions = new ArrayList<CommandAcceptanceException>();

    @Override
    public void handleException(CommandAcceptanceException exception) {
        this.exceptions.add(exception);
    }

    public List<CommandAcceptanceException> getExceptions() {
        return this.exceptions;
    }
}

