/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.reflection;

import groovy.lang.Closure;
import groovy.lang.ExpandoMetaClass;
import groovy.lang.ExpandoMetaClassCreationHandle;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import groovy.lang.MetaClassRegistry;
import groovy.lang.MetaMethod;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.ClassLoaderForClassArtifacts;
import org.codehaus.groovy.reflection.GroovyClassValue;
import org.codehaus.groovy.reflection.GroovyClassValueFactory;
import org.codehaus.groovy.reflection.stdclasses.ArrayCachedClass;
import org.codehaus.groovy.reflection.stdclasses.BigDecimalCachedClass;
import org.codehaus.groovy.reflection.stdclasses.BigIntegerCachedClass;
import org.codehaus.groovy.reflection.stdclasses.BooleanCachedClass;
import org.codehaus.groovy.reflection.stdclasses.ByteCachedClass;
import org.codehaus.groovy.reflection.stdclasses.CachedClosureClass;
import org.codehaus.groovy.reflection.stdclasses.CachedSAMClass;
import org.codehaus.groovy.reflection.stdclasses.CharacterCachedClass;
import org.codehaus.groovy.reflection.stdclasses.DoubleCachedClass;
import org.codehaus.groovy.reflection.stdclasses.FloatCachedClass;
import org.codehaus.groovy.reflection.stdclasses.IntegerCachedClass;
import org.codehaus.groovy.reflection.stdclasses.LongCachedClass;
import org.codehaus.groovy.reflection.stdclasses.NumberCachedClass;
import org.codehaus.groovy.reflection.stdclasses.ObjectCachedClass;
import org.codehaus.groovy.reflection.stdclasses.ShortCachedClass;
import org.codehaus.groovy.reflection.stdclasses.StringCachedClass;
import org.codehaus.groovy.util.Finalizable;
import org.codehaus.groovy.util.LazyReference;
import org.codehaus.groovy.util.LockableObject;
import org.codehaus.groovy.util.ManagedConcurrentLinkedQueue;
import org.codehaus.groovy.util.ManagedConcurrentMap;
import org.codehaus.groovy.util.ManagedReference;
import org.codehaus.groovy.util.ReferenceBundle;
import org.codehaus.groovy.vmplugin.VMPluginFactory;

public class ClassInfo
implements Finalizable {
    private final LazyCachedClassRef cachedClassRef;
    private final LazyClassLoaderRef artifactClassLoader;
    private final LockableObject lock = new LockableObject();
    public final int hash = -1;
    private final WeakReference<Class<?>> classRef;
    private static final Class<?> klazz = Sentinel.class;
    private final AtomicInteger version = new AtomicInteger();
    private MetaClass strongMetaClass;
    private ManagedReference<MetaClass> weakMetaClass;
    MetaMethod[] dgmMetaMethods = CachedClass.EMPTY;
    MetaMethod[] newMetaMethods = CachedClass.EMPTY;
    private ManagedConcurrentMap<Object, MetaClass> perInstanceMetaClassMap;
    private static final ReferenceBundle softBundle = ReferenceBundle.getSoftBundle();
    private static final ReferenceBundle weakBundle = ReferenceBundle.getWeakBundle();
    private static final ManagedConcurrentLinkedQueue<ClassInfo> modifiedExpandos = new ManagedConcurrentLinkedQueue(weakBundle);
    private static final GroovyClassValue<ClassInfo> globalClassValue = GroovyClassValueFactory.createGroovyClassValue(new GroovyClassValue.ComputeValue<ClassInfo>(){

        @Override
        public ClassInfo computeValue(Class<?> type) {
            ClassInfo ret = new ClassInfo(type);
            globalClassSet.add(ret);
            return ret;
        }
    });
    private static final GlobalClassSet globalClassSet = new GlobalClassSet();

    ClassInfo(Class klazz) {
        this.classRef = new WeakReference<Class>(klazz);
        this.cachedClassRef = new LazyCachedClassRef(softBundle, this);
        this.artifactClassLoader = new LazyClassLoaderRef(softBundle, this);
    }

    public int getVersion() {
        return this.version.get();
    }

    public void incVersion() {
        this.version.incrementAndGet();
        VMPluginFactory.getPlugin().invalidateCallSites();
    }

    public ExpandoMetaClass getModifiedExpando() {
        MetaClass strongRef = this.strongMetaClass;
        return strongRef == null ? null : (strongRef instanceof ExpandoMetaClass ? (ExpandoMetaClass)strongRef : null);
    }

    public static void clearModifiedExpandos() {
        Iterator<ClassInfo> itr = modifiedExpandos.iterator();
        while (itr.hasNext()) {
            ClassInfo info = itr.next();
            itr.remove();
            info.setStrongMetaClass(null);
        }
    }

    public final Class<?> getTheClass() {
        return (Class)this.classRef.get();
    }

    public CachedClass getCachedClass() {
        return (CachedClass)this.cachedClassRef.get();
    }

    public ClassLoaderForClassArtifacts getArtifactClassLoader() {
        return (ClassLoaderForClassArtifacts)this.artifactClassLoader.get();
    }

    public static ClassInfo getClassInfo(Class cls) {
        return globalClassValue.get(cls);
    }

    public static void remove(Class<?> cls) {
        globalClassValue.remove(cls);
    }

    public static Collection<ClassInfo> getAllClassInfo() {
        return ClassInfo.getAllGlobalClassInfo();
    }

    public static void onAllClassInfo(ClassInfoAction action) {
        for (ClassInfo classInfo : ClassInfo.getAllGlobalClassInfo()) {
            action.onClassInfo(classInfo);
        }
    }

    private static Collection<ClassInfo> getAllGlobalClassInfo() {
        return globalClassSet.values();
    }

    public MetaClass getStrongMetaClass() {
        return this.strongMetaClass;
    }

    public void setStrongMetaClass(MetaClass answer) {
        this.version.incrementAndGet();
        MetaClass strongRef = this.strongMetaClass;
        if (strongRef instanceof ExpandoMetaClass) {
            ((ExpandoMetaClass)strongRef).inRegistry = false;
            Iterator<ClassInfo> itr = modifiedExpandos.iterator();
            while (itr.hasNext()) {
                ClassInfo info = itr.next();
                if (info != this) continue;
                itr.remove();
            }
        }
        this.strongMetaClass = answer;
        if (answer instanceof ExpandoMetaClass) {
            ((ExpandoMetaClass)answer).inRegistry = true;
            modifiedExpandos.add(this);
        }
        this.replaceWeakMetaClassRef(null);
    }

    public MetaClass getWeakMetaClass() {
        ManagedReference<MetaClass> weakRef = this.weakMetaClass;
        return weakRef == null ? null : weakRef.get();
    }

    public void setWeakMetaClass(MetaClass answer) {
        this.version.incrementAndGet();
        this.strongMetaClass = null;
        ManagedReference<MetaClass> newRef = null;
        if (answer != null) {
            newRef = new ManagedReference<MetaClass>(softBundle, answer);
        }
        this.replaceWeakMetaClassRef(newRef);
    }

    private void replaceWeakMetaClassRef(ManagedReference<MetaClass> newRef) {
        ManagedReference<MetaClass> weakRef = this.weakMetaClass;
        if (weakRef != null) {
            weakRef.clear();
        }
        this.weakMetaClass = newRef;
    }

    public MetaClass getMetaClassForClass() {
        MetaClass strongMc = this.strongMetaClass;
        if (strongMc != null) {
            return strongMc;
        }
        MetaClass weakMc = this.getWeakMetaClass();
        if (ClassInfo.isValidWeakMetaClass(weakMc)) {
            return weakMc;
        }
        return null;
    }

    private MetaClass getMetaClassUnderLock() {
        MetaClassRegistry metaClassRegistry;
        MetaClassRegistry.MetaClassCreationHandle mccHandle;
        MetaClass answer = this.getStrongMetaClass();
        if (answer != null) {
            return answer;
        }
        answer = this.getWeakMetaClass();
        if (ClassInfo.isValidWeakMetaClass(answer, mccHandle = (metaClassRegistry = GroovySystem.getMetaClassRegistry()).getMetaClassCreationHandler())) {
            return answer;
        }
        answer = mccHandle.create((Class)this.classRef.get(), metaClassRegistry);
        answer.initialize();
        if (GroovySystem.isKeepJavaMetaClasses()) {
            this.setStrongMetaClass(answer);
        } else {
            this.setWeakMetaClass(answer);
        }
        return answer;
    }

    private static boolean isValidWeakMetaClass(MetaClass metaClass) {
        return ClassInfo.isValidWeakMetaClass(metaClass, GroovySystem.getMetaClassRegistry().getMetaClassCreationHandler());
    }

    private static boolean isValidWeakMetaClass(MetaClass metaClass, MetaClassRegistry.MetaClassCreationHandle mccHandle) {
        if (metaClass == null) {
            return false;
        }
        boolean enableGloballyOn = mccHandle instanceof ExpandoMetaClassCreationHandle;
        boolean cachedAnswerIsEMC = metaClass instanceof ExpandoMetaClass;
        return !enableGloballyOn || cachedAnswerIsEMC;
    }

    public final MetaClass getMetaClass() {
        MetaClass answer = this.getMetaClassForClass();
        if (answer != null) {
            return answer;
        }
        this.lock();
        try {
            MetaClass metaClass = this.getMetaClassUnderLock();
            return metaClass;
        }
        finally {
            this.unlock();
        }
    }

    public MetaClass getMetaClass(Object obj) {
        MetaClass instanceMetaClass = this.getPerInstanceMetaClass(obj);
        if (instanceMetaClass != null) {
            return instanceMetaClass;
        }
        return this.getMetaClass();
    }

    public static int size() {
        return globalClassSet.size();
    }

    public static int fullSize() {
        return globalClassSet.fullSize();
    }

    private static CachedClass createCachedClass(Class klazz, ClassInfo classInfo) {
        if (klazz == Object.class) {
            return new ObjectCachedClass(classInfo);
        }
        if (klazz == String.class) {
            return new StringCachedClass(classInfo);
        }
        CachedClass cachedClass = Number.class.isAssignableFrom(klazz) || klazz.isPrimitive() ? (klazz == Number.class ? new NumberCachedClass(klazz, classInfo) : (klazz == Integer.class || klazz == Integer.TYPE ? new IntegerCachedClass(klazz, classInfo, klazz == Integer.class) : (klazz == Double.class || klazz == Double.TYPE ? new DoubleCachedClass(klazz, classInfo, klazz == Double.class) : (klazz == BigDecimal.class ? new BigDecimalCachedClass(klazz, classInfo) : (klazz == Long.class || klazz == Long.TYPE ? new LongCachedClass(klazz, classInfo, klazz == Long.class) : (klazz == Float.class || klazz == Float.TYPE ? new FloatCachedClass(klazz, classInfo, klazz == Float.class) : (klazz == Short.class || klazz == Short.TYPE ? new ShortCachedClass(klazz, classInfo, klazz == Short.class) : (klazz == Boolean.TYPE ? new BooleanCachedClass(klazz, classInfo, false) : (klazz == Character.TYPE ? new CharacterCachedClass(klazz, classInfo, false) : (klazz == BigInteger.class ? new BigIntegerCachedClass(klazz, classInfo) : (klazz == Byte.class || klazz == Byte.TYPE ? new ByteCachedClass(klazz, classInfo, klazz == Byte.class) : new CachedClass(klazz, classInfo)))))))))))) : (klazz.getName().charAt(0) == '[' ? new ArrayCachedClass(klazz, classInfo) : (klazz == Boolean.class ? new BooleanCachedClass(klazz, classInfo, true) : (klazz == Character.class ? new CharacterCachedClass(klazz, classInfo, true) : (Closure.class.isAssignableFrom(klazz) ? new CachedClosureClass(klazz, classInfo) : (ClassInfo.isSAM(klazz) ? new CachedSAMClass(klazz, classInfo) : new CachedClass(klazz, classInfo))))));
        return cachedClass;
    }

    private static boolean isSAM(Class<?> c) {
        return CachedSAMClass.getSAMMethod(c) != null;
    }

    public void lock() {
        this.lock.lock();
    }

    public void unlock() {
        this.lock.unlock();
    }

    public MetaClass getPerInstanceMetaClass(Object obj) {
        if (this.perInstanceMetaClassMap == null) {
            return null;
        }
        return (MetaClass)this.perInstanceMetaClassMap.get(obj);
    }

    public void setPerInstanceMetaClass(Object obj, MetaClass metaClass) {
        this.version.incrementAndGet();
        if (metaClass != null) {
            if (this.perInstanceMetaClassMap == null) {
                this.perInstanceMetaClassMap = new ManagedConcurrentMap(ReferenceBundle.getWeakBundle());
            }
            this.perInstanceMetaClassMap.put(obj, metaClass);
        } else if (this.perInstanceMetaClassMap != null) {
            this.perInstanceMetaClassMap.remove(obj);
        }
    }

    public boolean hasPerInstanceMetaClasses() {
        return this.perInstanceMetaClassMap != null;
    }

    @Override
    public void finalizeReference() {
        this.setStrongMetaClass(null);
        this.cachedClassRef.clear();
        this.artifactClassLoader.clear();
    }

    static /* synthetic */ ReferenceBundle access$500() {
        return weakBundle;
    }

    public static interface ClassInfoAction {
        public void onClassInfo(ClassInfo var1);
    }

    private static class GlobalClassSet {
        private final ManagedConcurrentLinkedQueue<ClassInfo> items = new ManagedConcurrentLinkedQueue(ClassInfo.access$500());

        private GlobalClassSet() {
        }

        public int size() {
            return this.values().size();
        }

        public int fullSize() {
            return this.values().size();
        }

        public Collection<ClassInfo> values() {
            return this.items.values();
        }

        public void add(ClassInfo value) {
            this.items.add(value);
        }
    }

    private static class LazyClassLoaderRef
    extends LazyReference<ClassLoaderForClassArtifacts> {
        private final ClassInfo info;

        LazyClassLoaderRef(ReferenceBundle bundle, ClassInfo info) {
            super(bundle);
            this.info = info;
        }

        @Override
        public ClassLoaderForClassArtifacts initValue() {
            return AccessController.doPrivileged(new PrivilegedAction<ClassLoaderForClassArtifacts>(){

                @Override
                public ClassLoaderForClassArtifacts run() {
                    return new ClassLoaderForClassArtifacts((Class)LazyClassLoaderRef.this.info.classRef.get());
                }
            });
        }
    }

    private static class LazyCachedClassRef
    extends LazyReference<CachedClass> {
        private final ClassInfo info;

        LazyCachedClassRef(ReferenceBundle bundle, ClassInfo info) {
            super(bundle);
            this.info = info;
        }

        @Override
        public CachedClass initValue() {
            return ClassInfo.createCachedClass((Class)this.info.classRef.get(), this.info);
        }
    }

    private static final class Sentinel {
        private Sentinel() {
        }
    }
}

