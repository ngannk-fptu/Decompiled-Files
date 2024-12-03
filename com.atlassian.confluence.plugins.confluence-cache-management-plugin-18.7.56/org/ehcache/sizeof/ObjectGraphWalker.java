/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.ehcache.sizeof;

import java.lang.ref.SoftReference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.Set;
import org.ehcache.sizeof.FlyweightType;
import org.ehcache.sizeof.VisitorListener;
import org.ehcache.sizeof.filters.SizeOfFilter;
import org.ehcache.sizeof.util.WeakIdentityConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ObjectGraphWalker {
    private static final Logger LOG = LoggerFactory.getLogger(ObjectGraphWalker.class);
    private static final String VERBOSE_DEBUG_LOGGING = "org.ehcache.sizeof.verboseDebugLogging";
    private static final boolean USE_VERBOSE_DEBUG_LOGGING = ObjectGraphWalker.getVerboseSizeOfDebugLogging();
    private final WeakIdentityConcurrentMap<Class<?>, SoftReference<Collection<Field>>> fieldCache = new WeakIdentityConcurrentMap();
    private final WeakIdentityConcurrentMap<Class<?>, Boolean> classCache = new WeakIdentityConcurrentMap();
    private final boolean bypassFlyweight;
    private final SizeOfFilter sizeOfFilter;
    private final Visitor visitor;

    ObjectGraphWalker(Visitor visitor, SizeOfFilter filter, boolean bypassFlyweight) {
        if (visitor == null) {
            throw new NullPointerException("Visitor can't be null");
        }
        if (filter == null) {
            throw new NullPointerException("SizeOfFilter can't be null");
        }
        this.visitor = visitor;
        this.sizeOfFilter = filter;
        this.bypassFlyweight = bypassFlyweight;
    }

    private static boolean getVerboseSizeOfDebugLogging() {
        String verboseString = System.getProperty(VERBOSE_DEBUG_LOGGING, "false").toLowerCase();
        return verboseString.equals("true");
    }

    long walk(Object ... root) {
        return this.walk(null, root);
    }

    long walk(VisitorListener visitorListener, Object ... root) {
        StringBuilder traversalDebugMessage = USE_VERBOSE_DEBUG_LOGGING && LOG.isDebugEnabled() ? new StringBuilder() : null;
        long result = 0L;
        ArrayDeque<Object> toVisit = new ArrayDeque<Object>();
        Set visited = Collections.newSetFromMap(new IdentityHashMap());
        if (root != null) {
            if (traversalDebugMessage != null) {
                traversalDebugMessage.append("visiting ");
            }
            for (Object object : root) {
                ObjectGraphWalker.nullSafeAdd(toVisit, object);
                if (traversalDebugMessage == null || object == null) continue;
                traversalDebugMessage.append(object.getClass().getName()).append("@").append(System.identityHashCode(object)).append(", ");
            }
            if (traversalDebugMessage != null) {
                traversalDebugMessage.deleteCharAt(traversalDebugMessage.length() - 2).append("\n");
            }
        }
        while (!toVisit.isEmpty()) {
            Object ref = toVisit.pop();
            if (!visited.add(ref)) continue;
            Class<?> refClass = ref.getClass();
            if (!this.byPassIfFlyweight(ref) && this.shouldWalkClass(refClass)) {
                if (refClass.isArray() && !refClass.getComponentType().isPrimitive()) {
                    for (int i = 0; i < Array.getLength(ref); ++i) {
                        ObjectGraphWalker.nullSafeAdd(toVisit, Array.get(ref, i));
                    }
                } else {
                    for (Field field : this.getFilteredFields(refClass)) {
                        try {
                            ObjectGraphWalker.nullSafeAdd(toVisit, field.get(ref));
                        }
                        catch (IllegalAccessException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
                long visitSize = this.visitor.visit(ref);
                if (visitorListener != null) {
                    visitorListener.visited(ref, visitSize);
                }
                if (traversalDebugMessage != null) {
                    traversalDebugMessage.append("  ").append(visitSize).append("b\t\t").append(ref.getClass().getName()).append("@").append(System.identityHashCode(ref)).append("\n");
                }
                result += visitSize;
                continue;
            }
            if (traversalDebugMessage == null) continue;
            traversalDebugMessage.append("  ignored\t").append(ref.getClass().getName()).append("@").append(System.identityHashCode(ref)).append("\n");
        }
        if (traversalDebugMessage != null) {
            traversalDebugMessage.append("Total size: ").append(result).append(" bytes\n");
            LOG.debug(traversalDebugMessage.toString());
        }
        return result;
    }

    private Collection<Field> getFilteredFields(Class<?> refClass) {
        Collection<Field> fieldList;
        SoftReference<Collection<Field>> ref = this.fieldCache.get(refClass);
        Collection<Field> collection = fieldList = ref != null ? ref.get() : null;
        if (fieldList != null) {
            return fieldList;
        }
        Collection<Field> result = this.sizeOfFilter.filterFields(refClass, ObjectGraphWalker.getAllFields(refClass));
        if (USE_VERBOSE_DEBUG_LOGGING && LOG.isDebugEnabled()) {
            for (Field field : result) {
                if (!Modifier.isTransient(field.getModifiers())) continue;
                LOG.debug("SizeOf engine walking transient field '{}' of class {}", (Object)field.getName(), (Object)refClass.getName());
            }
        }
        this.fieldCache.put(refClass, new SoftReference<Collection<Field>>(result));
        return result;
    }

    private boolean shouldWalkClass(Class<?> refClass) {
        Boolean cached = this.classCache.get(refClass);
        if (cached == null) {
            cached = this.sizeOfFilter.filterClass(refClass);
            this.classCache.put(refClass, cached);
        }
        return cached;
    }

    private static void nullSafeAdd(Deque<Object> toVisit, Object o) {
        if (o != null) {
            toVisit.push(o);
        }
    }

    private static Collection<Field> getAllFields(Class<?> refClass) {
        ArrayList<Field> fields = new ArrayList<Field>();
        for (Class<?> klazz = refClass; klazz != null; klazz = klazz.getSuperclass()) {
            for (Field field : klazz.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || field.getType().isPrimitive()) continue;
                try {
                    field.setAccessible(true);
                }
                catch (SecurityException e) {
                    LOG.error("Security settings prevent Ehcache from accessing the subgraph beneath '{}' - cache sizes may be underestimated as a result", (Object)field, (Object)e);
                    continue;
                }
                catch (RuntimeException e) {
                    LOG.warn("The JVM is preventing Ehcache from accessing the subgraph beneath '{}' - cache sizes may be underestimated as a result", (Object)field, (Object)e);
                    continue;
                }
                fields.add(field);
            }
        }
        return fields;
    }

    private boolean byPassIfFlyweight(Object obj) {
        if (this.bypassFlyweight) {
            FlyweightType type = FlyweightType.getFlyweightType(obj.getClass());
            return type != null && type.isShared(obj);
        }
        return false;
    }

    static interface Visitor {
        public long visit(Object var1);
    }
}

