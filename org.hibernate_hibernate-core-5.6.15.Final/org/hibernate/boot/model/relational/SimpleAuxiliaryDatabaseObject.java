/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.relational;

import java.util.Set;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.AbstractAuxiliaryDatabaseObject;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.boot.model.relational.internal.SqlStringGenerationContextImpl;
import org.hibernate.dialect.Dialect;
import org.hibernate.internal.util.StringHelper;

public class SimpleAuxiliaryDatabaseObject
extends AbstractAuxiliaryDatabaseObject {
    private static final String CATALOG_NAME_PLACEHOLDER = "${catalog}";
    private static final String SCHEMA_NAME_PLACEHOLDER = "${schema}";
    private final String catalogName;
    private final String schemaName;
    private final String[] createStrings;
    private final String[] dropStrings;

    public SimpleAuxiliaryDatabaseObject(Namespace namespace, String createString, String dropString, Set<String> dialectScopes) {
        this(namespace, new String[]{createString}, new String[]{dropString}, dialectScopes);
    }

    public SimpleAuxiliaryDatabaseObject(Namespace namespace, String[] createStrings, String[] dropStrings, Set<String> dialectScopes) {
        this(dialectScopes, SimpleAuxiliaryDatabaseObject.extractName(namespace.getPhysicalName().getCatalog()), SimpleAuxiliaryDatabaseObject.extractName(namespace.getPhysicalName().getSchema()), createStrings, dropStrings);
    }

    private static String extractName(Identifier identifier) {
        return identifier == null ? null : identifier.getText();
    }

    public SimpleAuxiliaryDatabaseObject(Set<String> dialectScopes, String catalogName, String schemaName, String[] createStrings, String[] dropStrings) {
        super(dialectScopes);
        this.catalogName = catalogName;
        this.schemaName = schemaName;
        this.createStrings = createStrings;
        this.dropStrings = dropStrings;
    }

    @Override
    @Deprecated
    public String[] sqlCreateStrings(Dialect dialect) {
        return this.sqlCreateStrings(SqlStringGenerationContextImpl.forBackwardsCompatibility(dialect, null, null));
    }

    @Override
    public String[] sqlCreateStrings(SqlStringGenerationContext context) {
        String[] copy = new String[this.createStrings.length];
        int max = this.createStrings.length;
        for (int i = 0; i < max; ++i) {
            copy[i] = this.injectCatalogAndSchema(this.createStrings[i], context);
        }
        return copy;
    }

    @Override
    @Deprecated
    public String[] sqlDropStrings(Dialect dialect) {
        return this.sqlDropStrings(SqlStringGenerationContextImpl.forBackwardsCompatibility(dialect, null, null));
    }

    @Override
    public String[] sqlDropStrings(SqlStringGenerationContext context) {
        String[] copy = new String[this.dropStrings.length];
        int max = this.dropStrings.length;
        for (int i = 0; i < max; ++i) {
            copy[i] = this.injectCatalogAndSchema(this.dropStrings[i], context);
        }
        return copy;
    }

    protected String getCatalogName() {
        return this.catalogName;
    }

    protected String getSchemaName() {
        return this.schemaName;
    }

    private String injectCatalogAndSchema(String ddlString, SqlStringGenerationContext context) {
        Identifier defaultedCatalogName = context.catalogWithDefault(this.catalogName == null ? null : context.toIdentifier(this.catalogName));
        Identifier defaultedSchemaName = context.schemaWithDefault(this.schemaName == null ? null : context.toIdentifier(this.schemaName));
        String rtn = StringHelper.replace(ddlString, CATALOG_NAME_PLACEHOLDER, defaultedCatalogName == null ? "" : defaultedCatalogName.getText());
        rtn = StringHelper.replace(rtn, SCHEMA_NAME_PLACEHOLDER, defaultedSchemaName == null ? "" : defaultedSchemaName.getText());
        return rtn;
    }
}

