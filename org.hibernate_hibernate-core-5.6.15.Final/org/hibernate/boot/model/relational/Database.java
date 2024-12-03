/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.relational;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
import org.hibernate.boot.model.relational.InitCommand;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.spi.MetadataBuildingOptions;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.service.ServiceRegistry;

public class Database {
    private final Dialect dialect;
    private final JdbcEnvironment jdbcEnvironment;
    private final Map<Namespace.Name, Namespace> namespaceMap = new TreeMap<Namespace.Name, Namespace>();
    private final Map<String, AuxiliaryDatabaseObject> auxiliaryDatabaseObjects = new HashMap<String, AuxiliaryDatabaseObject>();
    private final ServiceRegistry serviceRegistry;
    private final PhysicalNamingStrategy physicalNamingStrategy;
    private Namespace.Name physicalImplicitNamespaceName;
    private List<InitCommand> initCommands;

    public Database(MetadataBuildingOptions buildingOptions) {
        this(buildingOptions, buildingOptions.getServiceRegistry().getService(JdbcEnvironment.class));
    }

    public Database(MetadataBuildingOptions buildingOptions, JdbcEnvironment jdbcEnvironment) {
        this.serviceRegistry = buildingOptions.getServiceRegistry();
        this.jdbcEnvironment = jdbcEnvironment;
        this.physicalNamingStrategy = buildingOptions.getPhysicalNamingStrategy();
        this.dialect = Database.determineDialect(buildingOptions);
        this.setImplicitNamespaceName(this.toIdentifier(buildingOptions.getMappingDefaults().getImplicitCatalogName()), this.toIdentifier(buildingOptions.getMappingDefaults().getImplicitSchemaName()));
    }

    private void setImplicitNamespaceName(Identifier catalogName, Identifier schemaName) {
        this.physicalImplicitNamespaceName = new Namespace.Name(this.physicalNamingStrategy.toPhysicalCatalogName(catalogName, this.jdbcEnvironment), this.physicalNamingStrategy.toPhysicalSchemaName(schemaName, this.jdbcEnvironment));
    }

    private static Dialect determineDialect(MetadataBuildingOptions buildingOptions) {
        Dialect dialect = buildingOptions.getServiceRegistry().getService(JdbcServices.class).getDialect();
        if (dialect != null) {
            return dialect;
        }
        return new H2Dialect();
    }

    private Namespace makeNamespace(Namespace.Name name) {
        Namespace namespace = new Namespace(this.getPhysicalNamingStrategy(), this.getJdbcEnvironment(), name);
        this.namespaceMap.put(name, namespace);
        return namespace;
    }

    public Dialect getDialect() {
        return this.dialect;
    }

    public JdbcEnvironment getJdbcEnvironment() {
        return this.jdbcEnvironment;
    }

    public Identifier toIdentifier(String text) {
        return text == null ? null : this.jdbcEnvironment.getIdentifierHelper().toIdentifier(text);
    }

    public PhysicalNamingStrategy getPhysicalNamingStrategy() {
        return this.physicalNamingStrategy;
    }

    public Iterable<Namespace> getNamespaces() {
        return this.namespaceMap.values();
    }

    public Namespace getDefaultNamespace() {
        return this.locateNamespace(null, null);
    }

    public Namespace.Name getPhysicalImplicitNamespaceName() {
        return this.physicalImplicitNamespaceName;
    }

    public Namespace locateNamespace(Identifier catalogName, Identifier schemaName) {
        Namespace.Name name = new Namespace.Name(catalogName, schemaName);
        Namespace namespace = this.namespaceMap.get(name);
        if (namespace == null) {
            namespace = this.makeNamespace(name);
        }
        return namespace;
    }

    public Namespace adjustDefaultNamespace(Identifier catalogName, Identifier schemaName) {
        this.setImplicitNamespaceName(catalogName, schemaName);
        return this.locateNamespace(catalogName, schemaName);
    }

    public Namespace adjustDefaultNamespace(String implicitCatalogName, String implicitSchemaName) {
        return this.adjustDefaultNamespace(this.toIdentifier(implicitCatalogName), this.toIdentifier(implicitSchemaName));
    }

    public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject) {
        this.auxiliaryDatabaseObjects.put(auxiliaryDatabaseObject.getExportIdentifier(), auxiliaryDatabaseObject);
    }

    public Collection<AuxiliaryDatabaseObject> getAuxiliaryDatabaseObjects() {
        return this.auxiliaryDatabaseObjects == null ? Collections.emptyList() : this.auxiliaryDatabaseObjects.values();
    }

    public Collection<InitCommand> getInitCommands() {
        return this.initCommands == null ? Collections.emptyList() : this.initCommands;
    }

    public void addInitCommand(InitCommand initCommand) {
        if (this.initCommands == null) {
            this.initCommands = new ArrayList<InitCommand>();
        }
        this.initCommands.add(initCommand);
    }

    public ServiceRegistry getServiceRegistry() {
        return this.serviceRegistry;
    }
}

