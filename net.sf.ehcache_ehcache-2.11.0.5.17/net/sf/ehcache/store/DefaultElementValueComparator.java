/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store;

import java.util.Arrays;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.ElementValueComparator;

public class DefaultElementValueComparator
implements ElementValueComparator {
    public DefaultElementValueComparator(CacheConfiguration cacheConfiguration) {
    }

    @Override
    public boolean equals(Element e1, Element e2) {
        if (e1 == null && e2 == null) {
            return true;
        }
        if (e1 != null && e1.equals(e2)) {
            if (e1.getObjectValue() == null) {
                return e2.getObjectValue() == null;
            }
            return DefaultElementValueComparator.compareValues(e1.getObjectValue(), e2.getObjectValue());
        }
        return false;
    }

    private static boolean compareValues(Object objectValue1, Object objectValue2) {
        if (objectValue1 != null && objectValue2 != null && objectValue1.getClass().isArray() && objectValue2.getClass().isArray()) {
            return Arrays.deepEquals(new Object[]{objectValue1}, new Object[]{objectValue2});
        }
        if (objectValue1 == null) {
            return objectValue2 == null;
        }
        return objectValue1.equals(objectValue2);
    }
}

