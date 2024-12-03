/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.relational;

import org.hibernate.HibernateException;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.Exportable;
import org.hibernate.boot.model.relational.QualifiedNameParser;
import org.hibernate.boot.model.relational.QualifiedSequenceName;

public class Sequence
implements Exportable {
    private final QualifiedSequenceName name;
    private final String exportIdentifier;
    private int initialValue = 1;
    private int incrementSize = 1;

    public Sequence(Identifier catalogName, Identifier schemaName, Identifier sequenceName) {
        this.name = new QualifiedSequenceName(catalogName, schemaName, sequenceName);
        this.exportIdentifier = this.name.render();
    }

    public Sequence(Identifier catalogName, Identifier schemaName, Identifier sequenceName, int initialValue, int incrementSize) {
        this(catalogName, schemaName, sequenceName);
        this.initialValue = initialValue;
        this.incrementSize = incrementSize;
    }

    public QualifiedSequenceName getName() {
        return this.name;
    }

    @Override
    public String getExportIdentifier() {
        return this.exportIdentifier;
    }

    public int getInitialValue() {
        return this.initialValue;
    }

    public int getIncrementSize() {
        return this.incrementSize;
    }

    public void validate(int initialValue, int incrementSize) {
        if (this.initialValue != initialValue) {
            throw new HibernateException(String.format("Multiple references to database sequence [%s] were encountered attempting to set conflicting values for 'initial value'.  Found [%s] and [%s]", this.exportIdentifier, this.initialValue, initialValue));
        }
        if (this.incrementSize != incrementSize) {
            throw new HibernateException(String.format("Multiple references to database sequence [%s] were encountered attempting to set conflicting values for 'increment size'.  Found [%s] and [%s]", this.exportIdentifier, this.incrementSize, incrementSize));
        }
    }

    public static class Name
    extends QualifiedNameParser.NameParts {
        public Name(Identifier catalogIdentifier, Identifier schemaIdentifier, Identifier nameIdentifier) {
            super(catalogIdentifier, schemaIdentifier, nameIdentifier);
        }
    }
}

