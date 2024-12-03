/*
 * Decompiled with CFR 0.152.
 */
package org.ehcache.sizeof;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.ServiceLoader;
import java.util.concurrent.CopyOnWriteArrayList;
import org.ehcache.sizeof.Filter;
import org.ehcache.sizeof.FilterConfigurator;
import org.ehcache.sizeof.filters.AnnotationSizeOfFilter;
import org.ehcache.sizeof.filters.SizeOfFilter;
import org.ehcache.sizeof.filters.TypeFilter;

public final class SizeOfFilterSource
implements Filter {
    private final CopyOnWriteArrayList<SizeOfFilter> filters = new CopyOnWriteArrayList();
    private final TypeFilter typeFilter = new TypeFilter();

    public SizeOfFilterSource(boolean registerAnnotationFilter) {
        this.filters.add(this.typeFilter);
        if (registerAnnotationFilter) {
            this.filters.add(new AnnotationSizeOfFilter());
        }
        this.applyMutators();
    }

    private void applyMutators() {
        this.applyMutators(SizeOfFilterSource.class.getClassLoader());
    }

    void applyMutators(ClassLoader classLoader) {
        ServiceLoader<FilterConfigurator> loader = ServiceLoader.load(FilterConfigurator.class, classLoader);
        for (FilterConfigurator filterConfigurator : loader) {
            filterConfigurator.configure(this);
        }
    }

    public SizeOfFilter[] getFilters() {
        ArrayList<SizeOfFilter> allFilters = new ArrayList<SizeOfFilter>(this.filters);
        return allFilters.toArray(new SizeOfFilter[allFilters.size()]);
    }

    @Override
    public void ignoreInstancesOf(Class clazz, boolean strict) {
        this.typeFilter.addClass(clazz, Modifier.isFinal(clazz.getModifiers()) || strict);
    }

    @Override
    public void ignoreField(Field field) {
        this.typeFilter.addField(field);
    }
}

