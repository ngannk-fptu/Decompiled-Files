/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.ServiceReference
 *  org.osgi.service.event.Event
 */
package org.apache.sling.commons.osgi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class OsgiUtil {
    public static boolean toBoolean(Object propValue, boolean defaultValue) {
        if ((propValue = OsgiUtil.toObject(propValue)) instanceof Boolean) {
            return (Boolean)propValue;
        }
        if (propValue != null) {
            return Boolean.valueOf(String.valueOf(propValue));
        }
        return defaultValue;
    }

    public static String toString(Object propValue, String defaultValue) {
        return (propValue = OsgiUtil.toObject(propValue)) != null ? propValue.toString() : defaultValue;
    }

    public static long toLong(Object propValue, long defaultValue) {
        if ((propValue = OsgiUtil.toObject(propValue)) instanceof Long) {
            return (Long)propValue;
        }
        if (propValue != null) {
            try {
                return Long.valueOf(String.valueOf(propValue));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return defaultValue;
    }

    public static int toInteger(Object propValue, int defaultValue) {
        if ((propValue = OsgiUtil.toObject(propValue)) instanceof Integer) {
            return (Integer)propValue;
        }
        if (propValue != null) {
            try {
                return Integer.valueOf(String.valueOf(propValue));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return defaultValue;
    }

    @Deprecated
    public static double getProperty(Object propValue, double defaultValue) {
        return OsgiUtil.toDouble(propValue, defaultValue);
    }

    public static double toDouble(Object propValue, double defaultValue) {
        if ((propValue = OsgiUtil.toObject(propValue)) instanceof Double) {
            return (Double)propValue;
        }
        if (propValue != null) {
            try {
                return Double.valueOf(String.valueOf(propValue));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return defaultValue;
    }

    public static Object toObject(Object propValue) {
        if (propValue == null) {
            return null;
        }
        if (propValue.getClass().isArray()) {
            Object[] prop = (Object[])propValue;
            return prop.length > 0 ? prop[0] : null;
        }
        if (propValue instanceof Collection) {
            Collection prop = (Collection)propValue;
            return prop.isEmpty() ? null : prop.iterator().next();
        }
        return propValue;
    }

    public static String[] toStringArray(Object propValue) {
        return OsgiUtil.toStringArray(propValue, null);
    }

    public static String[] toStringArray(Object propValue, String[] defaultArray) {
        if (propValue == null) {
            return defaultArray;
        }
        if (propValue instanceof String) {
            return new String[]{(String)propValue};
        }
        if (propValue instanceof String[]) {
            return (String[])propValue;
        }
        if (propValue.getClass().isArray()) {
            Object[] valueArray = (Object[])propValue;
            ArrayList<String> values = new ArrayList<String>(valueArray.length);
            for (Object value : valueArray) {
                if (value == null) continue;
                values.add(value.toString());
            }
            return values.toArray(new String[values.size()]);
        }
        if (propValue instanceof Collection) {
            Collection valueCollection = (Collection)propValue;
            ArrayList<String> valueList = new ArrayList<String>(valueCollection.size());
            for (Object value : valueCollection) {
                if (value == null) continue;
                valueList.add(value.toString());
            }
            return valueList.toArray(new String[valueList.size()]);
        }
        return defaultArray;
    }

    public static Event createEvent(Bundle sourceBundle, ServiceReference sourceService, String eventName, Map<String, Object> props) {
        Hashtable<String, Object> table = new Hashtable<String, Object>(props);
        if (sourceService != null) {
            ((Dictionary)table).put("service", sourceService);
            ((Dictionary)table).put("service.id", sourceService.getProperty("service.id"));
            ((Dictionary)table).put("service.objectClass", sourceService.getProperty("objectClass"));
            if (sourceService.getProperty("service.pid") != null) {
                ((Dictionary)table).put("service.pid", sourceService.getProperty("service.pid"));
            }
        }
        if (sourceBundle != null) {
            ((Dictionary)table).put("bundle.symbolicName", sourceBundle.getSymbolicName());
        }
        ((Dictionary)table).put("timestamp", new Long(System.currentTimeMillis()));
        return new Event(eventName, table);
    }
}

