/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.internal;

import java.util.HashMap;
import java.util.Map;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.QualifiedTableName;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;
import org.hibernate.tool.schema.extract.spi.ColumnInformation;
import org.hibernate.tool.schema.extract.spi.ForeignKeyInformation;
import org.hibernate.tool.schema.extract.spi.IndexInformation;
import org.hibernate.tool.schema.extract.spi.InformationExtractor;
import org.hibernate.tool.schema.extract.spi.PrimaryKeyInformation;
import org.hibernate.tool.schema.extract.spi.TableInformation;

public class TableInformationImpl
implements TableInformation {
    private final InformationExtractor extractor;
    private final IdentifierHelper identifierHelper;
    private final QualifiedTableName tableName;
    private final boolean physicalTable;
    private final String comment;
    private PrimaryKeyInformation primaryKey;
    private Map<Identifier, ForeignKeyInformation> foreignKeys;
    private Map<Identifier, IndexInformation> indexes;
    private Map<Identifier, ColumnInformation> columns = new HashMap<Identifier, ColumnInformation>();
    private boolean wasPrimaryKeyLoaded = false;

    public TableInformationImpl(InformationExtractor extractor, IdentifierHelper identifierHelper, QualifiedTableName tableName, boolean physicalTable, String comment) {
        this.extractor = extractor;
        this.identifierHelper = identifierHelper;
        this.tableName = tableName;
        this.physicalTable = physicalTable;
        this.comment = comment;
    }

    @Override
    public QualifiedTableName getName() {
        return this.tableName;
    }

    @Override
    public boolean isPhysicalTable() {
        return this.physicalTable;
    }

    @Override
    public String getComment() {
        return this.comment;
    }

    @Override
    public ColumnInformation getColumn(Identifier columnIdentifier) {
        return this.columns.get(new Identifier(this.identifierHelper.toMetaDataObjectName(columnIdentifier), false));
    }

    @Override
    public PrimaryKeyInformation getPrimaryKey() {
        if (!this.wasPrimaryKeyLoaded) {
            this.primaryKey = this.extractor.getPrimaryKey(this);
            this.wasPrimaryKeyLoaded = true;
        }
        return this.primaryKey;
    }

    @Override
    public Iterable<ForeignKeyInformation> getForeignKeys() {
        return this.foreignKeys().values();
    }

    protected Map<Identifier, ForeignKeyInformation> foreignKeys() {
        if (this.foreignKeys == null) {
            HashMap<Identifier, ForeignKeyInformation> fkMap = new HashMap<Identifier, ForeignKeyInformation>();
            Iterable<ForeignKeyInformation> fks = this.extractor.getForeignKeys(this);
            for (ForeignKeyInformation fk : fks) {
                fkMap.put(fk.getForeignKeyIdentifier(), fk);
            }
            this.foreignKeys = fkMap;
        }
        return this.foreignKeys;
    }

    @Override
    public ForeignKeyInformation getForeignKey(Identifier fkIdentifier) {
        return this.foreignKeys().get(new Identifier(this.identifierHelper.toMetaDataObjectName(fkIdentifier), false));
    }

    @Override
    public Iterable<IndexInformation> getIndexes() {
        return this.indexes().values();
    }

    protected Map<Identifier, IndexInformation> indexes() {
        if (this.indexes == null) {
            HashMap<Identifier, IndexInformation> indexMap = new HashMap<Identifier, IndexInformation>();
            Iterable<IndexInformation> indexes = this.extractor.getIndexes(this);
            for (IndexInformation index : indexes) {
                indexMap.put(index.getIndexIdentifier(), index);
            }
            this.indexes = indexMap;
        }
        return this.indexes;
    }

    @Override
    public void addColumn(ColumnInformation columnIdentifier) {
        this.columns.put(columnIdentifier.getColumnIdentifier(), columnIdentifier);
    }

    @Override
    public IndexInformation getIndex(Identifier indexName) {
        return this.indexes().get(new Identifier(this.identifierHelper.toMetaDataObjectName(indexName), false));
    }

    public String toString() {
        return "TableInformationImpl(" + this.tableName.toString() + ')';
    }
}

