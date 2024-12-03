/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.util.reflection;

import java.util.HashMap;
import java.util.Map;

public class ReflectionContextState {
    private static final String GETTING_BY_KEY_PROPERTY = "xwork.getting.by.key.property";
    private static final String SET_MAP_KEY = "set.map.key";
    public static final String CURRENT_PROPERTY_PATH = "current.property.path";
    public static final String FULL_PROPERTY_PATH = "current.property.path";
    public static final String CREATE_NULL_OBJECTS = "xwork.NullHandler.createNullObjects";
    public static final String DENY_METHOD_EXECUTION = "xwork.MethodAccessor.denyMethodExecution";
    public static final String DENY_INDEXED_ACCESS_EXECUTION = "xwork.IndexedPropertyAccessor.denyMethodExecution";

    public static boolean isCreatingNullObjects(Map<String, Object> context) {
        return ReflectionContextState.getBooleanProperty(CREATE_NULL_OBJECTS, context);
    }

    public static void setCreatingNullObjects(Map<String, Object> context, boolean creatingNullObjects) {
        ReflectionContextState.setBooleanValue(CREATE_NULL_OBJECTS, context, creatingNullObjects);
    }

    public static boolean isGettingByKeyProperty(Map<String, Object> context) {
        return ReflectionContextState.getBooleanProperty(GETTING_BY_KEY_PROPERTY, context);
    }

    public static void setDenyMethodExecution(Map<String, Object> context, boolean denyMethodExecution) {
        ReflectionContextState.setBooleanValue(DENY_METHOD_EXECUTION, context, denyMethodExecution);
    }

    public static boolean isDenyMethodExecution(Map<String, Object> context) {
        return ReflectionContextState.getBooleanProperty(DENY_METHOD_EXECUTION, context);
    }

    public static void setGettingByKeyProperty(Map<String, Object> context, boolean gettingByKeyProperty) {
        ReflectionContextState.setBooleanValue(GETTING_BY_KEY_PROPERTY, context, gettingByKeyProperty);
    }

    public static boolean isReportingConversionErrors(Map<String, Object> context) {
        return ReflectionContextState.getBooleanProperty("report.conversion.errors", context);
    }

    public static void setReportingConversionErrors(Map<String, Object> context, boolean reportingErrors) {
        ReflectionContextState.setBooleanValue("report.conversion.errors", context, reportingErrors);
    }

    public static Class getLastBeanClassAccessed(Map<String, Object> context) {
        return (Class)context.get("last.bean.accessed");
    }

    public static void setLastBeanPropertyAccessed(Map<String, Object> context, String property) {
        context.put("last.property.accessed", property);
    }

    public static String getLastBeanPropertyAccessed(Map<String, Object> context) {
        return (String)context.get("last.property.accessed");
    }

    public static void setLastBeanClassAccessed(Map<String, Object> context, Class clazz) {
        context.put("last.bean.accessed", clazz);
    }

    public static String getCurrentPropertyPath(Map<String, Object> context) {
        return (String)context.get("current.property.path");
    }

    public static String getFullPropertyPath(Map<String, Object> context) {
        return (String)context.get("current.property.path");
    }

    public static void setFullPropertyPath(Map<String, Object> context, String path) {
        context.put("current.property.path", path);
    }

    public static void updateCurrentPropertyPath(Map<String, Object> context, Object name) {
        String currentPath = ReflectionContextState.getCurrentPropertyPath(context);
        if (name != null) {
            if (currentPath != null) {
                StringBuilder sb = new StringBuilder(currentPath);
                sb.append(".");
                sb.append(name.toString());
                currentPath = sb.toString();
            } else {
                currentPath = name.toString();
            }
            context.put("current.property.path", currentPath);
        }
    }

    public static void setSetMap(Map<String, Object> context, Map<Object, Object> setMap, String path) {
        HashMap<String, Map<Object, Object>> mapOfSetMaps = (HashMap<String, Map<Object, Object>>)context.get(SET_MAP_KEY);
        if (mapOfSetMaps == null) {
            mapOfSetMaps = new HashMap<String, Map<Object, Object>>();
            context.put(SET_MAP_KEY, mapOfSetMaps);
        }
        mapOfSetMaps.put(path, setMap);
    }

    public static Map<Object, Object> getSetMap(Map<String, Object> context, String path) {
        Map mapOfSetMaps = (Map)context.get(SET_MAP_KEY);
        if (mapOfSetMaps == null) {
            return null;
        }
        return (Map)mapOfSetMaps.get(path);
    }

    private static boolean getBooleanProperty(String property, Map<String, Object> context) {
        Boolean myBool = (Boolean)context.get(property);
        return myBool == null ? false : myBool;
    }

    private static void setBooleanValue(String property, Map<String, Object> context, boolean value) {
        context.put(property, new Boolean(value));
    }

    public static void clearCurrentPropertyPath(Map<String, Object> context) {
        context.put("current.property.path", null);
    }

    public static void clear(Map<String, Object> context) {
        if (context != null) {
            context.put("last.bean.accessed", null);
            context.put("last.property.accessed", null);
            context.put("current.property.path", null);
            context.put("current.property.path", null);
        }
    }
}

