/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.naming;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.NamingStrategyHelper;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.internal.util.StringHelper;

public abstract class ObjectNameNormalizer {
    private Database database;

    public Identifier normalizeIdentifierQuoting(String identifierText) {
        return this.database().toIdentifier(identifierText);
    }

    protected Database database() {
        if (this.database == null) {
            this.database = this.getBuildingContext().getMetadataCollector().getDatabase();
        }
        return this.database;
    }

    public Identifier normalizeIdentifierQuoting(Identifier identifier) {
        return this.getBuildingContext().getMetadataCollector().getDatabase().getJdbcEnvironment().getIdentifierHelper().normalizeQuoting(identifier);
    }

    public String normalizeIdentifierQuotingAsString(String identifierText) {
        Identifier identifier = this.normalizeIdentifierQuoting(identifierText);
        if (identifier == null) {
            return null;
        }
        return identifier.render(this.database().getDialect());
    }

    public String toDatabaseIdentifierText(String identifierText) {
        return this.database().getDialect().quote(this.normalizeIdentifierQuotingAsString(identifierText));
    }

    public Identifier determineLogicalName(String explicitName, NamingStrategyHelper namingStrategyHelper) {
        Identifier logicalName = StringHelper.isEmpty(explicitName) ? namingStrategyHelper.determineImplicitName(this.getBuildingContext()) : namingStrategyHelper.handleExplicitName(explicitName, this.getBuildingContext());
        logicalName = this.getBuildingContext().getMetadataCollector().getDatabase().getJdbcEnvironment().getIdentifierHelper().normalizeQuoting(logicalName);
        return logicalName;
    }

    public String applyGlobalQuoting(String text) {
        return this.database().getJdbcEnvironment().getIdentifierHelper().applyGlobalQuoting(text).render(this.database().getDialect());
    }

    protected abstract MetadataBuildingContext getBuildingContext();
}

