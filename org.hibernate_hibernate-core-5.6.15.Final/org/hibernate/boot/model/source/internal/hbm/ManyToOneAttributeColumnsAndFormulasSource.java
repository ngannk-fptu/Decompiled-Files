/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.List;
import java.util.Set;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmManyToOneType;
import org.hibernate.boot.model.source.internal.hbm.CommaSeparatedStringHelper;
import org.hibernate.boot.model.source.internal.hbm.RelationalValueSourceHelper;
import org.hibernate.boot.model.source.internal.hbm.XmlElementMetadata;

public class ManyToOneAttributeColumnsAndFormulasSource
extends RelationalValueSourceHelper.AbstractColumnsAndFormulasSource {
    private final JaxbHbmManyToOneType manyToOneMapping;

    public ManyToOneAttributeColumnsAndFormulasSource(JaxbHbmManyToOneType manyToOneMapping) {
        this.manyToOneMapping = manyToOneMapping;
    }

    @Override
    public XmlElementMetadata getSourceType() {
        return XmlElementMetadata.MANY_TO_ONE;
    }

    @Override
    public String getSourceName() {
        return this.manyToOneMapping.getName();
    }

    @Override
    public String getFormulaAttribute() {
        return this.manyToOneMapping.getFormulaAttribute();
    }

    @Override
    public String getColumnAttribute() {
        return this.manyToOneMapping.getColumnAttribute();
    }

    @Override
    public List getColumnOrFormulaElements() {
        return this.manyToOneMapping.getColumnOrFormula();
    }

    @Override
    public Boolean isNullable() {
        return this.manyToOneMapping.isNotNull() == null ? null : Boolean.valueOf(this.manyToOneMapping.isNotNull() == false);
    }

    @Override
    public Set<String> getIndexConstraintNames() {
        return CommaSeparatedStringHelper.split(this.manyToOneMapping.getIndex());
    }

    @Override
    public boolean isUnique() {
        return this.manyToOneMapping.isUnique();
    }

    @Override
    public Set<String> getUniqueKeyConstraintNames() {
        return CommaSeparatedStringHelper.split(this.manyToOneMapping.getUniqueKey());
    }
}

