/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.condition.UrlReadingCondition
 */
package com.atlassian.plugin.webresource.impl.support;

import com.atlassian.plugin.webresource.condition.DecoratingAndCompositeCondition;
import com.atlassian.plugin.webresource.condition.DecoratingCondition;
import com.atlassian.plugin.webresource.condition.DecoratingUrlReadingCondition;
import com.atlassian.plugin.webresource.condition.UrlReadingCondition;
import com.atlassian.plugin.webresource.impl.CachedCondition;
import com.atlassian.plugin.webresource.impl.support.Tuple;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConditionInstanceCache {
    public static final boolean CONDITION_INTERN_ENABLED = !Boolean.getBoolean("com.atlassian.plugin.webresource.disableconditionintern");
    Map<Set<Tuple<Class<? extends UrlReadingCondition>, Map<String, String>>>, CachedCondition> instances = new HashMap<Set<Tuple<Class<? extends UrlReadingCondition>, Map<String, String>>>, CachedCondition>();

    public CachedCondition intern(DecoratingCondition condition) {
        if (CONDITION_INTERN_ENABLED && DecoratingAndCompositeCondition.class.equals(condition.getClass())) {
            List conditions = ((DecoratingAndCompositeCondition)condition).getConditions();
            ArrayList<DecoratingUrlReadingCondition> urlReadingConditions = new ArrayList<DecoratingUrlReadingCondition>();
            for (Object c : conditions) {
                if (!(c instanceof DecoratingUrlReadingCondition)) continue;
                urlReadingConditions.add((DecoratingUrlReadingCondition)c);
            }
            if (urlReadingConditions.size() == conditions.size()) {
                HashSet key = new HashSet();
                for (DecoratingUrlReadingCondition c : urlReadingConditions) {
                    key.add(new Tuple(c.getUrlReadingCondition().getClass(), c.getParams()));
                }
                CachedCondition cachedCondition = this.instances.get(key);
                if (cachedCondition == null) {
                    cachedCondition = new CachedCondition(condition);
                    this.instances.put(key, cachedCondition);
                }
                return cachedCondition;
            }
            return new CachedCondition(condition);
        }
        return new CachedCondition(condition);
    }
}

