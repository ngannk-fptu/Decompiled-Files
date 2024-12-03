/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.metaclass;

import groovy.lang.MetaMethod;
import java.util.NoSuchElementException;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.metaclass.ClosureMetaMethod;
import org.codehaus.groovy.runtime.metaclass.ClosureStaticMetaMethod;
import org.codehaus.groovy.runtime.metaclass.MixinInstanceMetaMethod;
import org.codehaus.groovy.runtime.metaclass.NewInstanceMetaMethod;
import org.codehaus.groovy.runtime.metaclass.NewMetaMethod;
import org.codehaus.groovy.runtime.metaclass.NewStaticMetaMethod;
import org.codehaus.groovy.util.FastArray;
import org.codehaus.groovy.util.SingleKeyHashMap;

public class MetaMethodIndex {
    public SingleKeyHashMap methodHeaders = new SingleKeyHashMap();
    protected Entry[] table;
    protected static final int DEFAULT_CAPACITY = 32;
    protected static final int MINIMUM_CAPACITY = 4;
    protected static final int MAXIMUM_CAPACITY = 0x10000000;
    protected int size;
    protected transient int threshold;

    public MetaMethodIndex(CachedClass theCachedClass) {
        this.init(32);
        CachedClass last = null;
        if (!theCachedClass.isInterface()) {
            for (CachedClass c = theCachedClass; c != null; c = c.getCachedSuperClass()) {
                SingleKeyHashMap.Entry e = this.methodHeaders.getOrPut(c.getTheClass());
                e.value = new Header(c.getTheClass(), last == null ? null : last.getTheClass());
                last = c;
            }
        } else {
            SingleKeyHashMap.Entry e = this.methodHeaders.getOrPut(Object.class);
            e.value = new Header(Object.class, theCachedClass.getTheClass());
        }
    }

    public static int hash(int h) {
        h += ~(h << 9);
        h ^= h >>> 14;
        h += h << 4;
        h ^= h >>> 10;
        return h;
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public void clear() {
        Entry[] tab = this.table;
        for (int i = 0; i < tab.length; ++i) {
            tab[i] = null;
        }
        this.size = 0;
    }

    public void init(int initCapacity) {
        this.threshold = initCapacity * 6 / 8;
        this.table = new Entry[initCapacity];
    }

    public void resize(int newLength) {
        Entry[] oldTable = this.table;
        int oldLength = this.table.length;
        Entry[] newTable = new Entry[newLength];
        for (int j = 0; j < oldLength; ++j) {
            Entry e = oldTable[j];
            while (e != null) {
                Entry next = e.nextHashEntry;
                int index = e.hash & newLength - 1;
                e.nextHashEntry = newTable[index];
                newTable[index] = e;
                e = next;
            }
        }
        this.table = newTable;
        this.threshold = 6 * newLength / 8;
    }

    public Entry[] getTable() {
        return this.table;
    }

    public EntryIterator getEntrySetIterator() {
        return new EntryIterator(){
            Entry next;
            int index;
            {
                Entry[] t = MetaMethodIndex.this.table;
                int i = t.length;
                Entry n = null;
                if (MetaMethodIndex.this.size != 0) {
                    while (i > 0 && (n = t[--i]) == null) {
                    }
                }
                this.next = n;
                this.index = i;
            }

            @Override
            public boolean hasNext() {
                return this.next != null;
            }

            @Override
            public Entry next() {
                return this.nextEntry();
            }

            Entry nextEntry() {
                Entry e = this.next;
                if (e == null) {
                    throw new NoSuchElementException();
                }
                Entry n = e.nextHashEntry;
                Entry[] t = MetaMethodIndex.this.table;
                int i = this.index;
                while (n == null && i > 0) {
                    n = t[--i];
                }
                this.index = i;
                this.next = n;
                return e;
            }
        };
    }

    public final Entry getMethods(Class cls, String name) {
        int h = MetaMethodIndex.hash(31 * cls.hashCode() + name.hashCode());
        Entry e = this.table[h & this.table.length - 1];
        while (e != null) {
            if (e.hash == h && cls == e.cls && (e.name == name || e.name.equals(name))) {
                return e;
            }
            e = e.nextHashEntry;
        }
        return null;
    }

    public Entry getOrPutMethods(String name, Header header) {
        Class cls = header.cls;
        int h = MetaMethodIndex.hash(header.clsHashCode31 + name.hashCode());
        Entry[] t = this.table;
        int index = h & t.length - 1;
        Entry e = t[index];
        while (e != null) {
            if (e.hash == h && cls == e.cls && (e.name == name || e.name.equals(name))) {
                return e;
            }
            e = e.nextHashEntry;
        }
        Entry entry = new Entry();
        entry.nextHashEntry = t[index];
        entry.hash = h;
        entry.name = name.intern();
        entry.cls = cls;
        t[index] = entry;
        entry.nextClassEntry = header.head;
        header.head = entry;
        if (++this.size == this.threshold) {
            this.resize(2 * t.length);
        }
        return entry;
    }

    public Header getHeader(Class cls) {
        SingleKeyHashMap.Entry head = this.methodHeaders.getOrPut(cls);
        if (head.value == null) {
            head.value = new Header(cls);
        }
        Header header = (Header)head.value;
        return header;
    }

    public void copyNonPrivateMethods(Class from, Class to) {
        this.copyNonPrivateMethods(this.getHeader(from), this.getHeader(to));
    }

    public void copyNonPrivateMethods(Header from, Header to) {
        Entry e = from.head;
        while (e != null) {
            this.copyNonPrivateMethods(e, to);
            e = e.nextClassEntry;
        }
    }

    public void copyAllMethodsToSuper(Header from, Header to) {
        Entry e = from.head;
        while (e != null) {
            this.copyAllMethodsToSuper(e, to);
            e = e.nextClassEntry;
        }
    }

    public void copyNonPrivateMethodsFromSuper(Header from) {
        Entry e = from.head;
        while (e != null) {
            this.copyNonPrivateMethodsFromSuper(e);
            e = e.nextClassEntry;
        }
    }

    private void copyNonPrivateMethods(Entry from, Header to) {
        Object oldListOrMethod = from.methods;
        if (oldListOrMethod instanceof FastArray) {
            FastArray oldList = (FastArray)oldListOrMethod;
            Entry e = null;
            int len1 = oldList.size();
            Object[] list = oldList.getArray();
            for (int j = 0; j != len1; ++j) {
                MetaMethod method = (MetaMethod)list[j];
                if (method.isPrivate()) continue;
                if (e == null) {
                    e = this.getOrPutMethods(from.name, to);
                }
                e.methods = this.addMethodToList(e.methods, method);
            }
        } else {
            MetaMethod method = (MetaMethod)oldListOrMethod;
            if (!method.isPrivate()) {
                Entry e = this.getOrPutMethods(from.name, to);
                e.methods = this.addMethodToList(e.methods, method);
            }
        }
    }

    private void copyAllMethodsToSuper(Entry from, Header to) {
        Object oldListOrMethod = from.methods;
        if (oldListOrMethod instanceof FastArray) {
            FastArray oldList = (FastArray)oldListOrMethod;
            Entry e = null;
            int len1 = oldList.size();
            Object[] list = oldList.getArray();
            for (int j = 0; j != len1; ++j) {
                MetaMethod method = (MetaMethod)list[j];
                if (e == null) {
                    e = this.getOrPutMethods(from.name, to);
                }
                e.methodsForSuper = this.addMethodToList(e.methodsForSuper, method);
            }
        } else {
            MetaMethod method = (MetaMethod)oldListOrMethod;
            Entry e = this.getOrPutMethods(from.name, to);
            e.methodsForSuper = this.addMethodToList(e.methodsForSuper, method);
        }
    }

    private void copyNonPrivateMethodsFromSuper(Entry e) {
        Object oldListOrMethod = e.methodsForSuper;
        if (oldListOrMethod == null) {
            return;
        }
        if (oldListOrMethod instanceof FastArray) {
            FastArray oldList = (FastArray)oldListOrMethod;
            int len1 = oldList.size();
            Object[] list = oldList.getArray();
            for (int j = 0; j != len1; ++j) {
                MetaMethod method = (MetaMethod)list[j];
                if (method.isPrivate()) continue;
                e.methods = this.addMethodToList(e.methods, method);
            }
        } else {
            MetaMethod method = (MetaMethod)oldListOrMethod;
            if (!method.isPrivate()) {
                e.methods = this.addMethodToList(e.methods, method);
            }
        }
    }

    public void copyNonPrivateMethodsDown(Class from, Class to) {
        this.copyNonPrivateNonNewMetaMethods(this.getHeader(from), this.getHeader(to));
    }

    public void copyNonPrivateNonNewMetaMethods(Header from, Header to) {
        Entry e = from.head;
        while (e != null) {
            this.copyNonPrivateNonNewMetaMethods(e, to);
            e = e.nextClassEntry;
        }
    }

    private void copyNonPrivateNonNewMetaMethods(Entry from, Header to) {
        Object oldListOrMethod = from.methods;
        if (oldListOrMethod == null) {
            return;
        }
        if (oldListOrMethod instanceof FastArray) {
            FastArray oldList = (FastArray)oldListOrMethod;
            Entry e = null;
            int len1 = oldList.size();
            Object[] list = oldList.getArray();
            for (int j = 0; j != len1; ++j) {
                MetaMethod method = (MetaMethod)list[j];
                if (method instanceof NewMetaMethod || method.isPrivate()) continue;
                if (e == null) {
                    e = this.getOrPutMethods(from.name, to);
                }
                e.methods = this.addMethodToList(e.methods, method);
            }
        } else {
            MetaMethod method = (MetaMethod)oldListOrMethod;
            if (method instanceof NewMetaMethod || method.isPrivate()) {
                return;
            }
            Entry e = this.getOrPutMethods(from.name, to);
            e.methods = this.addMethodToList(e.methods, method);
        }
    }

    public Object addMethodToList(Object o, MetaMethod method) {
        if (o == null) {
            return method;
        }
        if (o instanceof MetaMethod) {
            CachedClass matchC;
            CachedClass methodC;
            MetaMethod match = (MetaMethod)o;
            if (!MetaMethodIndex.isMatchingMethod(match, method)) {
                FastArray list = new FastArray(2);
                list.add(match);
                list.add(method);
                return list;
            }
            if (!match.isPrivate() && (MetaMethodIndex.isNonRealMethod(match) || !match.getDeclaringClass().isInterface() || method.getDeclaringClass().isInterface() || method.isStatic()) && ((methodC = method.getDeclaringClass()) == (matchC = match.getDeclaringClass()) ? MetaMethodIndex.isNonRealMethod(method) : !methodC.isAssignableFrom(matchC.getTheClass()))) {
                return method;
            }
            return o;
        }
        if (o instanceof FastArray) {
            FastArray list = (FastArray)o;
            int found = MetaMethodIndex.findMatchingMethod(list, method);
            if (found == -1) {
                list.add(method);
            } else {
                MetaMethod match = (MetaMethod)list.get(found);
                if (match == method) {
                    return o;
                }
                if (!match.isPrivate() && (MetaMethodIndex.isNonRealMethod(match) || !match.getDeclaringClass().isInterface() || method.getDeclaringClass().isInterface() || method.isStatic())) {
                    CachedClass matchC;
                    CachedClass methodC = method.getDeclaringClass();
                    if (methodC == (matchC = match.getDeclaringClass())) {
                        if (MetaMethodIndex.isNonRealMethod(method)) {
                            list.set(found, method);
                        }
                    } else if (!methodC.isAssignableFrom(matchC.getTheClass())) {
                        list.set(found, method);
                    }
                }
            }
        }
        return o;
    }

    private static boolean isNonRealMethod(MetaMethod method) {
        return method instanceof NewInstanceMetaMethod || method instanceof NewStaticMetaMethod || method instanceof ClosureMetaMethod || method instanceof GeneratedMetaMethod || method instanceof ClosureStaticMetaMethod || method instanceof MixinInstanceMetaMethod || method instanceof ClosureMetaMethod.AnonymousMetaMethod;
    }

    private static boolean isMatchingMethod(MetaMethod aMethod, MetaMethod method) {
        CachedClass[] params2;
        if (aMethod == method) {
            return true;
        }
        CachedClass[] params1 = aMethod.getParameterTypes();
        if (params1.length != (params2 = method.getParameterTypes()).length) {
            return false;
        }
        boolean matches = true;
        for (int i = 0; i < params1.length; ++i) {
            if (params1[i] == params2[i]) continue;
            matches = false;
            break;
        }
        return matches;
    }

    private static int findMatchingMethod(FastArray list, MetaMethod method) {
        int len = list.size();
        Object[] data = list.getArray();
        for (int j = 0; j != len; ++j) {
            MetaMethod aMethod = (MetaMethod)data[j];
            if (!MetaMethodIndex.isMatchingMethod(aMethod, method)) continue;
            return j;
        }
        return -1;
    }

    public void copyMethodsToSuper() {
        for (Entry e : this.table) {
            while (e != null) {
                e.methodsForSuper = e.methods instanceof FastArray ? ((FastArray)e.methods).copy() : e.methods;
                e = e.nextHashEntry;
            }
        }
    }

    public void copy(Class c, Header index) {
        this.copy(this.getHeader(c), index);
    }

    public void copy(Header from, Header to) {
        Entry e = from.head;
        while (e != null) {
            this.copyAllMethods(e, to);
            e = e.nextClassEntry;
        }
    }

    private void copyAllMethods(Entry from, Header to) {
        Object oldListOrMethod = from.methods;
        if (oldListOrMethod instanceof FastArray) {
            FastArray oldList = (FastArray)oldListOrMethod;
            Entry e = null;
            int len1 = oldList.size();
            Object[] list = oldList.getArray();
            for (int j = 0; j != len1; ++j) {
                MetaMethod method = (MetaMethod)list[j];
                if (e == null) {
                    e = this.getOrPutMethods(from.name, to);
                }
                e.methods = this.addMethodToList(e.methods, method);
            }
        } else {
            MetaMethod method = (MetaMethod)oldListOrMethod;
            if (!method.isPrivate()) {
                Entry e = this.getOrPutMethods(from.name, to);
                e.methods = this.addMethodToList(e.methods, method);
            }
        }
    }

    public void clearCaches() {
        for (int i = 0; i != this.table.length; ++i) {
            Entry e = this.table[i];
            while (e != null) {
                e.cachedStaticMethod = null;
                e.cachedMethodForSuper = null;
                e.cachedMethod = null;
                e = e.nextHashEntry;
            }
        }
    }

    public void clearCaches(String name) {
        for (int i = 0; i != this.table.length; ++i) {
            Entry e = this.table[i];
            while (e != null) {
                if (e.name.equals(name)) {
                    e.cachedStaticMethod = null;
                    e.cachedMethodForSuper = null;
                    e.cachedMethod = null;
                }
                e = e.nextHashEntry;
            }
        }
    }

    public static interface EntryIterator {
        public boolean hasNext();

        public Entry next();
    }

    public static class Entry {
        public int hash;
        public Entry nextHashEntry;
        public Entry nextClassEntry;
        public String name;
        public Class cls;
        public Object methods;
        public Object methodsForSuper;
        public Object staticMethods;
        public CacheEntry cachedMethod;
        public CacheEntry cachedMethodForSuper;
        public CacheEntry cachedStaticMethod;

        public String toString() {
            return "[" + this.name + ", " + this.cls.getName() + "]";
        }
    }

    public static class CacheEntry {
        public final Class[] params;
        public final MetaMethod method;

        public CacheEntry(Class[] params, MetaMethod method) {
            this.params = params;
            this.method = method;
        }
    }

    public static class Header {
        public Entry head;
        Class cls;
        public int clsHashCode31;
        public Class subclass;

        public Header(Class cls) {
            this(cls, null);
        }

        public Header(Class cls, Class subclass) {
            this.cls = cls;
            this.subclass = subclass;
            this.clsHashCode31 = 31 * cls.hashCode();
        }
    }
}

