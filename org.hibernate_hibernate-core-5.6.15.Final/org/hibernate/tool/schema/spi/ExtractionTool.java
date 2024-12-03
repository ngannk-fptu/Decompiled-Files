/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.spi;

import org.hibernate.Incubating;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.resource.transaction.spi.DdlTransactionIsolator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.schema.extract.spi.ExtractionContext;
import org.hibernate.tool.schema.extract.spi.InformationExtractor;

@Incubating
public interface ExtractionTool {
    public ExtractionContext createExtractionContext(ServiceRegistry var1, JdbcEnvironment var2, SqlStringGenerationContext var3, DdlTransactionIsolator var4, ExtractionContext.DatabaseObjectAccess var5);

    public InformationExtractor createInformationExtractor(ExtractionContext var1);
}

