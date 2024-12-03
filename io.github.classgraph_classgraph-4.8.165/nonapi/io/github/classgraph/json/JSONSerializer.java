/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import nonapi.io.github.classgraph.json.ClassFieldCache;
import nonapi.io.github.classgraph.json.ClassFields;
import nonapi.io.github.classgraph.json.FieldTypeInfo;
import nonapi.io.github.classgraph.json.JSONArray;
import nonapi.io.github.classgraph.json.JSONObject;
import nonapi.io.github.classgraph.json.JSONReference;
import nonapi.io.github.classgraph.json.JSONUtils;
import nonapi.io.github.classgraph.json.ReferenceEqualityKey;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.utils.CollectionUtils;

public final class JSONSerializer {
    private static final Comparator<Object> SET_COMPARATOR = new Comparator<Object>(){

        @Override
        public int compare(Object o1, Object o2) {
            if (o1 == null || o2 == null) {
                return (o1 == null ? 0 : 1) - (o2 == null ? 0 : 1);
            }
            if (Comparable.class.isAssignableFrom(o1.getClass()) && Comparable.class.isAssignableFrom(o2.getClass())) {
                Comparable comparableO1 = (Comparable)o1;
                return comparableO1.compareTo(o2);
            }
            return o1.toString().compareTo(o2.toString());
        }
    };

    private JSONSerializer() {
    }

    private static void assignObjectIds(Object jsonVal, Map<ReferenceEqualityKey<Object>, JSONObject> objToJSONVal, ClassFieldCache classFieldCache, Map<ReferenceEqualityKey<JSONReference>, CharSequence> jsonReferenceToId, AtomicInteger objId, boolean onlySerializePublicFields) {
        if (jsonVal instanceof JSONObject) {
            for (Map.Entry<String, Object> item : ((JSONObject)jsonVal).items) {
                JSONSerializer.assignObjectIds(item.getValue(), objToJSONVal, classFieldCache, jsonReferenceToId, objId, onlySerializePublicFields);
            }
        } else if (jsonVal instanceof JSONArray) {
            for (Object item : ((JSONArray)jsonVal).items) {
                JSONSerializer.assignObjectIds(item, objToJSONVal, classFieldCache, jsonReferenceToId, objId, onlySerializePublicFields);
            }
        } else if (jsonVal instanceof JSONReference) {
            Object refdObj = ((JSONReference)jsonVal).idObject;
            if (refdObj == null) {
                throw new RuntimeException("Internal inconsistency");
            }
            ReferenceEqualityKey<Object> refdObjKey = new ReferenceEqualityKey<Object>(refdObj);
            JSONObject refdJsonVal = objToJSONVal.get(refdObjKey);
            if (refdJsonVal == null) {
                throw new RuntimeException("Internal inconsistency");
            }
            Field annotatedField = classFieldCache.get(refdObj.getClass()).idField;
            CharSequence idStr = null;
            if (annotatedField != null) {
                try {
                    Object idObject = annotatedField.get(refdObj);
                    if (idObject != null) {
                        refdJsonVal.objectId = idStr = idObject.toString();
                    }
                }
                catch (IllegalAccessException | IllegalArgumentException e) {
                    throw new IllegalArgumentException("Could not access @Id-annotated field " + annotatedField, e);
                }
            }
            if (idStr == null) {
                if (refdJsonVal.objectId == null) {
                    refdJsonVal.objectId = idStr = "[#" + objId.getAndIncrement() + "]";
                } else {
                    idStr = refdJsonVal.objectId;
                }
            }
            jsonReferenceToId.put(new ReferenceEqualityKey<JSONReference>((JSONReference)jsonVal), idStr);
        }
    }

    private static void convertVals(Object[] convertedVals, Set<ReferenceEqualityKey<Object>> visitedOnPath, Set<ReferenceEqualityKey<Object>> standardObjectVisited, ClassFieldCache classFieldCache, Map<ReferenceEqualityKey<Object>, JSONObject> objToJSONVal, boolean onlySerializePublicFields) {
        ReferenceEqualityKey valKey;
        Object val;
        int i;
        ReferenceEqualityKey[] valKeys = new ReferenceEqualityKey[convertedVals.length];
        boolean[] needToConvert = new boolean[convertedVals.length];
        for (i = 0; i < convertedVals.length; ++i) {
            val = convertedVals[i];
            boolean bl = needToConvert[i] = !JSONUtils.isBasicValueType(val);
            if (needToConvert[i] && !JSONUtils.isCollectionOrArray(val)) {
                boolean alreadyVisited;
                valKeys[i] = valKey = new ReferenceEqualityKey(val);
                boolean bl2 = alreadyVisited = !standardObjectVisited.add(valKey);
                if (alreadyVisited) {
                    convertedVals[i] = new JSONReference(val);
                    needToConvert[i] = false;
                }
            }
            if (!(val instanceof Class)) continue;
            convertedVals[i] = ((Class)val).getName();
        }
        for (i = 0; i < convertedVals.length; ++i) {
            if (!needToConvert[i]) continue;
            val = convertedVals[i];
            convertedVals[i] = JSONSerializer.toJSONGraph(val, visitedOnPath, standardObjectVisited, classFieldCache, objToJSONVal, onlySerializePublicFields);
            if (JSONUtils.isCollectionOrArray(val)) continue;
            valKey = valKeys[i];
            objToJSONVal.put(valKey, (JSONObject)convertedVals[i]);
        }
    }

    private static Object toJSONGraph(Object obj, Set<ReferenceEqualityKey<Object>> visitedOnPath, Set<ReferenceEqualityKey<Object>> standardObjectVisited, ClassFieldCache classFieldCache, Map<ReferenceEqualityKey<Object>, JSONObject> objToJSONVal, boolean onlySerializePublicFields) {
        Object jsonVal;
        if (obj instanceof Class) {
            return ((Class)obj).getName();
        }
        if (JSONUtils.isBasicValueType(obj)) {
            return obj;
        }
        ReferenceEqualityKey<Object> objKey = new ReferenceEqualityKey<Object>(obj);
        if (!visitedOnPath.add(objKey)) {
            if (JSONUtils.isCollectionOrArray(obj)) {
                throw new IllegalArgumentException("Cycles involving collections cannot be serialized, since collections are not assigned object ids. Reached cycle at: " + obj);
            }
            return new JSONReference(obj);
        }
        Class<?> cls = obj.getClass();
        boolean isArray = cls.isArray();
        if (Map.class.isAssignableFrom(cls)) {
            Map map = (Map)obj;
            ArrayList keys = new ArrayList(map.keySet());
            int n = keys.size();
            boolean keysComparable = false;
            Object firstNonNullKey = null;
            for (int i = 0; i < n && firstNonNullKey == null; ++i) {
                firstNonNullKey = keys.get(i);
            }
            if (firstNonNullKey != null && Comparable.class.isAssignableFrom(firstNonNullKey.getClass())) {
                CollectionUtils.sortIfNotEmpty(keys);
                keysComparable = true;
            }
            Object[] convertedKeys = new String[n];
            for (int i = 0; i < n; ++i) {
                Object key = keys.get(i);
                if (key != null && !JSONUtils.isBasicValueType(key)) {
                    throw new IllegalArgumentException("Map key of type " + key.getClass().getName() + " is not a basic type (String, Integer, etc.), so can't be easily serialized as a JSON associative array key");
                }
                convertedKeys[i] = JSONUtils.escapeJSONString(key == null ? "null" : key.toString());
            }
            if (!keysComparable) {
                Arrays.sort(convertedKeys);
            }
            Object[] convertedVals = new Object[n];
            for (int i = 0; i < n; ++i) {
                convertedVals[i] = map.get(keys.get(i));
            }
            JSONSerializer.convertVals(convertedVals, visitedOnPath, standardObjectVisited, classFieldCache, objToJSONVal, onlySerializePublicFields);
            ArrayList<Map.Entry<String, Object>> convertedKeyValPairs = new ArrayList<Map.Entry<String, Object>>(n);
            for (int i = 0; i < n; ++i) {
                convertedKeyValPairs.add(new AbstractMap.SimpleEntry<Object, Object>(convertedKeys[i], convertedVals[i]));
            }
            jsonVal = new JSONObject(convertedKeyValPairs);
        } else if (isArray || List.class.isAssignableFrom(cls)) {
            List list;
            boolean isList = List.class.isAssignableFrom(cls);
            List list2 = list = isList ? (List)obj : null;
            int n = list != null ? list.size() : (isArray ? Array.getLength(obj) : 0);
            Object[] convertedVals = new Object[n];
            for (int i = 0; i < n; ++i) {
                convertedVals[i] = list != null ? list.get(i) : (isArray ? Array.get(obj, i) : Integer.valueOf(0));
            }
            JSONSerializer.convertVals(convertedVals, visitedOnPath, standardObjectVisited, classFieldCache, objToJSONVal, onlySerializePublicFields);
            jsonVal = new JSONArray(Arrays.asList(convertedVals));
        } else if (Collection.class.isAssignableFrom(cls)) {
            Collection collection = (Collection)obj;
            ArrayList convertedValsList = new ArrayList(collection);
            if (Set.class.isAssignableFrom(cls)) {
                CollectionUtils.sortIfNotEmpty(convertedValsList, SET_COMPARATOR);
            }
            Object[] convertedVals = convertedValsList.toArray();
            JSONSerializer.convertVals(convertedVals, visitedOnPath, standardObjectVisited, classFieldCache, objToJSONVal, onlySerializePublicFields);
            jsonVal = new JSONArray(Arrays.asList(convertedVals));
        } else {
            ClassFields resolvedFields = classFieldCache.get(cls);
            List<FieldTypeInfo> fieldOrder = resolvedFields.fieldOrder;
            int n = fieldOrder.size();
            String[] fieldNames = new String[n];
            Object[] convertedVals = new Object[n];
            for (int i = 0; i < n; ++i) {
                FieldTypeInfo fieldTypeInfo = fieldOrder.get(i);
                Field field = fieldTypeInfo.field;
                fieldNames[i] = field.getName();
                try {
                    convertedVals[i] = JSONUtils.getFieldValue(obj, field);
                    continue;
                }
                catch (IllegalAccessException | IllegalArgumentException e) {
                    throw new RuntimeException("Could not get value of field \"" + fieldNames[i] + "\" in object of class " + obj.getClass().getName(), e);
                }
            }
            JSONSerializer.convertVals(convertedVals, visitedOnPath, standardObjectVisited, classFieldCache, objToJSONVal, onlySerializePublicFields);
            ArrayList<Map.Entry<String, Object>> convertedKeyValPairs = new ArrayList<Map.Entry<String, Object>>(n);
            for (int i = 0; i < n; ++i) {
                convertedKeyValPairs.add(new AbstractMap.SimpleEntry<String, Object>(fieldNames[i], convertedVals[i]));
            }
            jsonVal = new JSONObject(convertedKeyValPairs);
        }
        visitedOnPath.remove(objKey);
        return jsonVal;
    }

    static void jsonValToJSONString(Object jsonVal, Map<ReferenceEqualityKey<JSONReference>, CharSequence> jsonReferenceToId, boolean includeNullValuedFields, int depth, int indentWidth, StringBuilder buf) {
        if (jsonVal == null) {
            buf.append("null");
        } else if (jsonVal instanceof JSONObject) {
            ((JSONObject)jsonVal).toJSONString(jsonReferenceToId, includeNullValuedFields, depth, indentWidth, buf);
        } else if (jsonVal instanceof JSONArray) {
            ((JSONArray)jsonVal).toJSONString(jsonReferenceToId, includeNullValuedFields, depth, indentWidth, buf);
        } else if (jsonVal instanceof JSONReference) {
            CharSequence referencedObjectId = jsonReferenceToId.get(new ReferenceEqualityKey<JSONReference>((JSONReference)jsonVal));
            JSONSerializer.jsonValToJSONString(referencedObjectId, jsonReferenceToId, includeNullValuedFields, depth, indentWidth, buf);
        } else if (jsonVal instanceof CharSequence || jsonVal instanceof Character || jsonVal.getClass().isEnum()) {
            buf.append('\"');
            JSONUtils.escapeJSONString(jsonVal.toString(), buf);
            buf.append('\"');
        } else {
            buf.append(jsonVal);
        }
    }

    public static String serializeObject(Object obj, int indentWidth, boolean onlySerializePublicFields, ClassFieldCache classFieldCache) {
        HashMap<ReferenceEqualityKey<Object>, JSONObject> objToJSONVal = new HashMap<ReferenceEqualityKey<Object>, JSONObject>();
        Object rootJsonVal = JSONSerializer.toJSONGraph(obj, new HashSet<ReferenceEqualityKey<Object>>(), new HashSet<ReferenceEqualityKey<Object>>(), classFieldCache, objToJSONVal, onlySerializePublicFields);
        HashMap<ReferenceEqualityKey<JSONReference>, CharSequence> jsonReferenceToId = new HashMap<ReferenceEqualityKey<JSONReference>, CharSequence>();
        AtomicInteger objId = new AtomicInteger(0);
        JSONSerializer.assignObjectIds(rootJsonVal, objToJSONVal, classFieldCache, jsonReferenceToId, objId, onlySerializePublicFields);
        StringBuilder buf = new StringBuilder(32768);
        JSONSerializer.jsonValToJSONString(rootJsonVal, jsonReferenceToId, false, 0, indentWidth, buf);
        return buf.toString();
    }

    public static String serializeObject(Object obj, int indentWidth, boolean onlySerializePublicFields, ReflectionUtils reflectionUtils) {
        return JSONSerializer.serializeObject(obj, indentWidth, onlySerializePublicFields, new ClassFieldCache(false, false, reflectionUtils));
    }

    public static String serializeObject(Object obj, int indentWidth, boolean onlySerializePublicFields) {
        return JSONSerializer.serializeObject(obj, indentWidth, onlySerializePublicFields, new ReflectionUtils());
    }

    public static String serializeObject(Object obj) {
        return JSONSerializer.serializeObject(obj, 0, false);
    }

    public static String serializeFromField(Object containingObject, String fieldName, int indentWidth, boolean onlySerializePublicFields, ClassFieldCache classFieldCache) {
        Object fieldValue;
        FieldTypeInfo fieldResolvedTypeInfo = classFieldCache.get(containingObject.getClass()).fieldNameToFieldTypeInfo.get(fieldName);
        if (fieldResolvedTypeInfo == null) {
            throw new IllegalArgumentException("Class " + containingObject.getClass().getName() + " does not have a field named \"" + fieldName + "\"");
        }
        Field field = fieldResolvedTypeInfo.field;
        if (!JSONUtils.fieldIsSerializable(field, false, classFieldCache.reflectionUtils)) {
            throw new IllegalArgumentException("Field " + containingObject.getClass().getName() + "." + fieldName + " needs to be accessible, non-transient, and non-final");
        }
        try {
            fieldValue = JSONUtils.getFieldValue(containingObject, field);
        }
        catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Could get value of field " + fieldName, e);
        }
        return JSONSerializer.serializeObject(fieldValue, indentWidth, onlySerializePublicFields, classFieldCache);
    }

    public static String serializeFromField(Object containingObject, String fieldName, int indentWidth, boolean onlySerializePublicFields, ReflectionUtils reflectionUtils) {
        ClassFieldCache classFieldCache = new ClassFieldCache(false, onlySerializePublicFields, reflectionUtils);
        return JSONSerializer.serializeFromField(containingObject, fieldName, indentWidth, onlySerializePublicFields, classFieldCache);
    }

    public static String serializeFromField(Object containingObject, String fieldName, int indentWidth, boolean onlySerializePublicFields) {
        return JSONSerializer.serializeFromField(containingObject, fieldName, indentWidth, onlySerializePublicFields, new ReflectionUtils());
    }
}

