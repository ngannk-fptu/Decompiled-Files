/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.CollectionUtils
 */
package org.eclipse.gemini.blueprint.compendium.internal.cm.util;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import org.springframework.util.CollectionUtils;

public class PropertiesUtil {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Properties initProperties(Properties localMap, boolean localOverride, Map<?, ?> source, Properties target) {
        Properties properties = target;
        synchronized (properties) {
            target.clear();
            if (localMap != null && !localOverride) {
                CollectionUtils.mergePropertiesIntoMap((Properties)localMap, (Map)target);
            }
            if (source != null) {
                target.putAll(source);
            }
            if (localMap != null && localOverride) {
                CollectionUtils.mergePropertiesIntoMap((Properties)localMap, (Map)target);
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Properties initProperties(Properties localMap, boolean localOverride, Dictionary source, Properties target) {
        Properties properties = target;
        synchronized (properties) {
            target.clear();
            if (localMap != null && !localOverride) {
                CollectionUtils.mergePropertiesIntoMap((Properties)localMap, (Map)target);
            }
            if (source != null) {
                Enumeration keys = source.keys();
                while (keys.hasMoreElements()) {
                    Object key = keys.nextElement();
                    Object value = source.get(key);
                    if (key == null || value == null) continue;
                    target.put(key, value);
                }
            }
            if (localMap != null && localOverride) {
                CollectionUtils.mergePropertiesIntoMap((Properties)localMap, (Map)target);
            }
            return target;
        }
    }
}

