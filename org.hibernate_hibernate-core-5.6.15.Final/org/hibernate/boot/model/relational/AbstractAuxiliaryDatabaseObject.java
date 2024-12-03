/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.relational;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
import org.hibernate.dialect.Dialect;

public abstract class AbstractAuxiliaryDatabaseObject
implements AuxiliaryDatabaseObject,
AuxiliaryDatabaseObject.Expandable {
    private static final String EXPORT_IDENTIFIER_PREFIX = "auxiliary-object-";
    private static final AtomicInteger counter = new AtomicInteger(0);
    private final String exportIdentifier;
    private final boolean beforeTables;
    private final Set<String> dialectScopes;

    protected AbstractAuxiliaryDatabaseObject() {
        this(null);
    }

    public AbstractAuxiliaryDatabaseObject(boolean beforeTables) {
        this(beforeTables, null);
    }

    protected AbstractAuxiliaryDatabaseObject(Set<String> dialectScopes) {
        this(false, dialectScopes);
    }

    protected AbstractAuxiliaryDatabaseObject(boolean beforeTables, Set<String> dialectScopes) {
        this.beforeTables = beforeTables;
        this.dialectScopes = dialectScopes == null ? new HashSet() : dialectScopes;
        this.exportIdentifier = "auxiliary-object-." + counter.getAndIncrement();
    }

    @Override
    public String getExportIdentifier() {
        return this.exportIdentifier;
    }

    @Override
    public void addDialectScope(String dialectName) {
        this.dialectScopes.add(dialectName);
    }

    public Set getDialectScopes() {
        return this.dialectScopes;
    }

    @Override
    public boolean appliesToDialect(Dialect dialect) {
        return this.getDialectScopes().isEmpty() || this.getDialectScopes().contains(dialect.getClass().getName());
    }

    @Override
    public boolean beforeTablesOnCreation() {
        return this.beforeTables;
    }
}

