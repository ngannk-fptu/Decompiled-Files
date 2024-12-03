/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.beanutils.PropertyUtils
 */
package org.apache.velocity.tools.generic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.velocity.tools.config.DefaultKey;

@DefaultKey(value="sorter")
public class SortTool {
    public <T> Collection<T> sort(Collection<T> c, Comparator<T> comparator) {
        ArrayList<T> list = new ArrayList<T>(c);
        Collections.sort(list, comparator);
        return list;
    }

    public <T> T[] sort(T[] a, Comparator<T> comparator) {
        Object[] copy = (Object[])a.clone();
        Arrays.sort(copy, comparator);
        return copy;
    }

    public <T> Collection<T> sort(Map<?, T> map, Comparator<T> comparator) {
        return this.sort(map.values(), comparator);
    }

    public Collection<?> sort(Object o, Comparator<?> comparator) {
        if (o instanceof Collection) {
            return this.sort((Object)((Collection)o), comparator);
        }
        if (o instanceof Object[]) {
            return this.sort((Object)((Object[])o), comparator);
        }
        if (o instanceof Map) {
            return this.sort((Object)((Map)o), comparator);
        }
        return null;
    }

    public Collection sort(Collection collection) {
        return this.sort(collection, (List)null);
    }

    public Collection sort(Object[] array) {
        return this.sort(array, (List)null);
    }

    public Collection sort(Map map) {
        return this.sort(map, (List)null);
    }

    public Collection sort(Object object, String property) {
        ArrayList<String> properties = new ArrayList<String>(1);
        properties.add(property);
        if (object instanceof Collection) {
            return this.sort((Collection)object, properties);
        }
        if (object instanceof Object[]) {
            return this.sort((Object[])object, properties);
        }
        if (object instanceof Map) {
            return this.sort((Map)object, properties);
        }
        return null;
    }

    public Collection sort(Collection collection, List properties) {
        ArrayList list = new ArrayList(collection.size());
        list.addAll(collection);
        return this.internalSort(list, properties);
    }

    public Collection sort(Map map, List properties) {
        return this.sort(map.values(), properties);
    }

    public Collection sort(Object[] array, List properties) {
        return this.internalSort(Arrays.asList(array), properties);
    }

    protected Collection internalSort(List list, List properties) {
        try {
            if (properties == null) {
                Collections.sort(list);
            } else {
                Collections.sort(list, new PropertiesComparator(properties));
            }
            return list;
        }
        catch (Exception e) {
            return null;
        }
    }

    protected static Comparable getComparable(Object object, String property) {
        try {
            return (Comparable)PropertyUtils.getProperty((Object)object, (String)property);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Could not retrieve comparable value for '" + property + "' from " + object + ": " + e);
        }
    }

    public static class PropertiesComparator
    implements Comparator,
    Serializable {
        private static final int TYPE_ASCENDING = 1;
        private static final int TYPE_DESCENDING = -1;
        public static final String TYPE_ASCENDING_SHORT = "asc";
        public static final String TYPE_DESCENDING_SHORT = "desc";
        List properties;
        int[] sortTypes;

        public PropertiesComparator(List props) {
            this.properties = new ArrayList(props.size());
            this.properties.addAll(props);
            this.sortTypes = new int[this.properties.size()];
            for (int i = 0; i < this.properties.size(); ++i) {
                if (this.properties.get(i) == null) {
                    throw new IllegalArgumentException("Property " + i + "is null, sort properties may not be null.");
                }
                String prop = this.properties.get(i).toString();
                int colonIndex = prop.indexOf(58);
                if (colonIndex != -1) {
                    String sortType = prop.substring(colonIndex + 1);
                    this.properties.set(i, prop.substring(0, colonIndex));
                    if (TYPE_ASCENDING_SHORT.equalsIgnoreCase(sortType)) {
                        this.sortTypes[i] = 1;
                        continue;
                    }
                    if (TYPE_DESCENDING_SHORT.equalsIgnoreCase(sortType)) {
                        this.sortTypes[i] = -1;
                        continue;
                    }
                    this.sortTypes[i] = 1;
                    continue;
                }
                this.sortTypes[i] = 1;
            }
        }

        public int compare(Object lhs, Object rhs) {
            for (int i = 0; i < this.properties.size(); ++i) {
                int comparison = 0;
                String property = (String)this.properties.get(i);
                Comparable left = SortTool.getComparable(lhs, property);
                Comparable right = SortTool.getComparable(rhs, property);
                if (left == null && right != null) {
                    comparison = right.compareTo(null);
                    comparison *= -1;
                } else if (left instanceof String) {
                    comparison = ((String)((Object)left)).compareToIgnoreCase((String)((Object)right));
                } else if (left != null) {
                    comparison = left.compareTo(right);
                }
                if (comparison == 0) continue;
                return comparison * this.sortTypes[i];
            }
            return 0;
        }
    }
}

