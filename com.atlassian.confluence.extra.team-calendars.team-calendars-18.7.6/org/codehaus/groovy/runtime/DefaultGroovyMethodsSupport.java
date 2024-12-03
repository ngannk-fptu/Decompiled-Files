/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.EmptyRange;
import groovy.lang.IntRange;
import groovy.lang.Range;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Logger;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.NullObject;
import org.codehaus.groovy.runtime.RangeInfo;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class DefaultGroovyMethodsSupport {
    private static final Logger LOG = Logger.getLogger(DefaultGroovyMethodsSupport.class.getName());
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    protected static RangeInfo subListBorders(int size, Range range) {
        if (range instanceof IntRange) {
            return ((IntRange)range).subListBorders(size);
        }
        int from = DefaultGroovyMethodsSupport.normaliseIndex(DefaultTypeTransformation.intUnbox(range.getFrom()), size);
        int to = DefaultGroovyMethodsSupport.normaliseIndex(DefaultTypeTransformation.intUnbox(range.getTo()), size);
        boolean reverse = range.isReverse();
        if (from > to) {
            int tmp = to;
            to = from;
            from = tmp;
            reverse = !reverse;
        }
        return new RangeInfo(from, to + 1, reverse);
    }

    protected static RangeInfo subListBorders(int size, EmptyRange range) {
        int from = DefaultGroovyMethodsSupport.normaliseIndex(DefaultTypeTransformation.intUnbox(range.getFrom()), size);
        return new RangeInfo(from, from, false);
    }

    protected static int normaliseIndex(int i, int size) {
        int temp = i;
        if (i < 0) {
            i += size;
        }
        if (i < 0) {
            throw new ArrayIndexOutOfBoundsException("Negative array index [" + temp + "] too large for array size " + size);
        }
        return i;
    }

    public static void closeWithWarning(Closeable c) {
        if (c != null) {
            try {
                c.close();
            }
            catch (IOException e) {
                LOG.warning("Caught exception during close(): " + e);
            }
        }
    }

    public static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    protected static <T> Collection<T> cloneSimilarCollection(Collection<T> orig, int newCapacity) {
        Collection<T> answer = (Collection<T>)DefaultGroovyMethodsSupport.cloneObject(orig);
        if (answer != null) {
            return answer;
        }
        answer = DefaultGroovyMethodsSupport.createSimilarCollection(orig, newCapacity);
        answer.addAll(orig);
        return answer;
    }

    private static Object cloneObject(Object orig) {
        if (orig instanceof Cloneable) {
            try {
                return InvokerHelper.invokeMethod(orig, "clone", EMPTY_OBJECT_ARRAY);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return null;
    }

    protected static Collection createSimilarOrDefaultCollection(Object object) {
        if (object instanceof Collection) {
            return DefaultGroovyMethodsSupport.createSimilarCollection((Collection)object);
        }
        return new ArrayList();
    }

    protected static <T> Collection<T> createSimilarCollection(Iterable<T> iterable) {
        if (iterable instanceof Collection) {
            return DefaultGroovyMethodsSupport.createSimilarCollection((Collection)iterable);
        }
        return new ArrayList();
    }

    protected static <T> Collection<T> createSimilarCollection(Collection<T> collection) {
        return DefaultGroovyMethodsSupport.createSimilarCollection(collection, collection.size());
    }

    protected static <T> Collection<T> createSimilarCollection(Collection<T> orig, int newCapacity) {
        if (orig instanceof Set) {
            return DefaultGroovyMethodsSupport.createSimilarSet((Set)orig);
        }
        if (orig instanceof List) {
            return DefaultGroovyMethodsSupport.createSimilarList((List)orig, newCapacity);
        }
        if (orig instanceof Queue) {
            return new LinkedList();
        }
        return new ArrayList(newCapacity);
    }

    protected static <T> List<T> createSimilarList(List<T> orig, int newCapacity) {
        if (orig instanceof LinkedList) {
            return new LinkedList();
        }
        if (orig instanceof Stack) {
            return new Stack();
        }
        if (orig instanceof Vector) {
            return new Vector();
        }
        return new ArrayList(newCapacity);
    }

    protected static <T> T[] createSimilarArray(T[] orig, int newCapacity) {
        Class<?> componentType = orig.getClass().getComponentType();
        return (Object[])Array.newInstance(componentType, newCapacity);
    }

    protected static <T> Set<T> createSimilarSet(Set<T> orig) {
        if (orig instanceof SortedSet) {
            return new TreeSet(((SortedSet)orig).comparator());
        }
        return new LinkedHashSet();
    }

    protected static <K, V> Map<K, V> createSimilarMap(Map<K, V> orig) {
        if (orig instanceof SortedMap) {
            return new TreeMap(((SortedMap)orig).comparator());
        }
        if (orig instanceof Properties) {
            return new Properties();
        }
        if (orig instanceof Hashtable) {
            return new Hashtable();
        }
        return new LinkedHashMap();
    }

    protected static <K, V> Map<K, V> cloneSimilarMap(Map<K, V> orig) {
        Map answer = (Map)DefaultGroovyMethodsSupport.cloneObject(orig);
        if (answer != null) {
            return answer;
        }
        if (orig instanceof TreeMap) {
            return new TreeMap<K, V>(orig);
        }
        if (orig instanceof Properties) {
            Properties map = new Properties();
            map.putAll(orig);
            return map;
        }
        if (orig instanceof Hashtable) {
            return new Hashtable<K, V>(orig);
        }
        return new LinkedHashMap<K, V>(orig);
    }

    protected static boolean sameType(Collection[] cols) {
        LinkedList all = new LinkedList();
        for (Collection col : cols) {
            all.addAll(col);
        }
        if (all.isEmpty()) {
            return true;
        }
        Object first = all.get(0);
        Class baseClass = first instanceof Number ? Number.class : (first == null ? NullObject.class : first.getClass());
        for (Collection col : cols) {
            for (Object o : col) {
                if (baseClass.isInstance(o)) continue;
                return false;
            }
        }
        return true;
    }
}

