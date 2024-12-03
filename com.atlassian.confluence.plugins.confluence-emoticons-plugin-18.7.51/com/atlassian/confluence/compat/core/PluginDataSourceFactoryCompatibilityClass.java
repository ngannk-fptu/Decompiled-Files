/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.PluginDataSourceFactory
 *  com.atlassian.fugue.Maybe
 *  javax.activation.DataSource
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.compat.core;

import com.atlassian.confluence.core.PluginDataSourceFactory;
import com.atlassian.fugue.Maybe;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import javax.activation.DataSource;
import javax.annotation.Nullable;

public abstract class PluginDataSourceFactoryCompatibilityClass {
    public static Iterable<DataSource> getResourcesFromModules(PluginDataSourceFactory pluginDataSourceFactory, String moduleKey, @Nullable String filterByTypeEnumValue) {
        try {
            Class<?> predicateClazz = Class.forName("com.atlassian.confluence.core.PluginDataSourceFactory$FilterByType");
            Object enumConstant = Enum.valueOf(predicateClazz, filterByTypeEnumValue);
            return PluginDataSourceFactoryCompatibilityClass.getResourcesFromModulesMethod(predicateClazz, pluginDataSourceFactory, moduleKey, enumConstant);
        }
        catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Iterable<DataSource> getResourcesFromModulesMethod(Class<?> predicateClazz, PluginDataSourceFactory pluginDataSourceFactory, String moduleKey, Object enumConstant) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class[] callArguments = new Class[]{String.class, predicateClazz.getInterfaces()[0]};
        try {
            Method method = pluginDataSourceFactory.getClass().getMethod("getResourcesFromModules", callArguments);
            Optional dataSources = (Optional)method.invoke((Object)pluginDataSourceFactory, moduleKey, enumConstant);
            return (Iterable)dataSources.get();
        }
        catch (NoSuchMethodException e) {
            Method method = pluginDataSourceFactory.getClass().getMethod("resourcesFromModules", callArguments);
            Maybe dataSources = (Maybe)method.invoke((Object)pluginDataSourceFactory, moduleKey, enumConstant);
            return (Iterable)dataSources.get();
        }
    }
}

