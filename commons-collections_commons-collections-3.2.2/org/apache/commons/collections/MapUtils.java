/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.io.PrintStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.commons.collections.ArrayStack;
import org.apache.commons.collections.Factory;
import org.apache.commons.collections.KeyValue;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.map.FixedSizeMap;
import org.apache.commons.collections.map.FixedSizeSortedMap;
import org.apache.commons.collections.map.LazyMap;
import org.apache.commons.collections.map.LazySortedMap;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.collections.map.PredicatedMap;
import org.apache.commons.collections.map.PredicatedSortedMap;
import org.apache.commons.collections.map.TransformedMap;
import org.apache.commons.collections.map.TransformedSortedMap;
import org.apache.commons.collections.map.TypedMap;
import org.apache.commons.collections.map.TypedSortedMap;
import org.apache.commons.collections.map.UnmodifiableMap;
import org.apache.commons.collections.map.UnmodifiableSortedMap;

public class MapUtils {
    public static final Map EMPTY_MAP = UnmodifiableMap.decorate(new HashMap(1));
    public static final SortedMap EMPTY_SORTED_MAP = UnmodifiableSortedMap.decorate(new TreeMap());
    private static final String INDENT_STRING = "    ";

    public static Object getObject(Map map, Object key) {
        if (map != null) {
            return map.get(key);
        }
        return null;
    }

    public static String getString(Map map, Object key) {
        Object answer;
        if (map != null && (answer = map.get(key)) != null) {
            return answer.toString();
        }
        return null;
    }

    public static Boolean getBoolean(Map map, Object key) {
        Object answer;
        if (map != null && (answer = map.get(key)) != null) {
            if (answer instanceof Boolean) {
                return (Boolean)answer;
            }
            if (answer instanceof String) {
                return new Boolean((String)answer);
            }
            if (answer instanceof Number) {
                Number n = (Number)answer;
                return n.intValue() != 0 ? Boolean.TRUE : Boolean.FALSE;
            }
        }
        return null;
    }

    public static Number getNumber(Map map, Object key) {
        Object answer;
        if (map != null && (answer = map.get(key)) != null) {
            if (answer instanceof Number) {
                return (Number)answer;
            }
            if (answer instanceof String) {
                try {
                    String text = (String)answer;
                    return NumberFormat.getInstance().parse(text);
                }
                catch (ParseException parseException) {
                    // empty catch block
                }
            }
        }
        return null;
    }

    public static Byte getByte(Map map, Object key) {
        Number answer = MapUtils.getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Byte) {
            return (Byte)answer;
        }
        return new Byte(answer.byteValue());
    }

    public static Short getShort(Map map, Object key) {
        Number answer = MapUtils.getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Short) {
            return (Short)answer;
        }
        return new Short(answer.shortValue());
    }

    public static Integer getInteger(Map map, Object key) {
        Number answer = MapUtils.getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Integer) {
            return (Integer)answer;
        }
        return new Integer(answer.intValue());
    }

    public static Long getLong(Map map, Object key) {
        Number answer = MapUtils.getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Long) {
            return (Long)answer;
        }
        return new Long(answer.longValue());
    }

    public static Float getFloat(Map map, Object key) {
        Number answer = MapUtils.getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Float) {
            return (Float)answer;
        }
        return new Float(answer.floatValue());
    }

    public static Double getDouble(Map map, Object key) {
        Number answer = MapUtils.getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Double) {
            return (Double)answer;
        }
        return new Double(answer.doubleValue());
    }

    public static Map getMap(Map map, Object key) {
        Object answer;
        if (map != null && (answer = map.get(key)) != null && answer instanceof Map) {
            return (Map)answer;
        }
        return null;
    }

    public static Object getObject(Map map, Object key, Object defaultValue) {
        Object answer;
        if (map != null && (answer = map.get(key)) != null) {
            return answer;
        }
        return defaultValue;
    }

    public static String getString(Map map, Object key, String defaultValue) {
        String answer = MapUtils.getString(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }

    public static Boolean getBoolean(Map map, Object key, Boolean defaultValue) {
        Boolean answer = MapUtils.getBoolean(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }

    public static Number getNumber(Map map, Object key, Number defaultValue) {
        Number answer = MapUtils.getNumber(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }

    public static Byte getByte(Map map, Object key, Byte defaultValue) {
        Byte answer = MapUtils.getByte(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }

    public static Short getShort(Map map, Object key, Short defaultValue) {
        Short answer = MapUtils.getShort(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }

    public static Integer getInteger(Map map, Object key, Integer defaultValue) {
        Integer answer = MapUtils.getInteger(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }

    public static Long getLong(Map map, Object key, Long defaultValue) {
        Long answer = MapUtils.getLong(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }

    public static Float getFloat(Map map, Object key, Float defaultValue) {
        Float answer = MapUtils.getFloat(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }

    public static Double getDouble(Map map, Object key, Double defaultValue) {
        Double answer = MapUtils.getDouble(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }

    public static Map getMap(Map map, Object key, Map defaultValue) {
        Map answer = MapUtils.getMap(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }

    public static boolean getBooleanValue(Map map, Object key) {
        Boolean booleanObject = MapUtils.getBoolean(map, key);
        if (booleanObject == null) {
            return false;
        }
        return booleanObject;
    }

    public static byte getByteValue(Map map, Object key) {
        Byte byteObject = MapUtils.getByte(map, key);
        if (byteObject == null) {
            return 0;
        }
        return byteObject;
    }

    public static short getShortValue(Map map, Object key) {
        Short shortObject = MapUtils.getShort(map, key);
        if (shortObject == null) {
            return 0;
        }
        return shortObject;
    }

    public static int getIntValue(Map map, Object key) {
        Integer integerObject = MapUtils.getInteger(map, key);
        if (integerObject == null) {
            return 0;
        }
        return integerObject;
    }

    public static long getLongValue(Map map, Object key) {
        Long longObject = MapUtils.getLong(map, key);
        if (longObject == null) {
            return 0L;
        }
        return longObject;
    }

    public static float getFloatValue(Map map, Object key) {
        Float floatObject = MapUtils.getFloat(map, key);
        if (floatObject == null) {
            return 0.0f;
        }
        return floatObject.floatValue();
    }

    public static double getDoubleValue(Map map, Object key) {
        Double doubleObject = MapUtils.getDouble(map, key);
        if (doubleObject == null) {
            return 0.0;
        }
        return doubleObject;
    }

    public static boolean getBooleanValue(Map map, Object key, boolean defaultValue) {
        Boolean booleanObject = MapUtils.getBoolean(map, key);
        if (booleanObject == null) {
            return defaultValue;
        }
        return booleanObject;
    }

    public static byte getByteValue(Map map, Object key, byte defaultValue) {
        Byte byteObject = MapUtils.getByte(map, key);
        if (byteObject == null) {
            return defaultValue;
        }
        return byteObject;
    }

    public static short getShortValue(Map map, Object key, short defaultValue) {
        Short shortObject = MapUtils.getShort(map, key);
        if (shortObject == null) {
            return defaultValue;
        }
        return shortObject;
    }

    public static int getIntValue(Map map, Object key, int defaultValue) {
        Integer integerObject = MapUtils.getInteger(map, key);
        if (integerObject == null) {
            return defaultValue;
        }
        return integerObject;
    }

    public static long getLongValue(Map map, Object key, long defaultValue) {
        Long longObject = MapUtils.getLong(map, key);
        if (longObject == null) {
            return defaultValue;
        }
        return longObject;
    }

    public static float getFloatValue(Map map, Object key, float defaultValue) {
        Float floatObject = MapUtils.getFloat(map, key);
        if (floatObject == null) {
            return defaultValue;
        }
        return floatObject.floatValue();
    }

    public static double getDoubleValue(Map map, Object key, double defaultValue) {
        Double doubleObject = MapUtils.getDouble(map, key);
        if (doubleObject == null) {
            return defaultValue;
        }
        return doubleObject;
    }

    public static Properties toProperties(Map map) {
        Properties answer = new Properties();
        if (map != null) {
            Iterator iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = iter.next();
                Object key = entry.getKey();
                Object value = entry.getValue();
                answer.put(key, value);
            }
        }
        return answer;
    }

    public static Map toMap(ResourceBundle resourceBundle) {
        Enumeration<String> enumeration = resourceBundle.getKeys();
        HashMap<String, Object> map = new HashMap<String, Object>();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            Object value = resourceBundle.getObject(key);
            map.put(key, value);
        }
        return map;
    }

    public static void verbosePrint(PrintStream out, Object label, Map map) {
        MapUtils.verbosePrintInternal(out, label, map, new ArrayStack(), false);
    }

    public static void debugPrint(PrintStream out, Object label, Map map) {
        MapUtils.verbosePrintInternal(out, label, map, new ArrayStack(), true);
    }

    protected static void logInfo(Exception ex) {
        System.out.println("INFO: Exception: " + ex);
    }

    private static void verbosePrintInternal(PrintStream out, Object label, Map map, ArrayStack lineage, boolean debug) {
        MapUtils.printIndent(out, lineage.size());
        if (map == null) {
            if (label != null) {
                out.print(label);
                out.print(" = ");
            }
            out.println("null");
            return;
        }
        if (label != null) {
            out.print(label);
            out.println(" = ");
        }
        MapUtils.printIndent(out, lineage.size());
        out.println("{");
        lineage.push(map);
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            Object childKey = entry.getKey();
            Object childValue = entry.getValue();
            if (childValue instanceof Map && !lineage.contains(childValue)) {
                MapUtils.verbosePrintInternal(out, childKey == null ? "null" : childKey, (Map)childValue, lineage, debug);
                continue;
            }
            MapUtils.printIndent(out, lineage.size());
            out.print(childKey);
            out.print(" = ");
            int lineageIndex = lineage.indexOf(childValue);
            if (lineageIndex == -1) {
                out.print(childValue);
            } else if (lineage.size() - 1 == lineageIndex) {
                out.print("(this Map)");
            } else {
                out.print("(ancestor[" + (lineage.size() - 1 - lineageIndex - 1) + "] Map)");
            }
            if (debug && childValue != null) {
                out.print(' ');
                out.println(childValue.getClass().getName());
                continue;
            }
            out.println();
        }
        lineage.pop();
        MapUtils.printIndent(out, lineage.size());
        out.println(debug ? "} " + map.getClass().getName() : "}");
    }

    private static void printIndent(PrintStream out, int indent) {
        for (int i = 0; i < indent; ++i) {
            out.print(INDENT_STRING);
        }
    }

    public static Map invertMap(Map map) {
        HashMap out = new HashMap(map.size());
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            out.put(entry.getValue(), entry.getKey());
        }
        return out;
    }

    public static void safeAddToMap(Map map, Object key, Object value) throws NullPointerException {
        if (value == null) {
            map.put(key, "");
        } else {
            map.put(key, value);
        }
    }

    public static Map putAll(Map map, Object[] array) {
        map.size();
        if (array == null || array.length == 0) {
            return map;
        }
        Object obj = array[0];
        if (obj instanceof Map.Entry) {
            for (int i = 0; i < array.length; ++i) {
                Map.Entry entry = (Map.Entry)array[i];
                map.put(entry.getKey(), entry.getValue());
            }
        } else if (obj instanceof KeyValue) {
            for (int i = 0; i < array.length; ++i) {
                KeyValue keyval = (KeyValue)array[i];
                map.put(keyval.getKey(), keyval.getValue());
            }
        } else if (obj instanceof Object[]) {
            for (int i = 0; i < array.length; ++i) {
                Object[] sub = (Object[])array[i];
                if (sub == null || sub.length < 2) {
                    throw new IllegalArgumentException("Invalid array element: " + i);
                }
                map.put(sub[0], sub[1]);
            }
        } else {
            int i = 0;
            while (i < array.length - 1) {
                map.put(array[i++], array[i++]);
            }
        }
        return map;
    }

    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map map) {
        return !MapUtils.isEmpty(map);
    }

    public static Map synchronizedMap(Map map) {
        return Collections.synchronizedMap(map);
    }

    public static Map unmodifiableMap(Map map) {
        return UnmodifiableMap.decorate(map);
    }

    public static Map predicatedMap(Map map, Predicate keyPred, Predicate valuePred) {
        return PredicatedMap.decorate(map, keyPred, valuePred);
    }

    public static Map typedMap(Map map, Class keyType, Class valueType) {
        return TypedMap.decorate(map, keyType, valueType);
    }

    public static Map transformedMap(Map map, Transformer keyTransformer, Transformer valueTransformer) {
        return TransformedMap.decorate(map, keyTransformer, valueTransformer);
    }

    public static Map fixedSizeMap(Map map) {
        return FixedSizeMap.decorate(map);
    }

    public static Map lazyMap(Map map, Factory factory) {
        return LazyMap.decorate(map, factory);
    }

    public static Map lazyMap(Map map, Transformer transformerFactory) {
        return LazyMap.decorate(map, transformerFactory);
    }

    public static Map orderedMap(Map map) {
        return ListOrderedMap.decorate(map);
    }

    public static Map multiValueMap(Map map) {
        return MultiValueMap.decorate(map);
    }

    public static Map multiValueMap(Map map, Class collectionClass) {
        return MultiValueMap.decorate(map, collectionClass);
    }

    public static Map multiValueMap(Map map, Factory collectionFactory) {
        return MultiValueMap.decorate(map, collectionFactory);
    }

    public static Map synchronizedSortedMap(SortedMap map) {
        return Collections.synchronizedSortedMap(map);
    }

    public static Map unmodifiableSortedMap(SortedMap map) {
        return UnmodifiableSortedMap.decorate(map);
    }

    public static SortedMap predicatedSortedMap(SortedMap map, Predicate keyPred, Predicate valuePred) {
        return PredicatedSortedMap.decorate(map, keyPred, valuePred);
    }

    public static SortedMap typedSortedMap(SortedMap map, Class keyType, Class valueType) {
        return TypedSortedMap.decorate(map, keyType, valueType);
    }

    public static SortedMap transformedSortedMap(SortedMap map, Transformer keyTransformer, Transformer valueTransformer) {
        return TransformedSortedMap.decorate(map, keyTransformer, valueTransformer);
    }

    public static SortedMap fixedSizeSortedMap(SortedMap map) {
        return FixedSizeSortedMap.decorate(map);
    }

    public static SortedMap lazySortedMap(SortedMap map, Factory factory) {
        return LazySortedMap.decorate(map, factory);
    }

    public static SortedMap lazySortedMap(SortedMap map, Transformer transformerFactory) {
        return LazySortedMap.decorate(map, transformerFactory);
    }
}

