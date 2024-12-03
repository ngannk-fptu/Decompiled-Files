/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import org.mozilla.javascript.ArrowFunction;
import org.mozilla.javascript.BoundFunction;
import org.mozilla.javascript.ConsString;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.InterpretedFunction;
import org.mozilla.javascript.JavaAdapter;
import org.mozilla.javascript.NativeContinuation;
import org.mozilla.javascript.NativeGlobal;
import org.mozilla.javascript.NativeJavaPackage;
import org.mozilla.javascript.NativeJavaTopPackage;
import org.mozilla.javascript.NativeSymbol;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Symbol;
import org.mozilla.javascript.SymbolKey;
import org.mozilla.javascript.Wrapper;
import org.mozilla.javascript.debug.DebuggableObject;

final class EqualObjectGraphs {
    private static final ThreadLocal<EqualObjectGraphs> instance = new ThreadLocal();
    private final Map<Object, Object> knownEquals = new IdentityHashMap<Object, Object>();
    private final Map<Object, Object> currentlyCompared = new IdentityHashMap<Object, Object>();

    EqualObjectGraphs() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static <T> T withThreadLocal(Function<EqualObjectGraphs, T> action) {
        EqualObjectGraphs currEq = instance.get();
        if (currEq == null) {
            EqualObjectGraphs eq = new EqualObjectGraphs();
            instance.set(eq);
            try {
                T t = action.apply(eq);
                return t;
            }
            finally {
                instance.set(null);
            }
        }
        return action.apply(currEq);
    }

    boolean equalGraphs(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        Object curr2 = this.currentlyCompared.get(o1);
        if (curr2 == o2) {
            return true;
        }
        if (curr2 != null) {
            return false;
        }
        Object prev2 = this.knownEquals.get(o1);
        if (prev2 == o2) {
            return true;
        }
        if (prev2 != null) {
            return false;
        }
        Object prev1 = this.knownEquals.get(o2);
        assert (prev1 != o1);
        if (prev1 != null) {
            return false;
        }
        this.currentlyCompared.put(o1, o2);
        boolean eq = this.equalGraphsNoMemo(o1, o2);
        if (eq) {
            this.knownEquals.put(o1, o2);
            this.knownEquals.put(o2, o1);
        }
        this.currentlyCompared.remove(o1);
        return eq;
    }

    private boolean equalGraphsNoMemo(Object o1, Object o2) {
        if (o1 instanceof Wrapper) {
            return o2 instanceof Wrapper && this.equalGraphs(((Wrapper)o1).unwrap(), ((Wrapper)o2).unwrap());
        }
        if (o1 instanceof Scriptable) {
            return o2 instanceof Scriptable && this.equalScriptables((Scriptable)o1, (Scriptable)o2);
        }
        if (o1 instanceof ConsString) {
            return ((ConsString)o1).toString().equals(o2);
        }
        if (o2 instanceof ConsString) {
            return o1.equals(((ConsString)o2).toString());
        }
        if (o1 instanceof SymbolKey) {
            return o2 instanceof SymbolKey && this.equalGraphs(((SymbolKey)o1).getName(), ((SymbolKey)o2).getName());
        }
        if (o1 instanceof Object[]) {
            return o2 instanceof Object[] && this.equalObjectArrays((Object[])o1, (Object[])o2);
        }
        if (o1.getClass().isArray()) {
            return Objects.deepEquals(o1, o2);
        }
        if (o1 instanceof List) {
            return o2 instanceof List && this.equalLists((List)o1, (List)o2);
        }
        if (o1 instanceof Map) {
            return o2 instanceof Map && this.equalMaps((Map)o1, (Map)o2);
        }
        if (o1 instanceof Set) {
            return o2 instanceof Set && this.equalSets((Set)o1, (Set)o2);
        }
        if (o1 instanceof NativeGlobal) {
            return o2 instanceof NativeGlobal;
        }
        if (o1 instanceof JavaAdapter) {
            return o2 instanceof JavaAdapter;
        }
        if (o1 instanceof NativeJavaTopPackage) {
            return o2 instanceof NativeJavaTopPackage;
        }
        return o1.equals(o2);
    }

    private boolean equalScriptables(Scriptable s1, Scriptable s2) {
        Object[] ids2;
        Object[] ids1 = EqualObjectGraphs.getSortedIds(s1);
        if (!this.equalObjectArrays(ids1, ids2 = EqualObjectGraphs.getSortedIds(s2))) {
            return false;
        }
        int l = ids1.length;
        for (int i = 0; i < l; ++i) {
            if (this.equalGraphs(EqualObjectGraphs.getValue(s1, ids1[i]), EqualObjectGraphs.getValue(s2, ids2[i]))) continue;
            return false;
        }
        if (!this.equalGraphs(s1.getPrototype(), s2.getPrototype())) {
            return false;
        }
        if (!this.equalGraphs(s1.getParentScope(), s2.getParentScope())) {
            return false;
        }
        if (s1 instanceof NativeContinuation) {
            return s2 instanceof NativeContinuation && NativeContinuation.equalImplementations((NativeContinuation)s1, (NativeContinuation)s2);
        }
        if (s1 instanceof NativeJavaPackage) {
            return s1.equals(s2);
        }
        if (s1 instanceof IdFunctionObject) {
            return s2 instanceof IdFunctionObject && IdFunctionObject.equalObjectGraphs((IdFunctionObject)s1, (IdFunctionObject)s2, this);
        }
        if (s1 instanceof InterpretedFunction) {
            return s2 instanceof InterpretedFunction && EqualObjectGraphs.equalInterpretedFunctions((InterpretedFunction)s1, (InterpretedFunction)s2);
        }
        if (s1 instanceof ArrowFunction) {
            return s2 instanceof ArrowFunction && ArrowFunction.equalObjectGraphs((ArrowFunction)s1, (ArrowFunction)s2, this);
        }
        if (s1 instanceof BoundFunction) {
            return s2 instanceof BoundFunction && BoundFunction.equalObjectGraphs((BoundFunction)s1, (BoundFunction)s2, this);
        }
        if (s1 instanceof NativeSymbol) {
            return s2 instanceof NativeSymbol && this.equalGraphs(((NativeSymbol)s1).getKey(), ((NativeSymbol)s2).getKey());
        }
        return true;
    }

    private boolean equalObjectArrays(Object[] a1, Object[] a2) {
        if (a1.length != a2.length) {
            return false;
        }
        for (int i = 0; i < a1.length; ++i) {
            if (this.equalGraphs(a1[i], a2[i])) continue;
            return false;
        }
        return true;
    }

    private boolean equalLists(List<?> l1, List<?> l2) {
        if (l1.size() != l2.size()) {
            return false;
        }
        Iterator<?> i1 = l1.iterator();
        Iterator<?> i2 = l2.iterator();
        while (i1.hasNext() && i2.hasNext()) {
            if (this.equalGraphs(i1.next(), i2.next())) continue;
            return false;
        }
        assert (!i1.hasNext() && !i2.hasNext());
        return true;
    }

    private boolean equalMaps(Map<?, ?> m1, Map<?, ?> m2) {
        if (m1.size() != m2.size()) {
            return false;
        }
        Iterator<Map.Entry> i1 = EqualObjectGraphs.sortedEntries(m1);
        Iterator<Map.Entry> i2 = EqualObjectGraphs.sortedEntries(m2);
        while (i1.hasNext() && i2.hasNext()) {
            Map.Entry kv1 = i1.next();
            Map.Entry kv2 = i2.next();
            if (this.equalGraphs(kv1.getKey(), kv2.getKey()) && this.equalGraphs(kv1.getValue(), kv2.getValue())) continue;
            return false;
        }
        assert (!i1.hasNext() && !i2.hasNext());
        return true;
    }

    private static Iterator<Map.Entry> sortedEntries(Map m) {
        Map sortedMap = m instanceof SortedMap ? m : new TreeMap(m);
        return sortedMap.entrySet().iterator();
    }

    private boolean equalSets(Set<?> s1, Set<?> s2) {
        return this.equalObjectArrays(EqualObjectGraphs.sortedSet(s1), EqualObjectGraphs.sortedSet(s2));
    }

    private static Object[] sortedSet(Set<?> s) {
        Object[] a = s.toArray();
        Arrays.sort(a);
        return a;
    }

    private static boolean equalInterpretedFunctions(InterpretedFunction f1, InterpretedFunction f2) {
        return Objects.equals(f1.getEncodedSource(), f2.getEncodedSource());
    }

    private static Object[] getSortedIds(Scriptable s) {
        Object[] ids = EqualObjectGraphs.getIds(s);
        Arrays.sort(ids, (a, b) -> {
            if (a instanceof Integer) {
                if (b instanceof Integer) {
                    return ((Integer)a).compareTo((Integer)b);
                }
                if (b instanceof String || b instanceof Symbol) {
                    return -1;
                }
            } else if (a instanceof String) {
                if (b instanceof String) {
                    return ((String)a).compareTo((String)b);
                }
                if (b instanceof Integer) {
                    return 1;
                }
                if (b instanceof Symbol) {
                    return -1;
                }
            } else if (a instanceof Symbol) {
                if (b instanceof Symbol) {
                    return EqualObjectGraphs.getSymbolName((Symbol)a).compareTo(EqualObjectGraphs.getSymbolName((Symbol)b));
                }
                if (b instanceof Integer || b instanceof String) {
                    return 1;
                }
            }
            throw new ClassCastException();
        });
        return ids;
    }

    private static String getSymbolName(Symbol s) {
        if (s instanceof SymbolKey) {
            return ((SymbolKey)s).getName();
        }
        if (s instanceof NativeSymbol) {
            return ((NativeSymbol)s).getKey().getName();
        }
        throw new ClassCastException();
    }

    private static Object[] getIds(Scriptable s) {
        if (s instanceof ScriptableObject) {
            return ((ScriptableObject)s).getIds(true, true);
        }
        if (s instanceof DebuggableObject) {
            return ((DebuggableObject)((Object)s)).getAllIds();
        }
        return s.getIds();
    }

    private static Object getValue(Scriptable s, Object id) {
        if (id instanceof Symbol) {
            return ScriptableObject.getProperty(s, (Symbol)id);
        }
        if (id instanceof Integer) {
            return ScriptableObject.getProperty(s, (Integer)id);
        }
        if (id instanceof String) {
            return ScriptableObject.getProperty(s, (String)id);
        }
        throw new ClassCastException();
    }
}

