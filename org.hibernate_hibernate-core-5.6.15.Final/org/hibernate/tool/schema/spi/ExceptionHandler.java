/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.spi;

import org.hibernate.tool.schema.spi.CommandAcceptanceException;

public interface ExceptionHandler {
    public void handleException(CommandAcceptanceException var1);
}

