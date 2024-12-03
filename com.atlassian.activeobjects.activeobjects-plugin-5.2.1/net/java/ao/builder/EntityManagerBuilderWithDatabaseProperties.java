/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.builder;

import net.java.ao.EntityManager;
import net.java.ao.builder.AbstractEntityManagerBuilderWithDatabaseProperties;
import net.java.ao.builder.BuilderDatabaseProperties;
import net.java.ao.builder.DatabaseProviderFactory;

public final class EntityManagerBuilderWithDatabaseProperties
extends AbstractEntityManagerBuilderWithDatabaseProperties<EntityManagerBuilderWithDatabaseProperties> {
    EntityManagerBuilderWithDatabaseProperties(BuilderDatabaseProperties databaseProperties) {
        super(databaseProperties);
    }

    @Override
    public EntityManager build() {
        return new EntityManager(DatabaseProviderFactory.getDatabaseProvider(this.getDatabaseProperties()), this.getEntityManagerConfiguration());
    }
}

