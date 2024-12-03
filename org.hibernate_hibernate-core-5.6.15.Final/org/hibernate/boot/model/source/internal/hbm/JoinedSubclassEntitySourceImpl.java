/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmJoinedSubclassEntityType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmKeyType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmOnDeleteEnum;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.RelationalValueSourceHelper;
import org.hibernate.boot.model.source.internal.hbm.SubclassEntitySourceImpl;
import org.hibernate.boot.model.source.internal.hbm.XmlElementMetadata;
import org.hibernate.boot.model.source.spi.ColumnSource;
import org.hibernate.boot.model.source.spi.EntitySource;
import org.hibernate.boot.model.source.spi.JoinedSubclassEntitySource;
import org.hibernate.boot.model.source.spi.RelationalValueSource;

public class JoinedSubclassEntitySourceImpl
extends SubclassEntitySourceImpl
implements JoinedSubclassEntitySource {
    private final JaxbHbmKeyType jaxbKeyMapping;
    private final List<ColumnSource> primaryKeyJoinColumnSources;

    public JoinedSubclassEntitySourceImpl(MappingDocument sourceMappingDocument, JaxbHbmJoinedSubclassEntityType jaxbJoinedSubclassMapping, EntitySource container) {
        super(sourceMappingDocument, jaxbJoinedSubclassMapping, container);
        this.jaxbKeyMapping = jaxbJoinedSubclassMapping.getKey();
        List<RelationalValueSource> valueSources = RelationalValueSourceHelper.buildValueSources(this.sourceMappingDocument(), null, new RelationalValueSourceHelper.AbstractColumnsAndFormulasSource(){

            @Override
            public XmlElementMetadata getSourceType() {
                return null;
            }

            @Override
            public String getSourceName() {
                return null;
            }

            @Override
            public String getColumnAttribute() {
                return JoinedSubclassEntitySourceImpl.this.jaxbKeyMapping.getColumnAttribute();
            }

            @Override
            public List getColumnOrFormulaElements() {
                return JoinedSubclassEntitySourceImpl.this.jaxbKeyMapping.getColumn();
            }

            @Override
            public Boolean isNullable() {
                return false;
            }
        });
        this.primaryKeyJoinColumnSources = new ArrayList<ColumnSource>(valueSources.size());
        for (RelationalValueSource valueSource : valueSources) {
            this.primaryKeyJoinColumnSources.add((ColumnSource)ColumnSource.class.cast(valueSource));
        }
    }

    @Override
    public boolean isCascadeDeleteEnabled() {
        return this.jaxbKeyMapping.getOnDelete() == JaxbHbmOnDeleteEnum.CASCADE;
    }

    @Override
    public String getExplicitForeignKeyName() {
        return this.jaxbKeyMapping.getForeignKey();
    }

    @Override
    public boolean createForeignKeyConstraint() {
        return true;
    }

    @Override
    public List<ColumnSource> getPrimaryKeyColumnSources() {
        return this.primaryKeyJoinColumnSources;
    }

    @Override
    public String getDiscriminatorMatchValue() {
        return JaxbHbmJoinedSubclassEntityType.class.isInstance(this.jaxbEntityMapping()) ? ((JaxbHbmJoinedSubclassEntityType)this.jaxbEntityMapping()).getDiscriminatorValue() : null;
    }
}

