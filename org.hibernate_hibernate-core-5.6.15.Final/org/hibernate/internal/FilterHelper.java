/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal;

import java.util.List;
import java.util.Map;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.FilterAliasGenerator;
import org.hibernate.internal.FilterConfiguration;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.sql.Template;

public class FilterHelper {
    private final String[] filterNames;
    private final String[] filterConditions;
    private final boolean[] filterAutoAliasFlags;
    private final Map<String, String>[] filterAliasTableMaps;

    public FilterHelper(List<FilterConfiguration> filters, SessionFactoryImplementor factory) {
        int filterCount = filters.size();
        this.filterNames = new String[filterCount];
        this.filterConditions = new String[filterCount];
        this.filterAutoAliasFlags = new boolean[filterCount];
        this.filterAliasTableMaps = new Map[filterCount];
        filterCount = 0;
        for (FilterConfiguration filter : filters) {
            this.filterAutoAliasFlags[filterCount] = false;
            this.filterNames[filterCount] = StringHelper.safeInterning(filter.getName());
            this.filterConditions[filterCount] = StringHelper.safeInterning(filter.getCondition());
            this.filterAliasTableMaps[filterCount] = filter.getAliasTableMap(factory);
            if ((this.filterAliasTableMaps[filterCount].isEmpty() || FilterHelper.isTableFromPersistentClass(this.filterAliasTableMaps[filterCount])) && filter.useAutoAliasInjection()) {
                this.filterConditions[filterCount] = StringHelper.safeInterning(Template.renderWhereStringTemplate(filter.getCondition(), "$FILTER_PLACEHOLDER$", factory.getDialect(), factory.getSqlFunctionRegistry()));
                this.filterAutoAliasFlags[filterCount] = true;
            }
            this.filterConditions[filterCount] = StringHelper.safeInterning(StringHelper.replace(this.filterConditions[filterCount], ":", ":" + this.filterNames[filterCount] + "."));
            ++filterCount;
        }
    }

    private static boolean isTableFromPersistentClass(Map<String, String> aliasTableMap) {
        return aliasTableMap.size() == 1 && aliasTableMap.containsKey(null);
    }

    public boolean isAffectedBy(Map enabledFilters) {
        for (String filterName : this.filterNames) {
            if (!enabledFilters.containsKey(filterName)) continue;
            return true;
        }
        return false;
    }

    public String render(FilterAliasGenerator aliasGenerator, Map enabledFilters) {
        StringBuilder buffer = new StringBuilder();
        this.render(buffer, aliasGenerator, enabledFilters);
        return buffer.toString();
    }

    public void render(StringBuilder buffer, FilterAliasGenerator aliasGenerator, Map enabledFilters) {
        if (CollectionHelper.isEmpty(this.filterNames)) {
            return;
        }
        int max = this.filterNames.length;
        for (int i = 0; i < max; ++i) {
            String condition;
            if (!enabledFilters.containsKey(this.filterNames[i]) || !StringHelper.isNotEmpty(condition = this.filterConditions[i])) continue;
            buffer.append(" and ").append(this.render(aliasGenerator, i));
        }
    }

    private String render(FilterAliasGenerator aliasGenerator, int filterIndex) {
        Map<String, String> aliasTableMap = this.filterAliasTableMaps[filterIndex];
        String condition = this.filterConditions[filterIndex];
        if (this.filterAutoAliasFlags[filterIndex]) {
            return StringHelper.replace(condition, "$FILTER_PLACEHOLDER$", aliasGenerator.getAlias(aliasTableMap.get(null)));
        }
        if (FilterHelper.isTableFromPersistentClass(aliasTableMap)) {
            return condition.replace("{alias}", aliasGenerator.getAlias(aliasTableMap.get(null)));
        }
        for (Map.Entry<String, String> entry : aliasTableMap.entrySet()) {
            condition = condition.replace("{" + entry.getKey() + "}", aliasGenerator.getAlias(entry.getValue()));
        }
        return condition;
    }
}

