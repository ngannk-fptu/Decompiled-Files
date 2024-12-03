/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.hibernate.boot.MappingException;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmColumnType;
import org.hibernate.boot.model.TruthValue;
import org.hibernate.boot.model.source.internal.hbm.ColumnAttributeSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.ColumnSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.FormulaImpl;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.XmlElementMetadata;
import org.hibernate.boot.model.source.spi.ColumnSource;
import org.hibernate.boot.model.source.spi.DerivedValueSource;
import org.hibernate.boot.model.source.spi.RelationalValueSource;
import org.hibernate.boot.model.source.spi.SizeSource;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.CollectionHelper;

public class RelationalValueSourceHelper {
    public static RelationalValueSource buildValueSource(MappingDocument mappingDocument, String containingTableName, ColumnsAndFormulasSource columnsAndFormulasSource) {
        List<RelationalValueSource> sources = RelationalValueSourceHelper.buildValueSources(mappingDocument, containingTableName, columnsAndFormulasSource);
        if (sources.size() > 1) {
            String errorMessage = columnsAndFormulasSource.getSourceType().canBeNamed() && StringHelper.isNotEmpty(columnsAndFormulasSource.getSourceName()) ? String.format(Locale.ENGLISH, "Expecting just a single formula/column in context of <%s name=\"%s\"/>", columnsAndFormulasSource.getSourceType().getElementName(), columnsAndFormulasSource.getSourceName()) : String.format(Locale.ENGLISH, "Expecting just a single formula/column in context of <%s/>", columnsAndFormulasSource.getSourceType().getElementName());
            throw new MappingException(errorMessage, mappingDocument.getOrigin());
        }
        return sources.get(0);
    }

    public static ColumnSource buildColumnSource(MappingDocument mappingDocument, String containingTableName, ColumnsAndFormulasSource columnsAndFormulasSource) {
        List<RelationalValueSource> sources = RelationalValueSourceHelper.buildValueSources(mappingDocument, containingTableName, columnsAndFormulasSource);
        if (sources.size() > 1) {
            String errorMessage = columnsAndFormulasSource.getSourceType().canBeNamed() && StringHelper.isNotEmpty(columnsAndFormulasSource.getSourceName()) ? String.format(Locale.ENGLISH, "Expecting just a single formula/column in context of <%s name=\"%s\"/>", columnsAndFormulasSource.getSourceType().getElementName(), columnsAndFormulasSource.getSourceName()) : String.format(Locale.ENGLISH, "Expecting just a single formula/column in context of <%s/>", columnsAndFormulasSource.getSourceType().getElementName());
            throw new MappingException(errorMessage, mappingDocument.getOrigin());
        }
        RelationalValueSource result = sources.get(0);
        if (!ColumnSource.class.isInstance(result)) {
            String errorMessage = columnsAndFormulasSource.getSourceType().canBeNamed() && StringHelper.isNotEmpty(columnsAndFormulasSource.getSourceName()) ? String.format(Locale.ENGLISH, "Expecting single column in context of <%s name=\"%s\"/>, but found formula [%s]", columnsAndFormulasSource.getSourceType().getElementName(), columnsAndFormulasSource.getSourceName(), ((DerivedValueSource)result).getExpression()) : String.format(Locale.ENGLISH, "Expecting single column in context of <%s/>, but found formula [%s]", columnsAndFormulasSource.getSourceType().getElementName(), ((DerivedValueSource)result).getExpression());
            throw new MappingException(errorMessage, mappingDocument.getOrigin());
        }
        return (ColumnSource)result;
    }

    public static List<ColumnSource> buildColumnSources(MappingDocument mappingDocument, String containingTableName, ColumnsAndFormulasSource columnsAndFormulasSource) {
        List<RelationalValueSource> sources = RelationalValueSourceHelper.buildValueSources(mappingDocument, containingTableName, columnsAndFormulasSource);
        ArrayList<ColumnSource> columnSources = CollectionHelper.arrayList(sources.size());
        for (RelationalValueSource source : sources) {
            if (!ColumnSource.class.isInstance(source)) {
                String errorMessage = columnsAndFormulasSource.getSourceType().canBeNamed() && StringHelper.isNotEmpty(columnsAndFormulasSource.getSourceName()) ? String.format(Locale.ENGLISH, "Expecting only columns in context of <%s name=\"%s\"/>, but found formula [%s]", columnsAndFormulasSource.getSourceType().getElementName(), columnsAndFormulasSource.getSourceName(), ((DerivedValueSource)source).getExpression()) : String.format(Locale.ENGLISH, "Expecting only columns in context of <%s/>, but found formula [%s]", columnsAndFormulasSource.getSourceType().getElementName(), ((DerivedValueSource)source).getExpression());
                throw new MappingException(errorMessage, mappingDocument.getOrigin());
            }
            columnSources.add((ColumnSource)source);
        }
        return columnSources;
    }

    public static List<RelationalValueSource> buildValueSources(MappingDocument mappingDocument, String containingTableName, ColumnsAndFormulasSource columnsAndFormulasSource) {
        ArrayList<RelationalValueSource> result = new ArrayList<RelationalValueSource>();
        if (StringHelper.isNotEmpty(columnsAndFormulasSource.getFormulaAttribute())) {
            RelationalValueSourceHelper.validateUseOfFormulaAttribute(mappingDocument, columnsAndFormulasSource);
            result.add(new FormulaImpl(mappingDocument, containingTableName, columnsAndFormulasSource.getFormulaAttribute()));
        } else if (CollectionHelper.isNotEmpty(columnsAndFormulasSource.getColumnOrFormulaElements())) {
            RelationalValueSourceHelper.validateUseOfColumnOrFormulaNestedElements(mappingDocument, columnsAndFormulasSource);
            for (Object selectable : columnsAndFormulasSource.getColumnOrFormulaElements()) {
                if (selectable instanceof JaxbHbmColumnType) {
                    JaxbHbmColumnType columnElement = (JaxbHbmColumnType)selectable;
                    result.add(new ColumnSourceImpl(mappingDocument, containingTableName, columnElement, columnsAndFormulasSource.getIndexConstraintNames(), columnsAndFormulasSource.getUniqueKeyConstraintNames()));
                    continue;
                }
                if (selectable instanceof String) {
                    result.add(new FormulaImpl(mappingDocument, containingTableName, (String)selectable));
                    continue;
                }
                throw new MappingException("Unexpected column/formula JAXB type : " + selectable.getClass().getName(), mappingDocument.getOrigin());
            }
        } else {
            result.add(new ColumnAttributeSourceImpl(mappingDocument, containingTableName, columnsAndFormulasSource.getColumnAttribute(), columnsAndFormulasSource.getSizeSource(), RelationalValueSourceHelper.interpretNullabilityToTruthValue(columnsAndFormulasSource.isNullable()), columnsAndFormulasSource.isUnique() ? TruthValue.TRUE : TruthValue.FALSE, columnsAndFormulasSource.getIndexConstraintNames(), columnsAndFormulasSource.getUniqueKeyConstraintNames()));
        }
        return result;
    }

    private static TruthValue interpretNullabilityToTruthValue(Boolean nullable) {
        if (nullable == null) {
            return TruthValue.UNKNOWN;
        }
        return nullable != false ? TruthValue.TRUE : TruthValue.FALSE;
    }

    private static void validateUseOfFormulaAttribute(MappingDocument sourceDocument, ColumnsAndFormulasSource columnsAndFormulasSource) {
        if (StringHelper.isNotEmpty(columnsAndFormulasSource.getColumnAttribute())) {
            String errorMessage = columnsAndFormulasSource.getSourceType().canBeNamed() && StringHelper.isNotEmpty(columnsAndFormulasSource.getSourceName()) ? String.format(Locale.ENGLISH, "column attribute and formula attribute may not be specified together near <%s name=\"%s\" column=\"%s\" formula=\"%s\" />", columnsAndFormulasSource.getSourceType().getElementName(), columnsAndFormulasSource.getSourceName(), columnsAndFormulasSource.getColumnAttribute(), columnsAndFormulasSource.getFormulaAttribute()) : String.format(Locale.ENGLISH, "column attribute and formula attribute may not be specified together near <%s column=\"%s\" formula=\"%s\" />", columnsAndFormulasSource.getSourceType().getElementName(), columnsAndFormulasSource.getColumnAttribute(), columnsAndFormulasSource.getFormulaAttribute());
            throw new MappingException(errorMessage, sourceDocument.getOrigin());
        }
        if (CollectionHelper.isNotEmpty(columnsAndFormulasSource.getColumnOrFormulaElements())) {
            String errorMessage = columnsAndFormulasSource.getSourceType().canBeNamed() && StringHelper.isNotEmpty(columnsAndFormulasSource.getSourceName()) ? String.format(Locale.ENGLISH, "formula attribute may not be specified along with <column/> or <formula/> subelement(s) near <%s name=\"%s\" formula=\"%s\" />", columnsAndFormulasSource.getSourceType().getElementName(), columnsAndFormulasSource.getSourceName(), columnsAndFormulasSource.getFormulaAttribute()) : String.format(Locale.ENGLISH, "formula attribute may not be specified along with <column/> or <formula/> subelement(s) near <%s formula=\"%s\" />", columnsAndFormulasSource.getSourceType().getElementName(), columnsAndFormulasSource.getFormulaAttribute());
            throw new MappingException(errorMessage, sourceDocument.getOrigin());
        }
    }

    private static void validateUseOfColumnOrFormulaNestedElements(MappingDocument sourceDocument, ColumnsAndFormulasSource columnsAndFormulasSource) {
        if (StringHelper.isNotEmpty(columnsAndFormulasSource.getColumnAttribute())) {
            String errorMessage = columnsAndFormulasSource.getSourceType().canBeNamed() && StringHelper.isNotEmpty(columnsAndFormulasSource.getSourceName()) ? String.format(Locale.ENGLISH, "column attribute may not be specified along with <column/> or <formula/> subelement(s) near <%s name=\"%s\" column=\"%s\" />", columnsAndFormulasSource.getSourceType().getElementName(), columnsAndFormulasSource.getSourceName(), columnsAndFormulasSource.getColumnAttribute()) : String.format(Locale.ENGLISH, "column attribute may not be specified along with <column/> or <formula/> subelement(s) near <%s column=\"%s\" />", columnsAndFormulasSource.getSourceType().getElementName(), columnsAndFormulasSource.getColumnAttribute());
            throw new MappingException(errorMessage, sourceDocument.getOrigin());
        }
    }

    private static void validateCustomWriteFragment(MappingDocument sourceDocument, ColumnsAndFormulasSource columnsAndFormulasSource, JaxbHbmColumnType columnMapping, String customWrite) {
        if (customWrite != null && !customWrite.matches("[^?]*\\?[^?]*")) {
            String errorMessage = columnsAndFormulasSource.getSourceType().canBeNamed() && StringHelper.isNotEmpty(columnsAndFormulasSource.getSourceName()) ? String.format(Locale.ENGLISH, "write expression must contain exactly one value placeholder ('?') character near <column name=\"%s\" ... write=\"%s\" /> for <%s name=\"%s\" />", columnMapping.getName(), customWrite, columnsAndFormulasSource.getSourceType().getElementName(), columnsAndFormulasSource.getSourceName()) : String.format(Locale.ENGLISH, "write expression must contain exactly one value placeholder ('?') character near <column name=\"%s\" ... write=\"%s\" /> for <%s />", columnMapping.getName(), customWrite, columnsAndFormulasSource.getSourceType().getElementName());
            throw new MappingException(errorMessage, sourceDocument.getOrigin());
        }
    }

    public static abstract class AbstractColumnsAndFormulasSource
    implements ColumnsAndFormulasSource {
        @Override
        public String getFormulaAttribute() {
            return null;
        }

        @Override
        public String getColumnAttribute() {
            return null;
        }

        @Override
        public List getColumnOrFormulaElements() {
            return Collections.emptyList();
        }

        @Override
        public SizeSource getSizeSource() {
            return null;
        }

        @Override
        public Boolean isNullable() {
            return null;
        }

        @Override
        public Set<String> getIndexConstraintNames() {
            return Collections.emptySet();
        }

        @Override
        public boolean isUnique() {
            return false;
        }

        @Override
        public Set<String> getUniqueKeyConstraintNames() {
            return Collections.emptySet();
        }
    }

    public static interface ColumnsAndFormulasSource {
        public XmlElementMetadata getSourceType();

        public String getSourceName();

        public String getFormulaAttribute();

        public List getColumnOrFormulaElements();

        public String getColumnAttribute();

        public SizeSource getSizeSource();

        public Boolean isNullable();

        public boolean isUnique();

        public Set<String> getIndexConstraintNames();

        public Set<String> getUniqueKeyConstraintNames();
    }
}

