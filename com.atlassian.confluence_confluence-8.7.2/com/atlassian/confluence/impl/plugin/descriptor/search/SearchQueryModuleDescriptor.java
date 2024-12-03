/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 */
package com.atlassian.confluence.impl.plugin.descriptor.search;

import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

public class SearchQueryModuleDescriptor
extends AbstractModuleDescriptor {
    public SearchQueryModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public Object getModule() {
        return null;
    }

    public SearchQuery newQuery(List parameters) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class moduleClass = this.getModuleClass();
        if (parameters.size() == 0) {
            return this.makeQueryWithoutParameters(moduleClass);
        }
        return this.makeQueryWithParameters(moduleClass, parameters);
    }

    private SearchQuery makeQueryWithoutParameters(Class moduleClass) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        try {
            Constructor noArgCtor = moduleClass.getConstructor(new Class[0]);
            return (SearchQuery)noArgCtor.newInstance(new Object[0]);
        }
        catch (NoSuchMethodException e) {
            return this.makeQueryWithParameters(moduleClass, Collections.EMPTY_LIST);
        }
    }

    private SearchQuery makeQueryWithParameters(Class moduleClass, List parameters) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor ctor = moduleClass.getConstructor(List.class);
        return (SearchQuery)ctor.newInstance(parameters);
    }
}

