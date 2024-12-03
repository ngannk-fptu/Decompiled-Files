/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.persister.entity.Joinable;

public class FilterConfiguration {
    private final String name;
    private final String condition;
    private final boolean autoAliasInjection;
    private final Map<String, String> aliasTableMap;
    private final Map<String, String> aliasEntityMap;
    private final PersistentClass persistentClass;

    public FilterConfiguration(String name, String condition, boolean autoAliasInjection, Map<String, String> aliasTableMap, Map<String, String> aliasEntityMap, PersistentClass persistentClass) {
        this.name = name;
        this.condition = condition;
        this.autoAliasInjection = autoAliasInjection;
        this.aliasTableMap = aliasTableMap;
        this.aliasEntityMap = aliasEntityMap;
        this.persistentClass = persistentClass;
    }

    public String getName() {
        return this.name;
    }

    public String getCondition() {
        return this.condition;
    }

    public boolean useAutoAliasInjection() {
        return this.autoAliasInjection;
    }

    public Map<String, String> getAliasTableMap(SessionFactoryImplementor factory) {
        Map<String, String> mergedAliasTableMap = this.mergeAliasMaps(factory);
        if (!mergedAliasTableMap.isEmpty()) {
            return mergedAliasTableMap;
        }
        if (this.persistentClass != null) {
            String table = this.persistentClass.getTable().getQualifiedName(factory.getSqlStringGenerationContext());
            return Collections.singletonMap(null, table);
        }
        return Collections.emptyMap();
    }

    private Map<String, String> mergeAliasMaps(SessionFactoryImplementor factory) {
        HashMap<String, String> ret = new HashMap<String, String>();
        if (this.aliasTableMap != null) {
            ret.putAll(this.aliasTableMap);
        }
        if (this.aliasEntityMap != null) {
            for (Map.Entry<String, String> entry : this.aliasEntityMap.entrySet()) {
                ret.put(entry.getKey(), ((Joinable)Joinable.class.cast(factory.getEntityPersister(entry.getValue()))).getTableName());
            }
        }
        return ret;
    }
}

