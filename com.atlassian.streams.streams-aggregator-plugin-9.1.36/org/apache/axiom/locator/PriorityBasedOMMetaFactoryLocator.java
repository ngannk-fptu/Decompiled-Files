/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.locator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.axiom.locator.Feature;
import org.apache.axiom.locator.Implementation;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMMetaFactoryLocator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class PriorityBasedOMMetaFactoryLocator
implements OMMetaFactoryLocator {
    private static final Log log = LogFactory.getLog(PriorityBasedOMMetaFactoryLocator.class);
    private final Map factories = new HashMap();

    PriorityBasedOMMetaFactoryLocator() {
    }

    void loadImplementations(List implementations) {
        HashMap<String, Integer> priorityMap = new HashMap<String, Integer>();
        this.factories.clear();
        for (Implementation implementation : implementations) {
            Feature[] features = implementation.getFeatures();
            for (int i = 0; i < features.length; ++i) {
                Feature feature = features[i];
                String name = feature.getName();
                int priority = feature.getPriority();
                Integer highestPriority = (Integer)priorityMap.get(name);
                if (highestPriority != null && priority <= highestPriority) continue;
                priorityMap.put(name, priority);
                this.factories.put(name, implementation.getMetaFactory());
            }
        }
        if (log.isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder("Meta factories:");
            for (Map.Entry entry : this.factories.entrySet()) {
                buffer.append("\n  ");
                buffer.append(entry.getKey());
                buffer.append(": ");
                buffer.append(entry.getValue().getClass().getName());
            }
            log.debug((Object)buffer);
        }
    }

    public OMMetaFactory getOMMetaFactory(String feature) {
        return (OMMetaFactory)this.factories.get(feature);
    }
}

