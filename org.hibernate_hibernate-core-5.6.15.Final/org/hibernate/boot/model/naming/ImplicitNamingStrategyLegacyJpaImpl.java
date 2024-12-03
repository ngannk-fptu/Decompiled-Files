/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.naming;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitCollectionTableNameSource;
import org.hibernate.boot.model.naming.ImplicitJoinColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitJoinTableNameSource;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;

public class ImplicitNamingStrategyLegacyJpaImpl
extends ImplicitNamingStrategyJpaCompliantImpl {
    public static final ImplicitNamingStrategyLegacyJpaImpl INSTANCE = new ImplicitNamingStrategyLegacyJpaImpl();

    @Override
    public Identifier determineCollectionTableName(ImplicitCollectionTableNameSource source) {
        Identifier identifier = this.toIdentifier(source.getOwningPhysicalTableName().getText() + "_" + this.transformAttributePath(source.getOwningAttributePath()), source.getBuildingContext());
        if (source.getOwningPhysicalTableName().isQuoted()) {
            identifier = Identifier.quote(identifier);
        }
        return identifier;
    }

    @Override
    public Identifier determineJoinTableName(ImplicitJoinTableNameSource source) {
        String ownerPortion = source.getOwningPhysicalTableName();
        String ownedPortion = source.getNonOwningPhysicalTableName() != null ? source.getNonOwningPhysicalTableName() : this.transformAttributePath(source.getAssociationOwningAttributePath());
        return this.toIdentifier(ownerPortion + "_" + ownedPortion, source.getBuildingContext());
    }

    @Override
    public Identifier determineJoinColumnName(ImplicitJoinColumnNameSource source) {
        String name = source.getNature() == ImplicitJoinColumnNameSource.Nature.ELEMENT_COLLECTION || source.getAttributePath() == null ? source.getReferencedTableName().getText() + '_' + source.getReferencedColumnName().getText() : this.transformAttributePath(source.getAttributePath()) + '_' + source.getReferencedColumnName().getText();
        return this.toIdentifier(name, source.getBuildingContext());
    }
}

