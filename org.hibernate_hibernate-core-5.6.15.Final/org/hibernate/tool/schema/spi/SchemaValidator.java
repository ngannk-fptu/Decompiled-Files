/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.spi;

import org.hibernate.Incubating;
import org.hibernate.boot.Metadata;
import org.hibernate.tool.schema.spi.ExecutionOptions;

@Incubating
public interface SchemaValidator {
    public void doValidation(Metadata var1, ExecutionOptions var2);
}

