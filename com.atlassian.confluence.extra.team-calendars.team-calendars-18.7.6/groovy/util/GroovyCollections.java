/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class GroovyCollections {
    public static List combinations(Object[] collections) {
        return GroovyCollections.combinations(Arrays.asList(collections));
    }

    public static <T> Set<List<T>> subsequences(List<T> items) {
        HashSet<List<Object>> ans = new HashSet<List<T>>();
        for (T h : items) {
            HashSet<List<Object>> next = new HashSet<List<Object>>();
            for (List list : ans) {
                ArrayList<T> sublist = new ArrayList<T>(list);
                sublist.add(h);
                next.add(sublist);
            }
            next.addAll(ans);
            ArrayList<T> hlist = new ArrayList<T>();
            hlist.add(h);
            next.add(hlist);
            ans = next;
        }
        return ans;
    }

    @Deprecated
    public static List combinations(Collection collections) {
        return GroovyCollections.combinations((Iterable)collections);
    }

    public static List combinations(Iterable collections) {
        ArrayList collectedCombos = new ArrayList();
        for (Object collection : collections) {
            Collection items = DefaultTypeTransformation.asCollection(collection);
            if (collectedCombos.isEmpty()) {
                for (Object item : items) {
                    ArrayList l = new ArrayList();
                    l.add(item);
                    collectedCombos.add(l);
                }
                continue;
            }
            ArrayList savedCombos = new ArrayList(collectedCombos);
            ArrayList newCombos = new ArrayList();
            for (Object value : items) {
                for (Object savedCombo : savedCombos) {
                    ArrayList oldList = new ArrayList((List)savedCombo);
                    oldList.add(value);
                    newCombos.add(oldList);
                }
            }
            collectedCombos = newCombos;
        }
        return collectedCombos;
    }

    public static List transpose(Object[] lists) {
        return GroovyCollections.transpose(Arrays.asList(lists));
    }

    public static List transpose(List lists) {
        List list;
        ArrayList result = new ArrayList();
        if (lists.isEmpty()) {
            return result;
        }
        int minSize = Integer.MAX_VALUE;
        for (Object listLike : lists) {
            list = (List)DefaultTypeTransformation.castToType(listLike, List.class);
            if (list.size() >= minSize) continue;
            minSize = list.size();
        }
        if (minSize == 0) {
            return result;
        }
        for (int i = 0; i < minSize; ++i) {
            result.add(new ArrayList());
        }
        for (Object listLike : lists) {
            list = (List)DefaultTypeTransformation.castToType(listLike, List.class);
            for (int i = 0; i < minSize; ++i) {
                List resultList = (List)result.get(i);
                resultList.add(list.get(i));
            }
        }
        return result;
    }

    public static <T> T min(T[] items) {
        return GroovyCollections.min(Arrays.asList(items));
    }

    @Deprecated
    public static <T> T min(Collection<T> items) {
        return GroovyCollections.min(items);
    }

    public static <T> T min(Iterable<T> items) {
        T answer = null;
        for (T value : items) {
            if (value == null || answer != null && !ScriptBytecodeAdapter.compareLessThan(value, answer)) continue;
            answer = value;
        }
        return answer;
    }

    public static <T> T max(T[] items) {
        return GroovyCollections.max(Arrays.asList(items));
    }

    @Deprecated
    public static <T> T max(Collection<T> items) {
        return GroovyCollections.max(items);
    }

    public static <T> T max(Iterable<T> items) {
        T answer = null;
        for (T value : items) {
            if (value == null || answer != null && !ScriptBytecodeAdapter.compareGreaterThan(value, answer)) continue;
            answer = value;
        }
        return answer;
    }

    public static Object sum(Object[] items) {
        return GroovyCollections.sum(Arrays.asList(items));
    }

    @Deprecated
    public static Object sum(Collection items) {
        return GroovyCollections.sum((Iterable)items);
    }

    public static Object sum(Iterable items) {
        return DefaultGroovyMethods.sum(items);
    }
}

