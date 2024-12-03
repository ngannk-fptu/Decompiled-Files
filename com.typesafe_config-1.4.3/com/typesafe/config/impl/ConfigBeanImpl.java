/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigMemorySize;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;
import com.typesafe.config.Optional;
import com.typesafe.config.impl.AbstractConfigValue;
import com.typesafe.config.impl.ConfigImplUtil;
import com.typesafe.config.impl.Path;
import com.typesafe.config.impl.ResolveStatus;
import com.typesafe.config.impl.SimpleConfig;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigBeanImpl {
    public static <T> T createInternal(Config config, Class<T> clazz) {
        if (((SimpleConfig)config).root().resolveStatus() != ResolveStatus.RESOLVED) {
            throw new ConfigException.NotResolved("need to Config#resolve() a config before using it to initialize a bean, see the API docs for Config#resolve()");
        }
        HashMap<String, AbstractConfigValue> configProps = new HashMap<String, AbstractConfigValue>();
        HashMap<String, PropertyDescriptor[]> originalNames = new HashMap<String, PropertyDescriptor[]>();
        for (Map.Entry configProp : config.root().entrySet()) {
            PropertyDescriptor[] originalName = (PropertyDescriptor[])configProp.getKey();
            String camelName = ConfigImplUtil.toCamelCase((String)originalName);
            if (originalNames.containsKey(camelName) && !originalName.equals(camelName)) continue;
            configProps.put(camelName, (AbstractConfigValue)configProp.getValue());
            originalNames.put(camelName, originalName);
        }
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(clazz);
        }
        catch (IntrospectionException e) {
            throw new ConfigException.BadBean("Could not get bean information for class " + clazz.getName(), e);
        }
        try {
            ArrayList<PropertyDescriptor> beanProps = new ArrayList<PropertyDescriptor>();
            for (PropertyDescriptor beanProp : beanInfo.getPropertyDescriptors()) {
                if (beanProp.getReadMethod() == null || beanProp.getWriteMethod() == null) continue;
                beanProps.add(beanProp);
            }
            ArrayList<ConfigException.ValidationProblem> problems = new ArrayList<ConfigException.ValidationProblem>();
            for (PropertyDescriptor beanProp : beanProps) {
                Method setter = beanProp.getWriteMethod();
                Class<?> parameterClass = setter.getParameterTypes()[0];
                ConfigValueType expectedType = ConfigBeanImpl.getValueTypeOrNull(parameterClass);
                if (expectedType == null) continue;
                String name = (String)originalNames.get(beanProp.getName());
                if (name == null) {
                    name = beanProp.getName();
                }
                Path path = Path.newKey(name);
                AbstractConfigValue configValue = (AbstractConfigValue)configProps.get(beanProp.getName());
                if (configValue != null) {
                    SimpleConfig.checkValid(path, expectedType, configValue, problems);
                    continue;
                }
                if (ConfigBeanImpl.isOptionalProperty(clazz, beanProp)) continue;
                SimpleConfig.addMissing(problems, expectedType, path, config.origin());
            }
            if (!problems.isEmpty()) {
                throw new ConfigException.ValidationFailed(problems);
            }
            T bean = clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            for (PropertyDescriptor beanProp : beanProps) {
                Method setter = beanProp.getWriteMethod();
                Type parameterType = setter.getGenericParameterTypes()[0];
                Class<?> parameterClass = setter.getParameterTypes()[0];
                String configPropName = (String)originalNames.get(beanProp.getName());
                if (configPropName == null) {
                    if (ConfigBeanImpl.isOptionalProperty(clazz, beanProp)) continue;
                    throw new ConfigException.Missing(beanProp.getName());
                }
                Object unwrapped = ConfigBeanImpl.getValue(clazz, parameterType, parameterClass, config, configPropName);
                setter.invoke(bean, unwrapped);
            }
            return bean;
        }
        catch (NoSuchMethodException e) {
            throw new ConfigException.BadBean(clazz.getName() + " needs a public no-args constructor to be used as a bean", e);
        }
        catch (InstantiationException e) {
            throw new ConfigException.BadBean(clazz.getName() + " needs to be instantiable to be used as a bean", e);
        }
        catch (IllegalAccessException e) {
            throw new ConfigException.BadBean(clazz.getName() + " getters and setters are not accessible, they must be for use as a bean", e);
        }
        catch (InvocationTargetException e) {
            throw new ConfigException.BadBean("Calling bean method on " + clazz.getName() + " caused an exception", e);
        }
    }

    private static Object getValue(Class<?> beanClass, Type parameterType, Class<?> parameterClass, Config config, String configPropName) {
        if (parameterClass == Boolean.class || parameterClass == Boolean.TYPE) {
            return config.getBoolean(configPropName);
        }
        if (parameterClass == Integer.class || parameterClass == Integer.TYPE) {
            return config.getInt(configPropName);
        }
        if (parameterClass == Double.class || parameterClass == Double.TYPE) {
            return config.getDouble(configPropName);
        }
        if (parameterClass == Long.class || parameterClass == Long.TYPE) {
            return config.getLong(configPropName);
        }
        if (parameterClass == String.class) {
            return config.getString(configPropName);
        }
        if (parameterClass == Duration.class) {
            return config.getDuration(configPropName);
        }
        if (parameterClass == ConfigMemorySize.class) {
            return config.getMemorySize(configPropName);
        }
        if (parameterClass == Object.class) {
            return config.getAnyRef(configPropName);
        }
        if (parameterClass == List.class) {
            return ConfigBeanImpl.getListValue(beanClass, parameterType, parameterClass, config, configPropName);
        }
        if (parameterClass == Set.class) {
            return ConfigBeanImpl.getSetValue(beanClass, parameterType, parameterClass, config, configPropName);
        }
        if (parameterClass == Map.class) {
            Type[] typeArgs = ((ParameterizedType)parameterType).getActualTypeArguments();
            if (typeArgs[0] != String.class || typeArgs[1] != Object.class) {
                throw new ConfigException.BadBean("Bean property '" + configPropName + "' of class " + beanClass.getName() + " has unsupported Map<" + typeArgs[0] + "," + typeArgs[1] + ">, only Map<String,Object> is supported right now");
            }
            return config.getObject(configPropName).unwrapped();
        }
        if (parameterClass == Config.class) {
            return config.getConfig(configPropName);
        }
        if (parameterClass == ConfigObject.class) {
            return config.getObject(configPropName);
        }
        if (parameterClass == ConfigValue.class) {
            return config.getValue(configPropName);
        }
        if (parameterClass == ConfigList.class) {
            return config.getList(configPropName);
        }
        if (parameterClass.isEnum()) {
            Object enumValue = config.getEnum(parameterClass, configPropName);
            return enumValue;
        }
        if (ConfigBeanImpl.hasAtLeastOneBeanProperty(parameterClass)) {
            return ConfigBeanImpl.createInternal(config.getConfig(configPropName), parameterClass);
        }
        throw new ConfigException.BadBean("Bean property " + configPropName + " of class " + beanClass.getName() + " has unsupported type " + parameterType);
    }

    private static Object getSetValue(Class<?> beanClass, Type parameterType, Class<?> parameterClass, Config config, String configPropName) {
        return new HashSet((List)ConfigBeanImpl.getListValue(beanClass, parameterType, parameterClass, config, configPropName));
    }

    private static Object getListValue(Class<?> beanClass, Type parameterType, Class<?> parameterClass, Config config, String configPropName) {
        Type elementType = ((ParameterizedType)parameterType).getActualTypeArguments()[0];
        if (elementType == Boolean.class) {
            return config.getBooleanList(configPropName);
        }
        if (elementType == Integer.class) {
            return config.getIntList(configPropName);
        }
        if (elementType == Double.class) {
            return config.getDoubleList(configPropName);
        }
        if (elementType == Long.class) {
            return config.getLongList(configPropName);
        }
        if (elementType == String.class) {
            return config.getStringList(configPropName);
        }
        if (elementType == Duration.class) {
            return config.getDurationList(configPropName);
        }
        if (elementType == ConfigMemorySize.class) {
            return config.getMemorySizeList(configPropName);
        }
        if (elementType == Object.class) {
            return config.getAnyRefList(configPropName);
        }
        if (elementType == Config.class) {
            return config.getConfigList(configPropName);
        }
        if (elementType == ConfigObject.class) {
            return config.getObjectList(configPropName);
        }
        if (elementType == ConfigValue.class) {
            return config.getList(configPropName);
        }
        if (((Class)elementType).isEnum()) {
            List enumValues = config.getEnumList((Class)elementType, configPropName);
            return enumValues;
        }
        if (ConfigBeanImpl.hasAtLeastOneBeanProperty((Class)elementType)) {
            ArrayList beanList = new ArrayList();
            List<? extends Config> configList = config.getConfigList(configPropName);
            for (Config config2 : configList) {
                beanList.add(ConfigBeanImpl.createInternal(config2, (Class)elementType));
            }
            return beanList;
        }
        throw new ConfigException.BadBean("Bean property '" + configPropName + "' of class " + beanClass.getName() + " has unsupported list element type " + elementType);
    }

    private static ConfigValueType getValueTypeOrNull(Class<?> parameterClass) {
        if (parameterClass == Boolean.class || parameterClass == Boolean.TYPE) {
            return ConfigValueType.BOOLEAN;
        }
        if (parameterClass == Integer.class || parameterClass == Integer.TYPE) {
            return ConfigValueType.NUMBER;
        }
        if (parameterClass == Double.class || parameterClass == Double.TYPE) {
            return ConfigValueType.NUMBER;
        }
        if (parameterClass == Long.class || parameterClass == Long.TYPE) {
            return ConfigValueType.NUMBER;
        }
        if (parameterClass == String.class) {
            return ConfigValueType.STRING;
        }
        if (parameterClass == Duration.class) {
            return null;
        }
        if (parameterClass == ConfigMemorySize.class) {
            return null;
        }
        if (parameterClass == List.class) {
            return ConfigValueType.LIST;
        }
        if (parameterClass == Map.class) {
            return ConfigValueType.OBJECT;
        }
        if (parameterClass == Config.class) {
            return ConfigValueType.OBJECT;
        }
        if (parameterClass == ConfigObject.class) {
            return ConfigValueType.OBJECT;
        }
        if (parameterClass == ConfigList.class) {
            return ConfigValueType.LIST;
        }
        return null;
    }

    private static boolean hasAtLeastOneBeanProperty(Class<?> clazz) {
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(clazz);
        }
        catch (IntrospectionException e) {
            return false;
        }
        for (PropertyDescriptor beanProp : beanInfo.getPropertyDescriptors()) {
            if (beanProp.getReadMethod() == null || beanProp.getWriteMethod() == null) continue;
            return true;
        }
        return false;
    }

    private static boolean isOptionalProperty(Class beanClass, PropertyDescriptor beanProp) {
        Field field = ConfigBeanImpl.getField(beanClass, beanProp.getName());
        return field != null ? ((Optional[])field.getAnnotationsByType(Optional.class)).length > 0 : ((Optional[])beanProp.getReadMethod().getAnnotationsByType(Optional.class)).length > 0;
    }

    private static Field getField(Class beanClass, String fieldName) {
        try {
            Field field = beanClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        }
        catch (NoSuchFieldException noSuchFieldException) {
            beanClass = beanClass.getSuperclass();
            if (beanClass == null) {
                return null;
            }
            return ConfigBeanImpl.getField(beanClass, fieldName);
        }
    }
}

