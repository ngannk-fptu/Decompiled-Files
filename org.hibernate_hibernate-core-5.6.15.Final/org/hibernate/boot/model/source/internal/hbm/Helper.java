/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCacheType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmConfigParameterType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCustomSqlDmlType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmDiscriminatorSubclassEntityType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmEntityBaseDefinition;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmJoinedSubclassEntityType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNaturalIdCacheType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmToolingHintType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmUnionSubclassEntityType;
import org.hibernate.boot.jaxb.hbm.spi.TableInformationContainer;
import org.hibernate.boot.jaxb.hbm.spi.ToolingHintContainer;
import org.hibernate.boot.model.Caching;
import org.hibernate.boot.model.CustomSql;
import org.hibernate.boot.model.TruthValue;
import org.hibernate.boot.model.source.internal.hbm.InLineViewSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.SizeSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.TableSourceImpl;
import org.hibernate.boot.model.source.spi.InheritanceType;
import org.hibernate.boot.model.source.spi.SizeSource;
import org.hibernate.boot.model.source.spi.TableSpecificationSource;
import org.hibernate.boot.model.source.spi.ToolingHint;
import org.hibernate.boot.model.source.spi.ToolingHintContext;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.internal.util.StringHelper;

public class Helper {
    public static InheritanceType interpretInheritanceType(JaxbHbmEntityBaseDefinition entityElement) {
        if (JaxbHbmDiscriminatorSubclassEntityType.class.isInstance(entityElement)) {
            return InheritanceType.DISCRIMINATED;
        }
        if (JaxbHbmJoinedSubclassEntityType.class.isInstance(entityElement)) {
            return InheritanceType.JOINED;
        }
        if (JaxbHbmUnionSubclassEntityType.class.isInstance(entityElement)) {
            return InheritanceType.UNION;
        }
        return InheritanceType.NO_INHERITANCE;
    }

    public static CustomSql buildCustomSql(JaxbHbmCustomSqlDmlType customSqlElement) {
        if (customSqlElement == null) {
            return null;
        }
        ExecuteUpdateResultCheckStyle checkStyle = customSqlElement.getCheck() == null ? (customSqlElement.isCallable() ? ExecuteUpdateResultCheckStyle.NONE : ExecuteUpdateResultCheckStyle.COUNT) : customSqlElement.getCheck();
        return new CustomSql(customSqlElement.getValue(), customSqlElement.isCallable(), checkStyle);
    }

    public static Caching createCaching(JaxbHbmCacheType cacheElement) {
        if (cacheElement == null) {
            return new Caching(TruthValue.FALSE);
        }
        boolean cacheLazyProps = cacheElement.getInclude() == null || !"non-lazy".equals(cacheElement.getInclude().value());
        return new Caching(cacheElement.getRegion(), cacheElement.getUsage(), cacheLazyProps, TruthValue.TRUE);
    }

    public static Caching createNaturalIdCaching(JaxbHbmNaturalIdCacheType cacheElement) {
        if (cacheElement == null) {
            return new Caching(TruthValue.UNKNOWN);
        }
        return new Caching(StringHelper.nullIfEmpty(cacheElement.getRegion()), null, false, TruthValue.TRUE);
    }

    public static String getPropertyAccessorName(String access, boolean isEmbedded, String defaultAccess) {
        return Helper.getValue(access, isEmbedded ? "embedded" : defaultAccess);
    }

    public static <T> T getValue(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static Map<String, String> extractParameters(List<JaxbHbmConfigParameterType> xmlParamElements) {
        if (xmlParamElements == null || xmlParamElements.isEmpty()) {
            return Collections.emptyMap();
        }
        HashMap<String, String> params = new HashMap<String, String>();
        for (JaxbHbmConfigParameterType paramElement : xmlParamElements) {
            params.put(paramElement.getName(), paramElement.getValue());
        }
        return params;
    }

    static ToolingHintContext collectToolingHints(ToolingHintContext baseline, ToolingHintContainer toolingHintContainer) {
        return Helper.collectToolingHints(baseline, toolingHintContainer, false);
    }

    private static ToolingHintContext collectToolingHints(ToolingHintContext baseline, ToolingHintContainer toolingHintContainer, boolean onlyInheritable) {
        ToolingHintContext localToolingHints = new ToolingHintContext(baseline);
        if (toolingHintContainer != null && toolingHintContainer.getToolingHints() != null) {
            for (JaxbHbmToolingHintType toolingHintJaxbBinding : toolingHintContainer.getToolingHints()) {
                ToolingHint inherited;
                if (onlyInheritable && !toolingHintJaxbBinding.isInheritable()) continue;
                String hintName = toolingHintJaxbBinding.getName();
                ToolingHint toolingHint = localToolingHints.getToolingHint(hintName);
                if (toolingHint == null) {
                    toolingHint = new ToolingHint(hintName, toolingHintJaxbBinding.isInheritable());
                    localToolingHints.add(toolingHint);
                } else if (baseline != null && toolingHint == (inherited = baseline.getToolingHint(hintName))) {
                    toolingHint = new ToolingHint(hintName, toolingHintJaxbBinding.isInheritable());
                    localToolingHints.add(toolingHint);
                }
                toolingHint.addValue(toolingHintJaxbBinding.getValue());
            }
        }
        return localToolingHints;
    }

    public static TableSpecificationSource createTableSource(MappingDocument mappingDocument, TableInformationContainer entityElement, InLineViewNameInferrer inLineViewNameInferrer) {
        return Helper.createTableSource(mappingDocument, entityElement, inLineViewNameInferrer, null, null, null);
    }

    public static TableSpecificationSource createTableSource(MappingDocument mappingDocument, TableInformationContainer tableInformationContainer, InLineViewNameInferrer inLineViewNameInferrer, String rowId, String comment, String checkConstraint) {
        if (StringHelper.isEmpty(tableInformationContainer.getSubselectAttribute()) && StringHelper.isEmpty(tableInformationContainer.getSubselect())) {
            return new TableSourceImpl(mappingDocument, tableInformationContainer.getSchema(), tableInformationContainer.getCatalog(), tableInformationContainer.getTable(), rowId, comment, checkConstraint);
        }
        return new InLineViewSourceImpl(mappingDocument, tableInformationContainer.getSchema(), tableInformationContainer.getCatalog(), tableInformationContainer.getSubselectAttribute() != null ? tableInformationContainer.getSubselectAttribute() : tableInformationContainer.getSubselect(), tableInformationContainer.getTable() == null ? inLineViewNameInferrer.inferInLineViewName() : tableInformationContainer.getTable(), comment);
    }

    public static SizeSource interpretSizeSource(Integer length, Integer scale, Integer precision) {
        if (length != null || precision != null || scale != null) {
            return new SizeSourceImpl(length, scale, precision);
        }
        return null;
    }

    public static SizeSource interpretSizeSource(Integer length, String scale, String precision) {
        return Helper.interpretSizeSource(length, scale == null ? null : Integer.valueOf(Integer.parseInt(scale)), precision == null ? null : Integer.valueOf(Integer.parseInt(precision)));
    }

    public static Class reflectedPropertyClass(MetadataBuildingContext buildingContext, String attributeOwnerClassName, String attributeName) {
        Class attributeOwnerClass = buildingContext.getBootstrapContext().getClassLoaderAccess().classForName(attributeOwnerClassName);
        return Helper.reflectedPropertyClass(buildingContext, attributeOwnerClass, attributeName);
    }

    public static Class reflectedPropertyClass(MetadataBuildingContext buildingContext, Class attributeOwnerClass, String attributeName) {
        return ReflectHelper.reflectedPropertyClass(attributeOwnerClass, attributeName);
    }

    public static interface InLineViewNameInferrer {
        public String inferInLineViewName();
    }
}

