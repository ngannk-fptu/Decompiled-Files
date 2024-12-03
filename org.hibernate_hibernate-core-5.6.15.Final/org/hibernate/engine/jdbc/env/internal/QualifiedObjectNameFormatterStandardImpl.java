/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.env.internal;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.QualifiedName;
import org.hibernate.boot.model.relational.QualifiedSequenceName;
import org.hibernate.boot.model.relational.QualifiedTableName;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
import org.hibernate.engine.jdbc.env.spi.QualifiedObjectNameFormatter;

public class QualifiedObjectNameFormatterStandardImpl
implements QualifiedObjectNameFormatter {
    private final Format format;

    public QualifiedObjectNameFormatterStandardImpl(NameQualifierSupport nameQualifierSupport, String catalogSeparator, boolean catalogAtEnd) {
        this.format = this.buildFormat(nameQualifierSupport, catalogSeparator, catalogAtEnd);
    }

    private Format buildFormat(NameQualifierSupport nameQualifierSupport, String catalogSeparator, boolean catalogAtEnd) {
        switch (nameQualifierSupport) {
            case NONE: {
                return NoQualifierSupportFormat.INSTANCE;
            }
            case CATALOG: {
                return catalogAtEnd ? new NameCatalogFormat(catalogSeparator) : new CatalogNameFormat(catalogSeparator);
            }
            case SCHEMA: {
                return SchemaNameFormat.INSTANCE;
            }
        }
        return catalogAtEnd ? new SchemaNameCatalogFormat(catalogSeparator) : new CatalogSchemaNameFormat(catalogSeparator);
    }

    public QualifiedObjectNameFormatterStandardImpl(NameQualifierSupport nameQualifierSupport) {
        this(nameQualifierSupport, ".", false);
    }

    public QualifiedObjectNameFormatterStandardImpl(NameQualifierSupport nameQualifierSupport, DatabaseMetaData databaseMetaData) throws SQLException {
        this(nameQualifierSupport, databaseMetaData.getCatalogSeparator(), !databaseMetaData.isCatalogAtStart());
    }

    @Override
    public String format(QualifiedTableName qualifiedTableName, Dialect dialect) {
        return this.format.format(qualifiedTableName.getCatalogName(), qualifiedTableName.getSchemaName(), qualifiedTableName.getTableName(), dialect);
    }

    private static String render(Identifier identifier, Dialect dialect) {
        if (identifier == null) {
            return null;
        }
        return identifier.render(dialect);
    }

    @Override
    public String format(QualifiedSequenceName qualifiedSequenceName, Dialect dialect) {
        return this.format.format(qualifiedSequenceName.getCatalogName(), qualifiedSequenceName.getSchemaName(), qualifiedSequenceName.getSequenceName(), dialect);
    }

    @Override
    public String format(QualifiedName qualifiedName, Dialect dialect) {
        return this.format.format(qualifiedName.getCatalogName(), qualifiedName.getSchemaName(), qualifiedName.getObjectName(), dialect);
    }

    private static class SchemaNameFormat
    implements Format {
        public static final SchemaNameFormat INSTANCE = new SchemaNameFormat();

        private SchemaNameFormat() {
        }

        @Override
        public String format(Identifier catalog, Identifier schema, Identifier name, Dialect dialect) {
            StringBuilder buff = new StringBuilder();
            if (schema != null) {
                buff.append(QualifiedObjectNameFormatterStandardImpl.render(schema, dialect)).append('.');
            }
            buff.append(QualifiedObjectNameFormatterStandardImpl.render(name, dialect));
            return buff.toString();
        }
    }

    private static class CatalogNameFormat
    implements Format {
        private final String catalogSeparator;

        public CatalogNameFormat(String catalogSeparator) {
            this.catalogSeparator = catalogSeparator;
        }

        @Override
        public String format(Identifier catalog, Identifier schema, Identifier name, Dialect dialect) {
            StringBuilder buff = new StringBuilder();
            if (catalog != null) {
                buff.append(QualifiedObjectNameFormatterStandardImpl.render(catalog, dialect)).append(this.catalogSeparator);
            }
            buff.append(QualifiedObjectNameFormatterStandardImpl.render(name, dialect));
            return buff.toString();
        }
    }

    private static class NameCatalogFormat
    implements Format {
        private final String catalogSeparator;

        public NameCatalogFormat(String catalogSeparator) {
            this.catalogSeparator = catalogSeparator;
        }

        @Override
        public String format(Identifier catalog, Identifier schema, Identifier name, Dialect dialect) {
            StringBuilder buff = new StringBuilder();
            buff.append(QualifiedObjectNameFormatterStandardImpl.render(name, dialect));
            if (catalog != null) {
                buff.append(this.catalogSeparator).append(QualifiedObjectNameFormatterStandardImpl.render(catalog, dialect));
            }
            return buff.toString();
        }
    }

    private static class CatalogSchemaNameFormat
    implements Format {
        private final String catalogSeparator;

        public CatalogSchemaNameFormat(String catalogSeparator) {
            this.catalogSeparator = catalogSeparator;
        }

        @Override
        public String format(Identifier catalog, Identifier schema, Identifier name, Dialect dialect) {
            StringBuilder buff = new StringBuilder();
            if (catalog != null) {
                buff.append(QualifiedObjectNameFormatterStandardImpl.render(catalog, dialect)).append(this.catalogSeparator);
            }
            if (schema != null) {
                buff.append(QualifiedObjectNameFormatterStandardImpl.render(schema, dialect)).append('.');
            }
            buff.append(QualifiedObjectNameFormatterStandardImpl.render(name, dialect));
            return buff.toString();
        }
    }

    private static class SchemaNameCatalogFormat
    implements Format {
        private final String catalogSeparator;

        public SchemaNameCatalogFormat(String catalogSeparator) {
            this.catalogSeparator = catalogSeparator;
        }

        @Override
        public String format(Identifier catalog, Identifier schema, Identifier name, Dialect dialect) {
            StringBuilder buff = new StringBuilder();
            if (schema != null) {
                buff.append(QualifiedObjectNameFormatterStandardImpl.render(schema, dialect)).append('.');
            }
            buff.append(QualifiedObjectNameFormatterStandardImpl.render(name, dialect));
            if (catalog != null) {
                buff.append(this.catalogSeparator).append(QualifiedObjectNameFormatterStandardImpl.render(catalog, dialect));
            }
            return buff.toString();
        }
    }

    private static class NoQualifierSupportFormat
    implements Format {
        public static final NoQualifierSupportFormat INSTANCE = new NoQualifierSupportFormat();

        private NoQualifierSupportFormat() {
        }

        @Override
        public String format(Identifier catalog, Identifier schema, Identifier name, Dialect dialect) {
            return QualifiedObjectNameFormatterStandardImpl.render(name, dialect);
        }
    }

    private static interface Format {
        public String format(Identifier var1, Identifier var2, Identifier var3, Dialect var4);
    }
}

