/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ClassUtils
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.configuration2.interpol;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.configuration2.interpol.Lookup;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConstantLookup
implements Lookup {
    private static final char FIELD_SEPRATOR = '.';
    private static final Map<String, Object> CACHE = new ConcurrentHashMap<String, Object>();
    private final Log log = LogFactory.getLog(this.getClass());

    @Override
    public Object lookup(String var) {
        if (var == null) {
            return null;
        }
        return CACHE.computeIfAbsent(var, k -> {
            int fieldPos = var.lastIndexOf(46);
            if (fieldPos >= 0) {
                try {
                    return this.resolveField(var.substring(0, fieldPos), var.substring(fieldPos + 1));
                }
                catch (Exception ex) {
                    this.log.warn((Object)("Could not obtain value for variable " + var), (Throwable)ex);
                }
            }
            return null;
        });
    }

    public static void clear() {
        CACHE.clear();
    }

    protected Object resolveField(String className, String fieldName) throws Exception {
        return this.fetchClass(className).getField(fieldName).get(null);
    }

    protected Class<?> fetchClass(String className) throws ClassNotFoundException {
        return ClassUtils.getClass((String)className);
    }
}

