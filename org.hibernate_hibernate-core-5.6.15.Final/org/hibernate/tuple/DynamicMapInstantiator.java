/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tuple.Instantiator;

public class DynamicMapInstantiator
implements Instantiator {
    public static final String KEY = "$type$";
    private String entityName;
    private Set isInstanceEntityNames = new HashSet();

    public DynamicMapInstantiator() {
        this.entityName = null;
    }

    public DynamicMapInstantiator(PersistentClass mappingInfo) {
        this.entityName = mappingInfo.getEntityName();
        this.isInstanceEntityNames.add(this.entityName);
        if (mappingInfo.hasSubclasses()) {
            Iterator itr = mappingInfo.getSubclassClosureIterator();
            while (itr.hasNext()) {
                PersistentClass subclassInfo = (PersistentClass)itr.next();
                this.isInstanceEntityNames.add(subclassInfo.getEntityName());
            }
        }
    }

    @Override
    public final Object instantiate(Serializable id) {
        return this.instantiate();
    }

    @Override
    public final Object instantiate() {
        Map map = this.generateMap();
        if (this.entityName != null) {
            map.put(KEY, this.entityName);
        }
        return map;
    }

    @Override
    public final boolean isInstance(Object object) {
        if (object instanceof Map) {
            if (this.entityName == null) {
                return true;
            }
            String type = (String)((Map)object).get(KEY);
            return type == null || this.isInstanceEntityNames.contains(type);
        }
        return false;
    }

    protected Map generateMap() {
        return new HashMap();
    }
}

