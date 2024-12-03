/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.relational;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import org.hibernate.HibernateException;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.boot.model.relational.Sequence;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.mapping.DenormalizedTable;
import org.hibernate.mapping.Table;

public class Namespace {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(Namespace.class);
    private final PhysicalNamingStrategy physicalNamingStrategy;
    private final JdbcEnvironment jdbcEnvironment;
    private final Name name;
    private final Name physicalName;
    private Map<Identifier, Table> tables = new TreeMap<Identifier, Table>();
    private Map<Identifier, Sequence> sequences = new TreeMap<Identifier, Sequence>();

    public Namespace(PhysicalNamingStrategy physicalNamingStrategy, JdbcEnvironment jdbcEnvironment, Name name) {
        this.physicalNamingStrategy = physicalNamingStrategy;
        this.jdbcEnvironment = jdbcEnvironment;
        this.name = name;
        this.physicalName = new Name(physicalNamingStrategy.toPhysicalCatalogName(name.getCatalog(), jdbcEnvironment), physicalNamingStrategy.toPhysicalSchemaName(name.getSchema(), jdbcEnvironment));
        log.debugf("Created database namespace [logicalName=%s, physicalName=%s]", name.toString(), this.physicalName.toString());
    }

    public Name getName() {
        return this.name;
    }

    public Name getPhysicalName() {
        return this.physicalName;
    }

    public Collection<Table> getTables() {
        return this.tables.values();
    }

    public Table locateTable(Identifier logicalTableName) {
        return this.tables.get(logicalTableName);
    }

    public Table createTable(Identifier logicalTableName, boolean isAbstract) {
        Table existing = this.tables.get(logicalTableName);
        if (existing != null) {
            return existing;
        }
        Identifier physicalTableName = this.physicalNamingStrategy.toPhysicalTableName(logicalTableName, this.jdbcEnvironment);
        Table table = new Table(this, physicalTableName, isAbstract);
        this.tables.put(logicalTableName, table);
        return table;
    }

    public DenormalizedTable createDenormalizedTable(Identifier logicalTableName, boolean isAbstract, Table includedTable) {
        Table existing = this.tables.get(logicalTableName);
        if (existing != null) {
            return (DenormalizedTable)existing;
        }
        Identifier physicalTableName = this.physicalNamingStrategy.toPhysicalTableName(logicalTableName, this.jdbcEnvironment);
        DenormalizedTable table = new DenormalizedTable(this, physicalTableName, isAbstract, includedTable);
        this.tables.put(logicalTableName, table);
        return table;
    }

    public Sequence locateSequence(Identifier name) {
        return this.sequences.get(name);
    }

    public Sequence createSequence(Identifier logicalName, int initialValue, int increment) {
        if (this.sequences.containsKey(logicalName)) {
            throw new HibernateException("Sequence was already registered with that name [" + logicalName.toString() + "]");
        }
        Identifier physicalName = this.physicalNamingStrategy.toPhysicalSequenceName(logicalName, this.jdbcEnvironment);
        Sequence sequence = new Sequence(this.physicalName.getCatalog(), this.physicalName.getSchema(), physicalName, initialValue, increment);
        this.sequences.put(logicalName, sequence);
        return sequence;
    }

    public String toString() {
        return "Schema{name=" + this.name + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Namespace that = (Namespace)o;
        return Objects.equals(this.name, that.name);
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public Iterable<Sequence> getSequences() {
        return this.sequences.values();
    }

    public static class ComparableHelper {
        public static <T extends Comparable<T>> int compare(T first, T second) {
            if (first == null) {
                if (second == null) {
                    return 0;
                }
                return 1;
            }
            if (second == null) {
                return -1;
            }
            return first.compareTo(second);
        }
    }

    public static class Name
    implements Comparable<Name> {
        private final Identifier catalog;
        private final Identifier schema;

        public Name(Identifier catalog, Identifier schema) {
            this.schema = schema;
            this.catalog = catalog;
        }

        public Identifier getCatalog() {
            return this.catalog;
        }

        public Identifier getSchema() {
            return this.schema;
        }

        public String toString() {
            return "Name{catalog=" + this.catalog + ", schema=" + this.schema + '}';
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Name that = (Name)o;
            return Objects.equals(this.catalog, that.catalog) && Objects.equals(this.schema, that.schema);
        }

        public int hashCode() {
            int result = this.catalog != null ? this.catalog.hashCode() : 0;
            result = 31 * result + (this.schema != null ? this.schema.hashCode() : 0);
            return result;
        }

        @Override
        public int compareTo(Name that) {
            int catalogCheck = ComparableHelper.compare(this.getCatalog(), that.getCatalog());
            if (catalogCheck != 0) {
                return catalogCheck;
            }
            return ComparableHelper.compare(this.getSchema(), that.getSchema());
        }
    }
}

