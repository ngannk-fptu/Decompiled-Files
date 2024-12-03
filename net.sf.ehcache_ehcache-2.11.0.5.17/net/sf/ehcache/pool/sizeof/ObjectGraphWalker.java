/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.pool.sizeof;

import java.lang.ref.SoftReference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Stack;
import net.sf.ehcache.pool.sizeof.FlyweightType;
import net.sf.ehcache.pool.sizeof.MaxDepthExceededException;
import net.sf.ehcache.pool.sizeof.filter.SizeOfFilter;
import net.sf.ehcache.util.WeakIdentityConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ObjectGraphWalker {
    private static final Logger LOG = LoggerFactory.getLogger(ObjectGraphWalker.class);
    private static final String TC_INTERNAL_FIELD_PREFIX = "$__tc_";
    private static final String VERBOSE_DEBUG_LOGGING = "net.sf.ehcache.sizeof.verboseDebugLogging";
    private static final String CONTINUE_MESSAGE = "The configured limit of {0} object references was reached while attempting to calculate the size of the object graph. Severe performance degradation could occur if the sizing operation continues. This can be avoided by setting the CacheManger or Cache <sizeOfPolicy> element's maxDepthExceededBehavior to \"abort\" or adding stop points with @IgnoreSizeOf annotations. If performance degradation is NOT an issue at the configured limit, raise the limit value using the CacheManager or Cache <sizeOfPolicy> element's maxDepth attribute. For more information, see the Ehcache configuration documentation.";
    private static final String ABORT_MESSAGE = "The configured limit of {0} object references was reached while attempting to calculate the size of the object graph. This can be avoided by adding stop points with @IgnoreSizeOf annotations. Since the CacheManger or Cache <sizeOfPolicy> element's maxDepthExceededBehavior is set to \"abort\", the sizing operation has stopped and the reported cache size is not accurate. If performance degradation is NOT an issue at the configured limit, raise the limit value using the CacheManager or Cache <sizeOfPolicy> element's maxDepth attribute. For more information, see the Ehcache configuration documentation.";
    private static final boolean USE_VERBOSE_DEBUG_LOGGING = ObjectGraphWalker.getVerboseSizeOfDebugLogging();
    private final WeakIdentityConcurrentMap<Class<?>, SoftReference<Collection<Field>>> fieldCache = new WeakIdentityConcurrentMap();
    private final WeakIdentityConcurrentMap<Class<?>, Boolean> classCache = new WeakIdentityConcurrentMap();
    private final SizeOfFilter sizeOfFilter;
    private final Visitor visitor;

    ObjectGraphWalker(Visitor visitor, SizeOfFilter filter) {
        this.visitor = visitor;
        this.sizeOfFilter = filter;
    }

    private static boolean getVerboseSizeOfDebugLogging() {
        String verboseString = System.getProperty(VERBOSE_DEBUG_LOGGING, "false").toLowerCase();
        return verboseString.equals("true");
    }

    long walk(int maxDepth, boolean abortWhenMaxDepthExceeded, Object ... root) {
        StringBuilder traversalDebugMessage = null;
        long result = 0L;
        boolean warned = false;
        try {
            Stack<Object> toVisit = new Stack<Object>();
            IdentityHashMap<Object, Object> visited = new IdentityHashMap<Object, Object>();
            if (root != null) {
                if (USE_VERBOSE_DEBUG_LOGGING && LOG.isDebugEnabled()) {
                    traversalDebugMessage = new StringBuilder();
                    traversalDebugMessage.append("visiting ");
                }
                for (Object object : root) {
                    ObjectGraphWalker.nullSafeAdd(toVisit, object);
                    if (!USE_VERBOSE_DEBUG_LOGGING || !LOG.isDebugEnabled() || object == null) continue;
                    traversalDebugMessage.append(object.getClass().getName()).append("@").append(System.identityHashCode(object)).append(", ");
                }
                if (USE_VERBOSE_DEBUG_LOGGING && LOG.isDebugEnabled()) {
                    traversalDebugMessage.deleteCharAt(traversalDebugMessage.length() - 2).append("\n");
                }
            }
            while (!toVisit.isEmpty()) {
                warned = this.checkMaxDepth(maxDepth, abortWhenMaxDepthExceeded, warned, visited);
                Object ref = toVisit.pop();
                if (visited.containsKey(ref)) continue;
                Class<?> refClass = ref.getClass();
                if (!ObjectGraphWalker.isSharedFlyweight(ref) && this.shouldWalkClass(refClass)) {
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
                    long visitSize = this.calculateSize(ref);
                    if (USE_VERBOSE_DEBUG_LOGGING && LOG.isDebugEnabled()) {
                        traversalDebugMessage.append("  ").append(visitSize).append("b\t\t").append(ref.getClass().getName()).append("@").append(System.identityHashCode(ref)).append("\n");
                    }
                    result += visitSize;
                } else if (USE_VERBOSE_DEBUG_LOGGING && LOG.isDebugEnabled()) {
                    traversalDebugMessage.append("  ignored\t").append(ref.getClass().getName()).append("@").append(System.identityHashCode(ref)).append("\n");
                }
                visited.put(ref, null);
            }
            if (USE_VERBOSE_DEBUG_LOGGING && LOG.isDebugEnabled()) {
                traversalDebugMessage.append("Total size: ").append(result).append(" bytes\n");
                LOG.debug(traversalDebugMessage.toString());
            }
            return result;
        }
        catch (MaxDepthExceededException we) {
            we.addToMeasuredSize(result);
            throw we;
        }
    }

    private long calculateSize(Object ref) {
        long visitSize = 0L;
        if (ref == null) {
            return 0L;
        }
        visitSize = this.visitor.visit(ref);
        return visitSize;
    }

    private boolean checkMaxDepth(int maxDepth, boolean abortWhenMaxDepthExceeded, boolean warned, IdentityHashMap<Object, Object> visited) {
        if (visited.size() >= maxDepth) {
            if (abortWhenMaxDepthExceeded) {
                throw new MaxDepthExceededException(MessageFormat.format(ABORT_MESSAGE, maxDepth));
            }
            if (!warned) {
                LOG.warn(MessageFormat.format(CONTINUE_MESSAGE, maxDepth));
                warned = true;
            }
        }
        return warned;
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

    private static void nullSafeAdd(Stack<Object> toVisit, Object o) {
        if (o != null) {
            toVisit.push(o);
        }
    }

    private static Collection<Field> getAllFields(Class<?> refClass) {
        ArrayList<Field> fields = new ArrayList<Field>();
        for (Class<?> klazz = refClass; klazz != null; klazz = klazz.getSuperclass()) {
            for (Field field : klazz.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || field.getType().isPrimitive() || field.getName().startsWith(TC_INTERNAL_FIELD_PREFIX)) continue;
                try {
                    field.setAccessible(true);
                }
                catch (SecurityException e) {
                    LOG.error("Security settings prevent Ehcache from accessing the subgraph beneath '{}' - cache sizes may be underestimated as a result", (Object)field, (Object)e);
                    continue;
                }
                fields.add(field);
            }
        }
        return fields;
    }

    private static boolean isSharedFlyweight(Object obj) {
        FlyweightType type = FlyweightType.getFlyweightType(obj.getClass());
        return type != null && type.isShared(obj);
    }

    static interface Visitor {
        public long visit(Object var1);
    }
}

