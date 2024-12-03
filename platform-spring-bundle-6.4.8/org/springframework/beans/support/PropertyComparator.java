/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.beans.support;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.SortDefinition;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public class PropertyComparator<T>
implements Comparator<T> {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final SortDefinition sortDefinition;

    public PropertyComparator(SortDefinition sortDefinition) {
        this.sortDefinition = sortDefinition;
    }

    public PropertyComparator(String property, boolean ignoreCase, boolean ascending) {
        this.sortDefinition = new MutableSortDefinition(property, ignoreCase, ascending);
    }

    public final SortDefinition getSortDefinition() {
        return this.sortDefinition;
    }

    @Override
    public int compare(T o1, T o2) {
        int result;
        Object v1 = this.getPropertyValue(o1);
        Object v2 = this.getPropertyValue(o2);
        if (this.sortDefinition.isIgnoreCase() && v1 instanceof String && v2 instanceof String) {
            v1 = ((String)v1).toLowerCase();
            v2 = ((String)v2).toLowerCase();
        }
        try {
            result = v1 != null ? (v2 != null ? ((Comparable)v1).compareTo(v2) : -1) : (v2 != null ? 1 : 0);
        }
        catch (RuntimeException ex) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Could not sort objects [" + o1 + "] and [" + o2 + "]"), (Throwable)ex);
            }
            return 0;
        }
        return this.sortDefinition.isAscending() ? result : -result;
    }

    @Nullable
    private Object getPropertyValue(Object obj) {
        try {
            BeanWrapperImpl beanWrapper = new BeanWrapperImpl(false);
            beanWrapper.setWrappedInstance(obj);
            return beanWrapper.getPropertyValue(this.sortDefinition.getProperty());
        }
        catch (BeansException ex) {
            this.logger.debug((Object)"PropertyComparator could not access property - treating as null for sorting", (Throwable)ex);
            return null;
        }
    }

    public static void sort(List<?> source, SortDefinition sortDefinition) throws BeansException {
        if (StringUtils.hasText(sortDefinition.getProperty())) {
            source.sort(new PropertyComparator(sortDefinition));
        }
    }

    public static void sort(Object[] source, SortDefinition sortDefinition) throws BeansException {
        if (StringUtils.hasText(sortDefinition.getProperty())) {
            Arrays.sort(source, new PropertyComparator(sortDefinition));
        }
    }
}

