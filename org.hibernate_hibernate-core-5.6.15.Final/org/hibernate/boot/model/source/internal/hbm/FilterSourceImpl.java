/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.boot.MappingException;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFilterAliasMappingType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFilterType;
import org.hibernate.boot.model.source.internal.hbm.AbstractHbmSourceNode;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.spi.FilterSource;
import org.hibernate.internal.util.NullnessHelper;
import org.hibernate.internal.util.StringHelper;

public class FilterSourceImpl
extends AbstractHbmSourceNode
implements FilterSource {
    private final String name;
    private final String condition;
    private final boolean autoAliasInjection;
    private final Map<String, String> aliasTableMap = new HashMap<String, String>();
    private final Map<String, String> aliasEntityMap = new HashMap<String, String>();

    public FilterSourceImpl(MappingDocument mappingDocument, JaxbHbmFilterType filterElement) {
        super(mappingDocument);
        this.name = filterElement.getName();
        String explicitAutoAliasInjectionSetting = filterElement.getAutoAliasInjection();
        String conditionAttribute = filterElement.getCondition();
        String conditionContent = null;
        for (Serializable content : filterElement.getContent()) {
            if (String.class.isInstance(content)) {
                String str = content.toString();
                if (StringHelper.isBlank(str)) continue;
                conditionContent = str.trim();
                continue;
            }
            JaxbHbmFilterAliasMappingType aliasMapping = (JaxbHbmFilterAliasMappingType)JaxbHbmFilterAliasMappingType.class.cast(content);
            if (StringHelper.isNotEmpty(aliasMapping.getTable())) {
                this.aliasTableMap.put(aliasMapping.getAlias(), aliasMapping.getTable());
                continue;
            }
            if (StringHelper.isNotEmpty(aliasMapping.getEntity())) {
                this.aliasEntityMap.put(aliasMapping.getAlias(), aliasMapping.getTable());
                continue;
            }
            throw new MappingException("filter alias must define either table or entity attribute", mappingDocument.getOrigin());
        }
        this.condition = NullnessHelper.coalesce(conditionContent, conditionAttribute);
        this.autoAliasInjection = StringHelper.isNotEmpty(explicitAutoAliasInjectionSetting) ? Boolean.valueOf(explicitAutoAliasInjectionSetting) : true;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getCondition() {
        return this.condition;
    }

    @Override
    public boolean shouldAutoInjectAliases() {
        return this.autoAliasInjection;
    }

    @Override
    public Map<String, String> getAliasToTableMap() {
        return this.aliasTableMap;
    }

    @Override
    public Map<String, String> getAliasToEntityMap() {
        return this.aliasEntityMap;
    }
}

