/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.spi;

import java.util.Map;
import org.hibernate.Incubating;
import org.hibernate.tool.schema.spi.ExceptionHandler;

@Incubating
public interface ExecutionOptions {
    public Map getConfigurationValues();

    public boolean shouldManageNamespaces();

    public ExceptionHandler getExceptionHandler();
}

