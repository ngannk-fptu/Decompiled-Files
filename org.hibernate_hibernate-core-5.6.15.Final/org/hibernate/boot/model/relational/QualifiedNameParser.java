/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.relational;

import java.util.Objects;
import org.hibernate.HibernateException;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.hibernate.boot.model.relational.QualifiedName;

public class QualifiedNameParser {
    public static final QualifiedNameParser INSTANCE = new QualifiedNameParser();

    public NameParts parse(String text, Identifier defaultCatalog, Identifier defaultSchema) {
        String name;
        String[] tokens;
        boolean wasQuotedInEntirety;
        if (text == null) {
            throw new IllegalIdentifierException("Object name to parse must be specified, but found null");
        }
        String catalogName = null;
        String schemaName = null;
        boolean catalogWasQuoted = false;
        boolean schemaWasQuoted = false;
        boolean bl = wasQuotedInEntirety = text.startsWith("`") && text.endsWith("`");
        if (wasQuotedInEntirety) {
            text = QualifiedNameParser.unquote(text);
        }
        if ((tokens = text.split("\\.")).length == 0 || tokens.length == 1) {
            name = text;
        } else if (tokens.length == 2) {
            schemaName = tokens[0];
            name = tokens[1];
        } else if (tokens.length == 3) {
            schemaName = tokens[0];
            catalogName = tokens[1];
            name = tokens[2];
        } else {
            throw new HibernateException("Unable to parse object name: " + text);
        }
        boolean nameWasQuoted = Identifier.isQuoted(name);
        if (nameWasQuoted) {
            name = QualifiedNameParser.unquote(name);
        }
        if (schemaName != null) {
            schemaWasQuoted = Identifier.isQuoted(schemaName);
            if (schemaWasQuoted) {
                schemaName = QualifiedNameParser.unquote(schemaName);
            }
        } else if (defaultSchema != null) {
            schemaName = defaultSchema.getText();
            schemaWasQuoted = defaultSchema.isQuoted();
        }
        if (catalogName != null) {
            catalogWasQuoted = Identifier.isQuoted(catalogName);
            if (catalogWasQuoted) {
                catalogName = QualifiedNameParser.unquote(catalogName);
            }
        } else if (defaultCatalog != null) {
            catalogName = defaultCatalog.getText();
            catalogWasQuoted = defaultCatalog.isQuoted();
        }
        return new NameParts(Identifier.toIdentifier(catalogName, wasQuotedInEntirety || catalogWasQuoted), Identifier.toIdentifier(schemaName, wasQuotedInEntirety || schemaWasQuoted), Identifier.toIdentifier(name, wasQuotedInEntirety || nameWasQuoted));
    }

    private static String unquote(String text) {
        return text.substring(1, text.length() - 1);
    }

    public NameParts parse(String text) {
        return this.parse(text, null, null);
    }

    public static class NameParts
    implements QualifiedName {
        private final Identifier catalogName;
        private final Identifier schemaName;
        private final Identifier objectName;
        private final String qualifiedText;

        public NameParts(Identifier catalogName, Identifier schemaName, Identifier objectName) {
            if (objectName == null) {
                throw new IllegalArgumentException("Name cannot be null");
            }
            this.catalogName = catalogName;
            this.schemaName = schemaName;
            this.objectName = objectName;
            StringBuilder buff = new StringBuilder();
            if (catalogName != null) {
                buff.append(catalogName.toString()).append('.');
            }
            if (schemaName != null) {
                buff.append(schemaName.toString()).append('.');
            }
            buff.append(objectName.toString());
            this.qualifiedText = buff.toString();
        }

        @Override
        public Identifier getCatalogName() {
            return this.catalogName;
        }

        @Override
        public Identifier getSchemaName() {
            return this.schemaName;
        }

        @Override
        public Identifier getObjectName() {
            return this.objectName;
        }

        @Override
        public String render() {
            return this.qualifiedText;
        }

        public String toString() {
            return this.qualifiedText;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            NameParts that = (NameParts)o;
            return Objects.equals(this.getCatalogName(), that.getCatalogName()) && Objects.equals(this.getSchemaName(), that.getSchemaName()) && Objects.equals(this.getObjectName(), that.getObjectName());
        }

        public int hashCode() {
            int result = this.getCatalogName() != null ? this.getCatalogName().hashCode() : 0;
            result = 31 * result + (this.getSchemaName() != null ? this.getSchemaName().hashCode() : 0);
            result = 31 * result + this.getObjectName().hashCode();
            return result;
        }
    }
}

