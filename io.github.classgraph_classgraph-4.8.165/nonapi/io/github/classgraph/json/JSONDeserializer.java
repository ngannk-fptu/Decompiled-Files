/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.json;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nonapi.io.github.classgraph.json.ClassFieldCache;
import nonapi.io.github.classgraph.json.ClassFields;
import nonapi.io.github.classgraph.json.FieldTypeInfo;
import nonapi.io.github.classgraph.json.JSONArray;
import nonapi.io.github.classgraph.json.JSONObject;
import nonapi.io.github.classgraph.json.JSONParser;
import nonapi.io.github.classgraph.json.JSONUtils;
import nonapi.io.github.classgraph.json.ParameterizedTypeImpl;
import nonapi.io.github.classgraph.json.TypeResolutions;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.types.ParseException;

public class JSONDeserializer {
    private JSONDeserializer() {
    }

    private static Object jsonBasicValueToObject(Object jsonVal, Type expectedType, boolean convertStringToNumber) {
        if (jsonVal == null) {
            return null;
        }
        if (jsonVal instanceof JSONArray || jsonVal instanceof JSONObject) {
            throw new RuntimeException("Expected a basic value type");
        }
        if (expectedType instanceof ParameterizedType) {
            if (((ParameterizedType)expectedType).getRawType().getClass() == Class.class) {
                String str;
                int idx = (str = jsonVal.toString()).indexOf(60);
                String className = str.substring(0, idx < 0 ? str.length() : idx);
                try {
                    return Class.forName(className);
                }
                catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException("Could not deserialize class reference " + jsonVal, e);
                }
            }
            throw new IllegalArgumentException("Got illegal ParameterizedType: " + expectedType);
        }
        if (!(expectedType instanceof Class)) {
            throw new IllegalArgumentException("Got illegal basic value type: " + expectedType);
        }
        Class rawType = (Class)expectedType;
        if (rawType == String.class) {
            if (!(jsonVal instanceof CharSequence)) {
                throw new IllegalArgumentException("Expected string; got " + jsonVal.getClass().getName());
            }
            return jsonVal.toString();
        }
        if (rawType == CharSequence.class) {
            if (!(jsonVal instanceof CharSequence)) {
                throw new IllegalArgumentException("Expected CharSequence; got " + jsonVal.getClass().getName());
            }
            return jsonVal;
        }
        if (rawType == Integer.class || rawType == Integer.TYPE) {
            if (convertStringToNumber && jsonVal instanceof CharSequence) {
                return Integer.parseInt(jsonVal.toString());
            }
            if (!(jsonVal instanceof Integer)) {
                throw new IllegalArgumentException("Expected integer; got " + jsonVal.getClass().getName());
            }
            return jsonVal;
        }
        if (rawType == Long.class || rawType == Long.TYPE) {
            boolean isLong = jsonVal instanceof Long;
            boolean isInteger = jsonVal instanceof Integer;
            if (convertStringToNumber && jsonVal instanceof CharSequence) {
                return isLong ? Long.parseLong(jsonVal.toString()) : (long)Integer.parseInt(jsonVal.toString());
            }
            if (!isLong && !isInteger) {
                throw new IllegalArgumentException("Expected long; got " + jsonVal.getClass().getName());
            }
            if (isLong) {
                return jsonVal;
            }
            return (long)((Integer)jsonVal).intValue();
        }
        if (rawType == Short.class || rawType == Short.TYPE) {
            if (convertStringToNumber && jsonVal instanceof CharSequence) {
                return Short.parseShort(jsonVal.toString());
            }
            if (!(jsonVal instanceof Integer)) {
                throw new IllegalArgumentException("Expected short; got " + jsonVal.getClass().getName());
            }
            int intValue = (Integer)jsonVal;
            if (intValue < Short.MIN_VALUE || intValue > Short.MAX_VALUE) {
                throw new IllegalArgumentException("Expected short; got out-of-range value " + intValue);
            }
            return (short)intValue;
        }
        if (rawType == Float.class || rawType == Float.TYPE) {
            if (convertStringToNumber && jsonVal instanceof CharSequence) {
                return Float.valueOf(Float.parseFloat(jsonVal.toString()));
            }
            if (!(jsonVal instanceof Double)) {
                throw new IllegalArgumentException("Expected float; got " + jsonVal.getClass().getName());
            }
            double doubleValue = (Double)jsonVal;
            if (doubleValue < -3.4028234663852886E38 || doubleValue > 3.4028234663852886E38) {
                throw new IllegalArgumentException("Expected float; got out-of-range value " + doubleValue);
            }
            return Float.valueOf((float)doubleValue);
        }
        if (rawType == Double.class || rawType == Double.TYPE) {
            if (convertStringToNumber && jsonVal instanceof CharSequence) {
                return Double.parseDouble(jsonVal.toString());
            }
            if (!(jsonVal instanceof Double)) {
                throw new IllegalArgumentException("Expected double; got " + jsonVal.getClass().getName());
            }
            return jsonVal;
        }
        if (rawType == Byte.class || rawType == Byte.TYPE) {
            if (convertStringToNumber && jsonVal instanceof CharSequence) {
                return Byte.parseByte(jsonVal.toString());
            }
            if (!(jsonVal instanceof Integer)) {
                throw new IllegalArgumentException("Expected byte; got " + jsonVal.getClass().getName());
            }
            int intValue = (Integer)jsonVal;
            if (intValue < -128 || intValue > 127) {
                throw new IllegalArgumentException("Expected byte; got out-of-range value " + intValue);
            }
            return (byte)intValue;
        }
        if (rawType == Character.class || rawType == Character.TYPE) {
            if (!(jsonVal instanceof CharSequence)) {
                throw new IllegalArgumentException("Expected character; got " + jsonVal.getClass().getName());
            }
            CharSequence charSequence = (CharSequence)jsonVal;
            if (charSequence.length() != 1) {
                throw new IllegalArgumentException("Expected single character; got string");
            }
            return Character.valueOf(charSequence.charAt(0));
        }
        if (rawType == Boolean.class || rawType == Boolean.TYPE) {
            if (convertStringToNumber && jsonVal instanceof CharSequence) {
                return Boolean.parseBoolean(jsonVal.toString());
            }
            if (!(jsonVal instanceof Boolean)) {
                throw new IllegalArgumentException("Expected boolean; got " + jsonVal.getClass().getName());
            }
            return jsonVal;
        }
        if (Enum.class.isAssignableFrom(rawType)) {
            if (!(jsonVal instanceof CharSequence)) {
                throw new IllegalArgumentException("Expected string for enum value; got " + jsonVal.getClass().getName());
            }
            Object enumValue = Enum.valueOf(rawType, jsonVal.toString());
            return enumValue;
        }
        if (JSONUtils.getRawType(expectedType).isAssignableFrom(jsonVal.getClass())) {
            return jsonVal;
        }
        throw new IllegalArgumentException("Got type " + jsonVal.getClass() + "; expected " + expectedType);
    }

    private static void populateObjectFromJsonObject(Object objectInstance, Type objectResolvedType, Object jsonVal, ClassFieldCache classFieldCache, Map<CharSequence, Object> idToObjectInstance, List<Runnable> collectionElementAdders) {
        Constructor<?> commonValueDefaultConstructor;
        Constructor<?> commonValueConstructorWithSizeHint;
        Class<?> commonValueRawType;
        Type commonResolvedValueType;
        boolean is1DArray;
        Class<?> arrayComponentType;
        Type mapKeyType;
        TypeResolutions typeResolutions;
        if (jsonVal == null) {
            return;
        }
        boolean isJsonObject = jsonVal instanceof JSONObject;
        boolean isJsonArray = jsonVal instanceof JSONArray;
        if (!isJsonArray && !isJsonObject) {
            throw new IllegalArgumentException("Expected JSONObject or JSONArray, got " + jsonVal.getClass().getSimpleName());
        }
        JSONObject jsonObject = isJsonObject ? (JSONObject)jsonVal : null;
        JSONArray jsonArray = isJsonArray ? (JSONArray)jsonVal : null;
        Class<?> rawType = objectInstance.getClass();
        boolean isMap = Map.class.isAssignableFrom(rawType);
        Map mapInstance = isMap ? (Map)objectInstance : null;
        boolean isCollection = Collection.class.isAssignableFrom(rawType);
        final Collection collectionInstance = isCollection ? (Collection)objectInstance : null;
        boolean isArray = rawType.isArray();
        boolean isObj = !isMap && !isCollection && !isArray;
        if ((isMap || isObj) != isJsonObject || (isCollection || isArray) != isJsonArray) {
            throw new IllegalArgumentException("Wrong JSON type for class " + objectInstance.getClass().getName());
        }
        Type objectResolvedTypeGeneric = objectResolvedType;
        if (objectResolvedType instanceof Class) {
            Class objectResolvedCls = (Class)objectResolvedType;
            if (Map.class.isAssignableFrom(objectResolvedCls)) {
                if (!isMap) {
                    throw new IllegalArgumentException("Got an unexpected map type");
                }
                objectResolvedTypeGeneric = objectResolvedCls.getGenericSuperclass();
            } else if (Collection.class.isAssignableFrom(objectResolvedCls)) {
                if (!isCollection) {
                    throw new IllegalArgumentException("Got an unexpected map type");
                }
                objectResolvedTypeGeneric = objectResolvedCls.getGenericSuperclass();
            }
        }
        if (objectResolvedTypeGeneric instanceof Class) {
            typeResolutions = null;
            mapKeyType = null;
            Class objectResolvedCls = (Class)objectResolvedTypeGeneric;
            if (isArray) {
                arrayComponentType = objectResolvedCls.getComponentType();
                is1DArray = !arrayComponentType.isArray();
            } else {
                arrayComponentType = null;
                is1DArray = false;
            }
            commonResolvedValueType = null;
        } else if (objectResolvedTypeGeneric instanceof ParameterizedType) {
            ParameterizedType parameterizedResolvedType = (ParameterizedType)objectResolvedTypeGeneric;
            typeResolutions = new TypeResolutions(parameterizedResolvedType);
            int numTypeArgs = typeResolutions.resolvedTypeArguments.length;
            if (isMap && numTypeArgs != 2) {
                throw new IllegalArgumentException("Wrong number of type arguments for Map: got " + numTypeArgs + "; expected 2");
            }
            if (isCollection && numTypeArgs != 1) {
                throw new IllegalArgumentException("Wrong number of type arguments for Collection: got " + numTypeArgs + "; expected 1");
            }
            Type type = mapKeyType = isMap ? typeResolutions.resolvedTypeArguments[0] : null;
            commonResolvedValueType = isMap ? typeResolutions.resolvedTypeArguments[1] : (isCollection ? typeResolutions.resolvedTypeArguments[0] : null);
            is1DArray = false;
            arrayComponentType = null;
        } else {
            throw new IllegalArgumentException("Got illegal type: " + objectResolvedTypeGeneric);
        }
        Class<?> clazz = commonValueRawType = commonResolvedValueType == null ? null : JSONUtils.getRawType(commonResolvedValueType);
        if (isMap || isCollection || is1DArray && !JSONUtils.isBasicValueType(arrayComponentType)) {
            commonValueConstructorWithSizeHint = classFieldCache.getConstructorWithSizeHintForConcreteTypeOf(is1DArray ? arrayComponentType : commonValueRawType);
            commonValueDefaultConstructor = commonValueConstructorWithSizeHint != null ? null : classFieldCache.getDefaultConstructorForConcreteTypeOf(is1DArray ? arrayComponentType : commonValueRawType);
        } else {
            commonValueConstructorWithSizeHint = null;
            commonValueDefaultConstructor = null;
        }
        ClassFields classFields = isObj ? classFieldCache.get(rawType) : null;
        ArrayList<ObjectInstantiation> itemsToRecurseToInPass2 = null;
        int numItems = jsonObject != null ? jsonObject.items.size() : (jsonArray != null ? jsonArray.items.size() : 0);
        for (int i = 0; i < numItems; ++i) {
            Object instantiatedItemObject;
            Type resolvedItemValueType;
            FieldTypeInfo fieldTypeInfo;
            JSONArray itemJsonValueJsonArray;
            Object itemJsonValue;
            String itemJsonKey;
            if (jsonObject != null) {
                Map.Entry<String, Object> jsonObjectItem = jsonObject.items.get(i);
                itemJsonKey = jsonObjectItem.getKey();
                itemJsonValue = jsonObjectItem.getValue();
            } else if (jsonArray != null) {
                itemJsonKey = null;
                itemJsonValue = jsonArray.items.get(i);
            } else {
                throw new RuntimeException("This exception should not be thrown");
            }
            boolean itemJsonValueIsJsonObject = itemJsonValue instanceof JSONObject;
            boolean itemJsonValueIsJsonArray = itemJsonValue instanceof JSONArray;
            JSONObject itemJsonValueJsonObject = itemJsonValueIsJsonObject ? (JSONObject)itemJsonValue : null;
            JSONArray jSONArray = itemJsonValueJsonArray = itemJsonValueIsJsonArray ? (JSONArray)itemJsonValue : null;
            if (classFields != null) {
                fieldTypeInfo = classFields.fieldNameToFieldTypeInfo.get(itemJsonKey);
                if (fieldTypeInfo == null) {
                    throw new IllegalArgumentException("Field " + rawType.getName() + "." + itemJsonKey + " does not exist or is not accessible, non-final, and non-transient");
                }
            } else {
                fieldTypeInfo = null;
            }
            Type type = fieldTypeInfo != null ? fieldTypeInfo.getFullyResolvedFieldType(typeResolutions) : (resolvedItemValueType = isArray ? arrayComponentType : commonResolvedValueType);
            if (itemJsonValue == null) {
                instantiatedItemObject = null;
            } else if (resolvedItemValueType == Object.class) {
                if (itemJsonValueIsJsonObject) {
                    instantiatedItemObject = new HashMap();
                    if (itemsToRecurseToInPass2 == null) {
                        itemsToRecurseToInPass2 = new ArrayList<ObjectInstantiation>();
                    }
                    itemsToRecurseToInPass2.add(new ObjectInstantiation(instantiatedItemObject, ParameterizedTypeImpl.MAP_OF_UNKNOWN_TYPE, itemJsonValue));
                } else if (itemJsonValueIsJsonArray) {
                    instantiatedItemObject = new ArrayList();
                    if (itemsToRecurseToInPass2 == null) {
                        itemsToRecurseToInPass2 = new ArrayList();
                    }
                    itemsToRecurseToInPass2.add(new ObjectInstantiation(instantiatedItemObject, ParameterizedTypeImpl.LIST_OF_UNKNOWN_TYPE, itemJsonValue));
                } else {
                    instantiatedItemObject = JSONDeserializer.jsonBasicValueToObject(itemJsonValue, resolvedItemValueType, false);
                }
            } else if (JSONUtils.isBasicValueType(resolvedItemValueType)) {
                if (itemJsonValueIsJsonObject || itemJsonValueIsJsonArray) {
                    throw new IllegalArgumentException("Got JSONObject or JSONArray type when expecting a simple value type");
                }
                instantiatedItemObject = JSONDeserializer.jsonBasicValueToObject(itemJsonValue, resolvedItemValueType, false);
            } else if (CharSequence.class.isAssignableFrom(itemJsonValue.getClass())) {
                Object linkedObject = idToObjectInstance.get(itemJsonValue);
                if (linkedObject == null) {
                    throw new IllegalArgumentException("Object id not found: " + itemJsonValue);
                }
                instantiatedItemObject = linkedObject;
            } else {
                block59: {
                    if (!itemJsonValueIsJsonObject && !itemJsonValueIsJsonArray) {
                        throw new IllegalArgumentException("Got simple value type when expecting a JSON object or JSON array");
                    }
                    try {
                        int numSubItems;
                        int n = itemJsonValueJsonObject != null ? itemJsonValueJsonObject.items.size() : (numSubItems = itemJsonValueJsonArray != null ? itemJsonValueJsonArray.items.size() : 0);
                        if (resolvedItemValueType instanceof Class && ((Class)resolvedItemValueType).isArray()) {
                            if (!itemJsonValueIsJsonArray) {
                                throw new IllegalArgumentException("Expected JSONArray, got " + itemJsonValue.getClass().getName());
                            }
                            instantiatedItemObject = Array.newInstance(((Class)resolvedItemValueType).getComponentType(), numSubItems);
                            break block59;
                        }
                        if (isCollection || isMap || is1DArray) {
                            instantiatedItemObject = commonValueConstructorWithSizeHint != null ? commonValueConstructorWithSizeHint.newInstance(numSubItems) : (commonValueDefaultConstructor != null ? commonValueDefaultConstructor.newInstance(new Object[0]) : null);
                            break block59;
                        }
                        if (fieldTypeInfo != null) {
                            Constructor<?> valueConstructorWithSizeHint = fieldTypeInfo.getConstructorForFieldTypeWithSizeHint(resolvedItemValueType, classFieldCache);
                            instantiatedItemObject = valueConstructorWithSizeHint != null ? valueConstructorWithSizeHint.newInstance(numSubItems) : fieldTypeInfo.getDefaultConstructorForFieldType(resolvedItemValueType, classFieldCache).newInstance(new Object[0]);
                            break block59;
                        }
                        if (isArray && !is1DArray) {
                            instantiatedItemObject = Array.newInstance(rawType.getComponentType(), numSubItems);
                            break block59;
                        }
                        throw new IllegalArgumentException("Got illegal type");
                    }
                    catch (ReflectiveOperationException | SecurityException e) {
                        throw new IllegalArgumentException("Could not instantiate type " + resolvedItemValueType, e);
                    }
                }
                if (itemJsonValue instanceof JSONObject) {
                    JSONObject itemJsonObject = (JSONObject)itemJsonValue;
                    if (itemJsonObject.objectId != null) {
                        idToObjectInstance.put(itemJsonObject.objectId, instantiatedItemObject);
                    }
                }
                if (itemsToRecurseToInPass2 == null) {
                    itemsToRecurseToInPass2 = new ArrayList();
                }
                itemsToRecurseToInPass2.add(new ObjectInstantiation(instantiatedItemObject, resolvedItemValueType, itemJsonValue));
            }
            if (fieldTypeInfo != null) {
                fieldTypeInfo.setFieldValue(objectInstance, instantiatedItemObject);
                continue;
            }
            if (mapInstance != null) {
                Object mapKey = JSONDeserializer.jsonBasicValueToObject(itemJsonKey, mapKeyType, true);
                mapInstance.put(mapKey, instantiatedItemObject);
                continue;
            }
            if (isArray) {
                Array.set(objectInstance, i, instantiatedItemObject);
                continue;
            }
            if (collectionInstance == null) continue;
            collectionElementAdders.add(new Runnable(){

                @Override
                public void run() {
                    collectionInstance.add(instantiatedItemObject);
                }
            });
        }
        if (itemsToRecurseToInPass2 != null) {
            for (ObjectInstantiation i : itemsToRecurseToInPass2) {
                JSONDeserializer.populateObjectFromJsonObject(i.objectInstance, i.type, i.jsonVal, classFieldCache, idToObjectInstance, collectionElementAdders);
            }
        }
    }

    private static Map<CharSequence, Object> getInitialIdToObjectMap(Object objectInstance, Object parsedJSON) {
        HashMap<CharSequence, Object> idToObjectInstance = new HashMap<CharSequence, Object>();
        if (parsedJSON instanceof JSONObject) {
            Object firstItemValue;
            Map.Entry<String, Object> firstItem;
            JSONObject itemJsonObject = (JSONObject)parsedJSON;
            if (!(itemJsonObject.items.isEmpty() || !(firstItem = itemJsonObject.items.get(0)).getKey().equals("__ID") || (firstItemValue = firstItem.getValue()) != null && CharSequence.class.isAssignableFrom(firstItemValue.getClass()))) {
                idToObjectInstance.put((CharSequence)firstItemValue, objectInstance);
            }
        }
        return idToObjectInstance;
    }

    private static <T> T deserializeObject(Class<T> expectedType, String json, ClassFieldCache classFieldCache) throws IllegalArgumentException {
        Object objectInstance;
        Object parsedJSON;
        try {
            parsedJSON = JSONParser.parseJSON(json);
        }
        catch (ParseException e) {
            throw new IllegalArgumentException("Could not parse JSON", e);
        }
        try {
            Object newInstance;
            Constructor<?> constructor = classFieldCache.getDefaultConstructorForConcreteTypeOf(expectedType);
            objectInstance = newInstance = constructor.newInstance(new Object[0]);
        }
        catch (ReflectiveOperationException | SecurityException e) {
            throw new IllegalArgumentException("Could not construct object of type " + expectedType.getName(), e);
        }
        ArrayList<Runnable> collectionElementAdders = new ArrayList<Runnable>();
        JSONDeserializer.populateObjectFromJsonObject(objectInstance, expectedType, parsedJSON, classFieldCache, JSONDeserializer.getInitialIdToObjectMap(objectInstance, parsedJSON), collectionElementAdders);
        for (Runnable runnable : collectionElementAdders) {
            runnable.run();
        }
        return (T)objectInstance;
    }

    public static <T> T deserializeObject(Class<T> expectedType, String json, ReflectionUtils reflectionUtils) throws IllegalArgumentException {
        ClassFieldCache classFieldCache = new ClassFieldCache(true, false, reflectionUtils);
        return JSONDeserializer.deserializeObject(expectedType, json, classFieldCache);
    }

    public static <T> T deserializeObject(Class<T> expectedType, String json) throws IllegalArgumentException {
        return JSONDeserializer.deserializeObject(expectedType, json, new ReflectionUtils());
    }

    public static void deserializeToField(Object containingObject, String fieldName, String json, ClassFieldCache classFieldCache) throws IllegalArgumentException {
        Object parsedJSON;
        if (containingObject == null) {
            throw new IllegalArgumentException("Cannot deserialize to a field of a null object");
        }
        try {
            parsedJSON = JSONParser.parseJSON(json);
        }
        catch (ParseException e) {
            throw new IllegalArgumentException("Could not parse JSON", e);
        }
        JSONObject wrapperJsonObj = new JSONObject(1);
        wrapperJsonObj.items.add(new AbstractMap.SimpleEntry<String, Object>(fieldName, parsedJSON));
        ArrayList<Runnable> collectionElementAdders = new ArrayList<Runnable>();
        JSONDeserializer.populateObjectFromJsonObject(containingObject, containingObject.getClass(), wrapperJsonObj, classFieldCache, new HashMap<CharSequence, Object>(), collectionElementAdders);
        for (Runnable runnable : collectionElementAdders) {
            runnable.run();
        }
    }

    public static void deserializeToField(Object containingObject, String fieldName, String json, ReflectionUtils reflectionUtils) throws IllegalArgumentException {
        ClassFieldCache typeCache = new ClassFieldCache(true, false, reflectionUtils);
        JSONDeserializer.deserializeToField(containingObject, fieldName, json, typeCache);
    }

    public static void deserializeToField(Object containingObject, String fieldName, String json) throws IllegalArgumentException {
        JSONDeserializer.deserializeToField(containingObject, fieldName, json, new ReflectionUtils());
    }

    private static class ObjectInstantiation {
        Object jsonVal;
        Object objectInstance;
        Type type;

        public ObjectInstantiation(Object objectInstance, Type type, Object jsonVal) {
            this.jsonVal = jsonVal;
            this.objectInstance = objectInstance;
            this.type = type;
        }
    }
}

