/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.List;
import java.util.Locale;
import org.hibernate.boot.MappingException;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFetchStyleEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmOnDeleteEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmSecondaryTableType;
import org.hibernate.boot.model.CustomSql;
import org.hibernate.boot.model.source.internal.hbm.AbstractHbmSourceNode;
import org.hibernate.boot.model.source.internal.hbm.Helper;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.RelationalValueSourceHelper;
import org.hibernate.boot.model.source.internal.hbm.XmlElementMetadata;
import org.hibernate.boot.model.source.spi.ColumnSource;
import org.hibernate.boot.model.source.spi.EntityNamingSource;
import org.hibernate.boot.model.source.spi.InLineViewSource;
import org.hibernate.boot.model.source.spi.SecondaryTableSource;
import org.hibernate.boot.model.source.spi.TableSource;
import org.hibernate.boot.model.source.spi.TableSpecificationSource;
import org.hibernate.engine.FetchStyle;
import org.hibernate.internal.util.StringHelper;

class SecondaryTableSourceImpl
extends AbstractHbmSourceNode
implements SecondaryTableSource {
    private final JaxbHbmSecondaryTableType jaxbSecondaryTableMapping;
    private final TableSpecificationSource joinTable;
    private final String logicalTableName;
    private final List<ColumnSource> keyColumnSources;

    public SecondaryTableSourceImpl(MappingDocument sourceMappingDocument, final JaxbHbmSecondaryTableType jaxbSecondaryTableMapping, EntityNamingSource entityNamingSource, Helper.InLineViewNameInferrer inLineViewNameInferrer) {
        super(sourceMappingDocument);
        this.jaxbSecondaryTableMapping = jaxbSecondaryTableMapping;
        this.joinTable = Helper.createTableSource(sourceMappingDocument, jaxbSecondaryTableMapping, inLineViewNameInferrer);
        if (this.joinTable instanceof TableSource && StringHelper.isEmpty(((TableSource)this.joinTable).getExplicitTableName())) {
            throw new MappingException(String.format(Locale.ENGLISH, "Secondary table (<join/>) must explicitly name table or sub-select, but neither specified for entity [%s]", entityNamingSource.getEntityName()), sourceMappingDocument.getOrigin());
        }
        this.logicalTableName = this.joinTable instanceof TableSource ? ((TableSource)this.joinTable).getExplicitTableName() : ((InLineViewSource)this.joinTable).getLogicalName();
        this.keyColumnSources = RelationalValueSourceHelper.buildColumnSources(sourceMappingDocument, this.logicalTableName, new RelationalValueSourceHelper.AbstractColumnsAndFormulasSource(){

            @Override
            public XmlElementMetadata getSourceType() {
                return XmlElementMetadata.KEY;
            }

            @Override
            public String getSourceName() {
                return null;
            }

            @Override
            public String getColumnAttribute() {
                return jaxbSecondaryTableMapping.getKey().getColumnAttribute();
            }

            @Override
            public List getColumnOrFormulaElements() {
                return jaxbSecondaryTableMapping.getKey().getColumn();
            }

            @Override
            public Boolean isNullable() {
                return false;
            }
        });
    }

    @Override
    public TableSpecificationSource getTableSource() {
        return this.joinTable;
    }

    @Override
    public List<ColumnSource> getPrimaryKeyColumnSources() {
        return this.keyColumnSources;
    }

    @Override
    public String getLogicalTableNameForContainedColumns() {
        return this.logicalTableName;
    }

    @Override
    public String getComment() {
        return this.jaxbSecondaryTableMapping.getComment();
    }

    @Override
    public FetchStyle getFetchStyle() {
        return this.jaxbSecondaryTableMapping.getFetch() == JaxbHbmFetchStyleEnum.JOIN ? FetchStyle.JOIN : FetchStyle.SELECT;
    }

    @Override
    public boolean isInverse() {
        return this.jaxbSecondaryTableMapping.isInverse();
    }

    @Override
    public boolean isOptional() {
        return this.jaxbSecondaryTableMapping.isOptional();
    }

    @Override
    public boolean isCascadeDeleteEnabled() {
        return this.jaxbSecondaryTableMapping.getKey().getOnDelete() == JaxbHbmOnDeleteEnum.CASCADE;
    }

    @Override
    public String getExplicitForeignKeyName() {
        return this.jaxbSecondaryTableMapping.getKey().getForeignKey();
    }

    @Override
    public boolean createForeignKeyConstraint() {
        return true;
    }

    @Override
    public CustomSql getCustomSqlInsert() {
        return Helper.buildCustomSql(this.jaxbSecondaryTableMapping.getSqlInsert());
    }

    @Override
    public CustomSql getCustomSqlUpdate() {
        return Helper.buildCustomSql(this.jaxbSecondaryTableMapping.getSqlUpdate());
    }

    @Override
    public CustomSql getCustomSqlDelete() {
        return Helper.buildCustomSql(this.jaxbSecondaryTableMapping.getSqlDelete());
    }
}

