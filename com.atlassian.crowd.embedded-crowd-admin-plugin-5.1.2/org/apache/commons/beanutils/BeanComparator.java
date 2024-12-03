/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.comparators.ComparableComparator
 */
package org.apache.commons.beanutils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.comparators.ComparableComparator;

public class BeanComparator<T>
implements Comparator<T>,
Serializable {
    private String property;
    private final Comparator<?> comparator;

    public BeanComparator() {
        this(null);
    }

    public BeanComparator(String property) {
        this(property, (Comparator<?>)ComparableComparator.getInstance());
    }

    public BeanComparator(String property, Comparator<?> comparator) {
        this.setProperty(property);
        this.comparator = comparator != null ? comparator : ComparableComparator.getInstance();
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getProperty() {
        return this.property;
    }

    public Comparator<?> getComparator() {
        return this.comparator;
    }

    @Override
    public int compare(T o1, T o2) {
        if (this.property == null) {
            return this.internalCompare(o1, o2);
        }
        try {
            Object value1 = PropertyUtils.getProperty(o1, this.property);
            Object value2 = PropertyUtils.getProperty(o2, this.property);
            return this.internalCompare(value1, value2);
        }
        catch (IllegalAccessException iae) {
            throw new RuntimeException("IllegalAccessException: " + iae.toString());
        }
        catch (InvocationTargetException ite) {
            throw new RuntimeException("InvocationTargetException: " + ite.toString());
        }
        catch (NoSuchMethodException nsme) {
            throw new RuntimeException("NoSuchMethodException: " + nsme.toString());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BeanComparator)) {
            return false;
        }
        BeanComparator beanComparator = (BeanComparator)o;
        if (!this.comparator.equals(beanComparator.comparator)) {
            return false;
        }
        if (this.property != null) {
            return this.property.equals(beanComparator.property);
        }
        return beanComparator.property == null;
    }

    public int hashCode() {
        int result = this.comparator.hashCode();
        return result;
    }

    private int internalCompare(Object val1, Object val2) {
        Comparator<?> c = this.comparator;
        return c.compare(val1, val2);
    }
}

