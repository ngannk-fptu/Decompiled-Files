/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.spi;

import java.util.Map;
import org.hibernate.Incubating;
import org.hibernate.service.Service;
import org.hibernate.tool.schema.internal.exec.GenerationTarget;
import org.hibernate.tool.schema.spi.ExtractionTool;
import org.hibernate.tool.schema.spi.SchemaCreator;
import org.hibernate.tool.schema.spi.SchemaDropper;
import org.hibernate.tool.schema.spi.SchemaMigrator;
import org.hibernate.tool.schema.spi.SchemaValidator;

@Incubating
public interface SchemaManagementTool
extends Service {
    public SchemaCreator getSchemaCreator(Map var1);

    public SchemaDropper getSchemaDropper(Map var1);

    public SchemaMigrator getSchemaMigrator(Map var1);

    public SchemaValidator getSchemaValidator(Map var1);

    public void setCustomDatabaseGenerationTarget(GenerationTarget var1);

    public ExtractionTool getExtractionTool();
}

