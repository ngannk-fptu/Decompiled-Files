/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Closure;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.CachedMethod;
import org.codehaus.groovy.reflection.ReflectionCache;
import org.codehaus.groovy.runtime.metaclass.DefaultMetaClassInfo;
import org.codehaus.groovy.runtime.metaclass.NewInstanceMetaMethod;
import org.codehaus.groovy.vmplugin.VMPluginFactory;

public class GroovyCategorySupport {
    private static int categoriesInUse = 0;
    private static final MyThreadLocal THREAD_INFO = new MyThreadLocal();

    public static AtomicInteger getCategoryNameUsage(String name) {
        return THREAD_INFO.getUsage(name);
    }

    public static <T> T use(Class categoryClass, Closure<T> closure) {
        return (T)GroovyCategorySupport.THREAD_INFO.getInfo().use(categoryClass, closure);
    }

    public static <T> T use(List<Class> categoryClasses, Closure<T> closure) {
        return THREAD_INFO.getInfo().use(categoryClasses, closure);
    }

    public static boolean hasCategoryInCurrentThread() {
        if (categoriesInUse == 0) {
            return false;
        }
        ThreadCategoryInfo infoNullable = THREAD_INFO.getInfoNullable();
        return infoNullable != null && infoNullable.level != 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    public static boolean hasCategoryInAnyThread() {
        Object object = ThreadCategoryInfo.LOCK;
        synchronized (object) {
            return categoriesInUse != 0;
        }
    }

    public static CategoryMethodList getCategoryMethods(String name) {
        ThreadCategoryInfo categoryInfo = THREAD_INFO.getInfoNullable();
        return categoryInfo == null ? null : categoryInfo.getCategoryMethods(name);
    }

    public static String getPropertyCategoryGetterName(String propertyName) {
        ThreadCategoryInfo categoryInfo = THREAD_INFO.getInfoNullable();
        return categoryInfo == null ? null : categoryInfo.getPropertyCategoryGetterName(propertyName);
    }

    public static String getPropertyCategorySetterName(String propertyName) {
        ThreadCategoryInfo categoryInfo = THREAD_INFO.getInfoNullable();
        return categoryInfo == null ? null : categoryInfo.getPropertyCategorySetterName(propertyName);
    }

    private static class MyThreadLocal
    extends ThreadLocal<SoftReference> {
        ConcurrentHashMap<String, AtomicInteger> usage = new ConcurrentHashMap();

        private MyThreadLocal() {
        }

        public ThreadCategoryInfo getInfo() {
            ThreadCategoryInfo tcinfo;
            SoftReference reference = (SoftReference)this.get();
            if (reference != null) {
                tcinfo = (ThreadCategoryInfo)reference.get();
                if (tcinfo == null) {
                    tcinfo = new ThreadCategoryInfo();
                    this.set(new SoftReference<ThreadCategoryInfo>(tcinfo));
                }
            } else {
                tcinfo = new ThreadCategoryInfo();
                this.set(new SoftReference<ThreadCategoryInfo>(tcinfo));
            }
            return tcinfo;
        }

        public ThreadCategoryInfo getInfoNullable() {
            SoftReference reference = (SoftReference)this.get();
            return reference == null ? null : (ThreadCategoryInfo)reference.get();
        }

        public AtomicInteger getUsage(String name) {
            AtomicInteger u = this.usage.get(name);
            if (u != null) {
                return u;
            }
            AtomicInteger ai = new AtomicInteger();
            AtomicInteger prev = this.usage.putIfAbsent(name, ai);
            return prev == null ? ai : prev;
        }
    }

    public static class CategoryMethod
    extends NewInstanceMetaMethod
    implements Comparable {
        private final Class metaClass;

        public CategoryMethod(CachedMethod metaMethod, Class metaClass) {
            super(metaMethod);
            this.metaClass = metaClass;
        }

        @Override
        public boolean isCacheable() {
            return false;
        }

        public int compareTo(Object o) {
            CategoryMethod thatMethod = (CategoryMethod)o;
            Class thisClass = this.metaClass;
            Class thatClass = thatMethod.metaClass;
            if (thisClass == thatClass) {
                return 0;
            }
            if (this.isChildOfParent(thisClass, thatClass)) {
                return -1;
            }
            if (this.isChildOfParent(thatClass, thisClass)) {
                return 1;
            }
            return 0;
        }

        private boolean isChildOfParent(Class candidateChild, Class candidateParent) {
            Class loop = candidateChild;
            while (loop != null && loop != Object.class) {
                if ((loop = loop.getSuperclass()) != candidateParent) continue;
                return true;
            }
            return false;
        }
    }

    public static class ThreadCategoryInfo
    extends HashMap<String, CategoryMethodList> {
        private static final Object LOCK = new Object();
        int level;
        private Map<String, String> propertyGetterMap;
        private Map<String, String> propertySetterMap;

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void newScope() {
            Object object = LOCK;
            synchronized (object) {
                categoriesInUse++;
                DefaultMetaClassInfo.setCategoryUsed(true);
            }
            VMPluginFactory.getPlugin().invalidateCallSites();
            ++this.level;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void endScope() {
            Iterator it = this.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = it.next();
                CategoryMethodList list = (CategoryMethodList)e.getValue();
                if (list.level != this.level) continue;
                CategoryMethodList prev = list.previous;
                if (prev == null) {
                    it.remove();
                    list.usage.addAndGet(-list.size());
                    continue;
                }
                e.setValue(prev);
                list.usage.addAndGet(prev.size() - list.size());
            }
            --this.level;
            VMPluginFactory.getPlugin().invalidateCallSites();
            Object object = LOCK;
            synchronized (object) {
                if (--categoriesInUse == 0) {
                    DefaultMetaClassInfo.setCategoryUsed(false);
                }
            }
            if (this.level == 0) {
                THREAD_INFO.remove();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private <T> T use(Class categoryClass, Closure<T> closure) {
            this.newScope();
            try {
                this.use(categoryClass);
                T t = closure.call();
                return t;
            }
            finally {
                this.endScope();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public <T> T use(List<Class> categoryClasses, Closure<T> closure) {
            this.newScope();
            try {
                for (Class categoryClass : categoryClasses) {
                    this.use(categoryClass);
                }
                Iterator iterator = closure.call();
                return (T)iterator;
            }
            finally {
                this.endScope();
            }
        }

        private void applyUse(CachedClass cachedClass) {
            CachedMethod[] methods;
            for (CachedMethod cachedMethod : methods = cachedClass.getMethods()) {
                CachedClass[] paramTypes;
                if (!cachedMethod.isStatic() || !cachedMethod.isPublic() || (paramTypes = cachedMethod.getParameterTypes()).length <= 0) continue;
                CachedClass metaClass = paramTypes[0];
                CategoryMethod mmethod = new CategoryMethod(cachedMethod, metaClass.getTheClass());
                String name = cachedMethod.getName();
                CategoryMethodList list = (CategoryMethodList)this.get(name);
                if (list == null || list.level != this.level) {
                    list = new CategoryMethodList(name, this.level, list);
                    this.put(name, list);
                }
                list.add(mmethod);
                Collections.sort(list);
                this.cachePropertyAccessor(mmethod);
            }
        }

        private void cachePropertyAccessor(CategoryMethod method) {
            String name = method.getName();
            int parameterLength = method.getParameterTypes().length;
            if (name.startsWith("get") && name.length() > 3 && parameterLength == 0) {
                this.propertyGetterMap = this.putPropertyAccessor(3, name, this.propertyGetterMap);
            } else if (name.startsWith("set") && name.length() > 3 && parameterLength == 1) {
                this.propertySetterMap = this.putPropertyAccessor(3, name, this.propertySetterMap);
            }
        }

        private Map<String, String> putPropertyAccessor(int prefixLength, String accessorName, Map<String, String> map) {
            if (map == null) {
                map = new HashMap<String, String>();
            }
            String property = accessorName.substring(prefixLength, prefixLength + 1).toLowerCase() + accessorName.substring(prefixLength + 1);
            map.put(property, accessorName);
            return map;
        }

        private void use(Class categoryClass) {
            CachedClass cachedClass = ReflectionCache.getCachedClass(categoryClass);
            LinkedList<CachedClass> classStack = new LinkedList<CachedClass>();
            CachedClass superClass = cachedClass;
            while (superClass.getTheClass() != Object.class) {
                classStack.add(superClass);
                superClass = superClass.getCachedSuperClass();
            }
            while (!classStack.isEmpty()) {
                CachedClass klazz = (CachedClass)classStack.removeLast();
                this.applyUse(klazz);
            }
        }

        public CategoryMethodList getCategoryMethods(String name) {
            return this.level == 0 ? null : (CategoryMethodList)this.get(name);
        }

        String getPropertyCategoryGetterName(String propertyName) {
            return this.propertyGetterMap != null ? this.propertyGetterMap.get(propertyName) : null;
        }

        String getPropertyCategorySetterName(String propertyName) {
            return this.propertySetterMap != null ? this.propertySetterMap.get(propertyName) : null;
        }
    }

    public static class CategoryMethodList
    extends ArrayList<CategoryMethod> {
        public final int level;
        final CategoryMethodList previous;
        final AtomicInteger usage;

        public CategoryMethodList(String name, int level, CategoryMethodList previous) {
            this.level = level;
            this.previous = previous;
            if (previous != null) {
                this.addAll(previous);
                this.usage = previous.usage;
            } else {
                this.usage = GroovyCategorySupport.getCategoryNameUsage(name);
            }
        }

        @Override
        public boolean add(CategoryMethod o) {
            this.usage.incrementAndGet();
            return super.add(o);
        }
    }
}

