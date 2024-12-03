/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.naming;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.boot.model.naming.EntityNaming;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitAnyDiscriminatorColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitAnyKeyColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitBasicColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitCollectionTableNameSource;
import org.hibernate.boot.model.naming.ImplicitDiscriminatorColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitEntityNameSource;
import org.hibernate.boot.model.naming.ImplicitForeignKeyNameSource;
import org.hibernate.boot.model.naming.ImplicitIdentifierColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitIndexColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitIndexNameSource;
import org.hibernate.boot.model.naming.ImplicitJoinColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitJoinTableNameSource;
import org.hibernate.boot.model.naming.ImplicitMapKeyColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.ImplicitPrimaryKeyJoinColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitTenantIdColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitUniqueKeyNameSource;
import org.hibernate.boot.model.naming.NamingHelper;
import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.internal.util.StringHelper;

public class ImplicitNamingStrategyJpaCompliantImpl
implements ImplicitNamingStrategy,
Serializable {
    public static final ImplicitNamingStrategy INSTANCE = new ImplicitNamingStrategyJpaCompliantImpl();

    @Override
    public Identifier determinePrimaryTableName(ImplicitEntityNameSource source) {
        if (source == null) {
            throw new HibernateException("Entity naming information was not provided.");
        }
        String tableName = this.transformEntityName(source.getEntityNaming());
        if (tableName == null) {
            throw new HibernateException("Could not determine primary table name for entity");
        }
        return this.toIdentifier(tableName, source.getBuildingContext());
    }

    protected String transformEntityName(EntityNaming entityNaming) {
        if (StringHelper.isNotEmpty(entityNaming.getJpaEntityName())) {
            return entityNaming.getJpaEntityName();
        }
        return StringHelper.unqualify(entityNaming.getEntityName());
    }

    @Override
    public Identifier determineJoinTableName(ImplicitJoinTableNameSource source) {
        String name = source.getOwningPhysicalTableName() + '_' + source.getNonOwningPhysicalTableName();
        return this.toIdentifier(name, source.getBuildingContext());
    }

    @Override
    public Identifier determineCollectionTableName(ImplicitCollectionTableNameSource source) {
        String entityName = this.transformEntityName(source.getOwningEntityNaming());
        String name = entityName + '_' + this.transformAttributePath(source.getOwningAttributePath());
        return this.toIdentifier(name, source.getBuildingContext());
    }

    @Override
    public Identifier determineIdentifierColumnName(ImplicitIdentifierColumnNameSource source) {
        return this.toIdentifier(this.transformAttributePath(source.getIdentifierAttributePath()), source.getBuildingContext());
    }

    @Override
    public Identifier determineDiscriminatorColumnName(ImplicitDiscriminatorColumnNameSource source) {
        return this.toIdentifier(source.getBuildingContext().getMappingDefaults().getImplicitDiscriminatorColumnName(), source.getBuildingContext());
    }

    @Override
    public Identifier determineTenantIdColumnName(ImplicitTenantIdColumnNameSource source) {
        return this.toIdentifier(source.getBuildingContext().getMappingDefaults().getImplicitTenantIdColumnName(), source.getBuildingContext());
    }

    @Override
    public Identifier determineBasicColumnName(ImplicitBasicColumnNameSource source) {
        return this.toIdentifier(this.transformAttributePath(source.getAttributePath()), source.getBuildingContext());
    }

    @Override
    public Identifier determineJoinColumnName(ImplicitJoinColumnNameSource source) {
        String name = source.getNature() == ImplicitJoinColumnNameSource.Nature.ELEMENT_COLLECTION || source.getAttributePath() == null ? this.transformEntityName(source.getEntityNaming()) + '_' + source.getReferencedColumnName().getText() : this.transformAttributePath(source.getAttributePath()) + '_' + source.getReferencedColumnName().getText();
        return this.toIdentifier(name, source.getBuildingContext());
    }

    @Override
    public Identifier determinePrimaryKeyJoinColumnName(ImplicitPrimaryKeyJoinColumnNameSource source) {
        return source.getReferencedPrimaryKeyColumnName();
    }

    @Override
    public Identifier determineAnyDiscriminatorColumnName(ImplicitAnyDiscriminatorColumnNameSource source) {
        return this.toIdentifier(this.transformAttributePath(source.getAttributePath()) + "_" + source.getBuildingContext().getMappingDefaults().getImplicitDiscriminatorColumnName(), source.getBuildingContext());
    }

    @Override
    public Identifier determineAnyKeyColumnName(ImplicitAnyKeyColumnNameSource source) {
        return this.toIdentifier(this.transformAttributePath(source.getAttributePath()) + "_" + source.getBuildingContext().getMappingDefaults().getImplicitIdColumnName(), source.getBuildingContext());
    }

    @Override
    public Identifier determineMapKeyColumnName(ImplicitMapKeyColumnNameSource source) {
        return this.toIdentifier(this.transformAttributePath(source.getPluralAttributePath()) + "_KEY", source.getBuildingContext());
    }

    @Override
    public Identifier determineListIndexColumnName(ImplicitIndexColumnNameSource source) {
        return this.toIdentifier(this.transformAttributePath(source.getPluralAttributePath()) + "_ORDER", source.getBuildingContext());
    }

    @Override
    public Identifier determineForeignKeyName(ImplicitForeignKeyNameSource source) {
        Identifier userProvidedIdentifier = source.getUserProvidedIdentifier();
        return userProvidedIdentifier != null ? userProvidedIdentifier : this.toIdentifier(NamingHelper.withCharset(source.getBuildingContext().getBuildingOptions().getSchemaCharset()).generateHashedFkName("FK", source.getTableName(), source.getReferencedTableName(), source.getColumnNames()), source.getBuildingContext());
    }

    @Override
    public Identifier determineUniqueKeyName(ImplicitUniqueKeyNameSource source) {
        Identifier userProvidedIdentifier = source.getUserProvidedIdentifier();
        return userProvidedIdentifier != null ? userProvidedIdentifier : this.toIdentifier(NamingHelper.withCharset(source.getBuildingContext().getBuildingOptions().getSchemaCharset()).generateHashedConstraintName("UK", source.getTableName(), source.getColumnNames()), source.getBuildingContext());
    }

    @Override
    public Identifier determineIndexName(ImplicitIndexNameSource source) {
        Identifier userProvidedIdentifier = source.getUserProvidedIdentifier();
        return userProvidedIdentifier != null ? userProvidedIdentifier : this.toIdentifier(NamingHelper.withCharset(source.getBuildingContext().getBuildingOptions().getSchemaCharset()).generateHashedConstraintName("IDX", source.getTableName(), source.getColumnNames()), source.getBuildingContext());
    }

    protected String transformAttributePath(AttributePath attributePath) {
        return attributePath.getProperty();
    }

    protected Identifier toIdentifier(String stringForm, MetadataBuildingContext buildingContext) {
        return buildingContext.getMetadataCollector().getDatabase().getJdbcEnvironment().getIdentifierHelper().toIdentifier(stringForm);
    }
}

