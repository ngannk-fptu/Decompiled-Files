/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.List;
import java.util.Set;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmBasicAttributeType;
import org.hibernate.boot.model.source.internal.hbm.CommaSeparatedStringHelper;
import org.hibernate.boot.model.source.internal.hbm.Helper;
import org.hibernate.boot.model.source.internal.hbm.RelationalValueSourceHelper;
import org.hibernate.boot.model.source.internal.hbm.XmlElementMetadata;
import org.hibernate.boot.model.source.spi.SizeSource;

public class BasicAttributeColumnsAndFormulasSource
extends RelationalValueSourceHelper.AbstractColumnsAndFormulasSource
implements RelationalValueSourceHelper.ColumnsAndFormulasSource {
    private final JaxbHbmBasicAttributeType basicAttributeMapping;

    public BasicAttributeColumnsAndFormulasSource(JaxbHbmBasicAttributeType basicAttributeMapping) {
        this.basicAttributeMapping = basicAttributeMapping;
    }

    @Override
    public XmlElementMetadata getSourceType() {
        return XmlElementMetadata.PROPERTY;
    }

    @Override
    public String getSourceName() {
        return this.basicAttributeMapping.getName();
    }

    @Override
    public String getFormulaAttribute() {
        return this.basicAttributeMapping.getFormulaAttribute();
    }

    @Override
    public String getColumnAttribute() {
        return this.basicAttributeMapping.getColumnAttribute();
    }

    @Override
    public List getColumnOrFormulaElements() {
        return this.basicAttributeMapping.getColumnOrFormula();
    }

    @Override
    public SizeSource getSizeSource() {
        return Helper.interpretSizeSource(this.basicAttributeMapping.getLength(), this.basicAttributeMapping.getScale(), this.basicAttributeMapping.getPrecision());
    }

    @Override
    public Boolean isNullable() {
        return this.basicAttributeMapping.isNotNull() == null ? null : Boolean.valueOf(this.basicAttributeMapping.isNotNull() == false);
    }

    @Override
    public Set<String> getIndexConstraintNames() {
        return CommaSeparatedStringHelper.split(this.basicAttributeMapping.getIndex());
    }

    @Override
    public boolean isUnique() {
        return this.basicAttributeMapping.isUnique();
    }

    @Override
    public Set<String> getUniqueKeyConstraintNames() {
        return CommaSeparatedStringHelper.split(this.basicAttributeMapping.getUniqueKey());
    }
}

