/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.naming;

import org.hibernate.boot.model.naming.EntityNaming;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitBasicColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitJoinColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitJoinTableNameSource;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.internal.util.StringHelper;

public class ImplicitNamingStrategyLegacyHbmImpl
extends ImplicitNamingStrategyJpaCompliantImpl {
    public static final ImplicitNamingStrategyLegacyHbmImpl INSTANCE = new ImplicitNamingStrategyLegacyHbmImpl();

    @Override
    protected String transformEntityName(EntityNaming entityNaming) {
        return StringHelper.unqualify(entityNaming.getEntityName());
    }

    @Override
    public Identifier determineBasicColumnName(ImplicitBasicColumnNameSource source) {
        return source.isCollectionElement() ? this.toIdentifier("elt", source.getBuildingContext()) : super.determineBasicColumnName(source);
    }

    @Override
    public Identifier determineJoinColumnName(ImplicitJoinColumnNameSource source) {
        if (source.getAttributePath() != null) {
            return this.toIdentifier(this.transformAttributePath(source.getAttributePath()), source.getBuildingContext());
        }
        return super.determineJoinColumnName(source);
    }

    @Override
    public Identifier determineJoinTableName(ImplicitJoinTableNameSource source) {
        if (source.getAssociationOwningAttributePath() != null) {
            String name = source.getOwningPhysicalTableName() + '_' + this.transformAttributePath(source.getAssociationOwningAttributePath());
            return this.toIdentifier(name, source.getBuildingContext());
        }
        return super.determineJoinTableName(source);
    }
}

